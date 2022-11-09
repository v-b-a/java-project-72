package hexlet.code;

import hexlet.code.models.Url;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url url;
    private static Database database;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();
    }
    @AfterAll
    public static void afterAll() {
        app.stop();
    }
    @BeforeEach
    void beforeEach() {
        database.script().run("/truncate.sql");
        database.script().run("/seed-test-db.sql");
    }
    @Test
    void testCreate() {
        String inputUrl = "https://youtube.com/";
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("name", inputUrl)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(302);

    }
}
