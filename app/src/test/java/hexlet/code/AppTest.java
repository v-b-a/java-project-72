package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;


import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private final int responseCode200 = 200;
    private final int responseCode302 = 302;

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
        database.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        database.rollbackTransaction();
    }

    @Test
    void testApp() {
        int port = app.port();
        assertThat(port).isNotNull();
    }

    @Nested
    class UrlTest {
        @Test
        void testIndex() {
            HttpResponse<String> response = Unirest.get(baseUrl).asString();
            assertThat(response.getStatus()).isEqualTo(responseCode200);
            assertThat(response.getBody()).contains("Анализатор web-сайтов");
        }

        @Test
        void testUrlsList() {
            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseCode200);
            assertThat(body).contains("Список проверок");
        }

        @Test
        void testCreate() {
            String inputUrl = "https://www.google.ru/webhp?authuser=1";
            String normalizedUrl = "https://www.google.ru";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", inputUrl)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
            assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

            HttpResponse<String> response = Unirest
                    .get(baseUrl + "/urls")
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseCode200);
            Url dbUrl = new QUrl()
                    .name.equalTo(normalizedUrl)
                    .findOne();
            assertThat(dbUrl).isNotNull();
            assertThat(dbUrl.getName()).isEqualTo(normalizedUrl);
            assertThat(body).containsIgnoringCase("Страница успешно добавлена");
        }

        @Test
        void testIncorrectUrl() {
            String incorrectValue1 = "tree";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", incorrectValue1)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
            HttpResponse<String> response = Unirest
                    .get(baseUrl)
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseCode200);
            assertThat(body).contains("Некорректный URL");
        }

        @Test
        void testNullUrl() {
            String incorrectValue1 = null;
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", incorrectValue1)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
            HttpResponse<String> response = Unirest
                    .get(baseUrl)
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseCode200);
            assertThat(body).contains("Некорректный URL");
        }

        @Test
        void repeatUrl() {
            String correctValue = "https://www.google.ru/webhp?authuser=1";
            HttpResponse responsePost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", correctValue)
                    .asEmpty();

            assertThat(responsePost.getStatus()).isEqualTo(responseCode302);

            HttpResponse repeatPost = Unirest
                    .post(baseUrl + "/urls")
                    .field("url", correctValue)
                    .asEmpty();

            assertThat(repeatPost.getStatus()).isEqualTo(responseCode302);

            HttpResponse<String> response = Unirest
                    .get(baseUrl)
                    .asString();
            String body = response.getBody();

            assertThat(response.getStatus()).isEqualTo(responseCode200);
            assertThat(body).contains("Этот сайт уже существует");
        }
    }

}
