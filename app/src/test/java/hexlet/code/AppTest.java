package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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
    private static Javalin app;
    private static String baseUrl;
    private static Url url;
    private static Database database;


    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

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
        int url1 = new QUrl()
                .id.between(1, Integer.MAX_VALUE)
                .delete();
    }

    @Test
    void testApp() {
        int port = app.port();
        assertThat(port).isNotNull();
    }

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

//    @Test
//    public void testCheck() throws Exception {
//        String titleTest = "<title>some title</title>";
//        String h1Test = "<h1>some h1</h1>";
//        // Create a MockWebServer.
//        MockWebServer server = new MockWebServer();
//
//        // Start the server.
//        server.start();
//
//        String testUrl = server.url("/").toString();
////        server.enqueue(new MockResponse().setBody("hello, world!"));
//        server.enqueue(new MockResponse().setResponseCode(200));
//        StringBuilder builder = new StringBuilder();
//        builder.append(titleTest);
//        builder.append(h1Test);
////        builder.append("<meta name=\"description\" content=\"The MDN Web Docs Learning\"/>");
//
//        server.enqueue(new MockResponse().setBody(String.valueOf(builder)));
//
//        HttpResponse responsePost = Unirest
//                .post(baseUrl + "/urls")
//                .field("url", testUrl)
//                .asEmpty();
//
//        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
//
//        Url dbUrl = new QUrl()
//                .id.equalTo(1)
//                .findOne();
//
//        HttpResponse responsePost2 = Unirest
//                .post(baseUrl + "/urls/1/checks")
//                .asString();
//
//        UrlCheck dbUrlCheck = new QUrlCheck()
//                .url.equalTo(dbUrl)
//                .findOne();
//        assertThat(dbUrlCheck).isNotNull();
//        assertThat(dbUrlCheck.getH1()).isEqualTo(h1Test);
//        assertThat(dbUrlCheck.getTitle()).isEqualTo(titleTest);
//    }

}
