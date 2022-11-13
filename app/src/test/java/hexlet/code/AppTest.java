package hexlet.code;

import hexlet.code.models.Url;
import hexlet.code.models.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private final int responseCode200 = 200;
    private final int responseCode302 = 302;
    static MockWebServer server = new MockWebServer();


    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    private static Javalin app;
    private static String baseUrl;
    private static Url url;
    private static Database database;
    private static String testUrl;

    @BeforeAll
    public static void beforeAll() {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        MockResponse response = new MockResponse()
                .setHeader("title", "some title")
                .setBody("{ " +
                        "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>"
                        + "<title>some title</title>"
                        + "</head>"
                        + "<body>\n"
                        + "\n"
                        + "<h1>some h1</h1>\n"
                        + "\n"
                        + "<p >My first paragraph.</p>\n"
                        + "<meta\n"
                        + "  name=\"description\"\n"
                        + "  content=\"The MDN Web Docs Learning Area aims to provide\n"
                        + "complete beginners to the Web with all they need to know to get\n"
                        + "started with developing web sites and applications.\" />"
                        + "</body>\n"
                        + "</html>\n"
                        + "<h1>some<h1>"
                        +   "}");

        testUrl = server.url("/").toString();
        server.enqueue(response);
//        server.start();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() throws IOException {
//        database.script().run("/truncate.sql");
        int url1 = new QUrl()
                .id.between(1,Integer.MAX_VALUE)
                .delete();

    }
    @AfterEach
    public void afterEach() throws IOException {
        server.shutdown();
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
            String splitUrl = "www.google.ru";
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
            assertThat(body).contains(splitUrl);
            assertThat(body).contains("Страница успешно добавлена");

            Url url1 = new QUrl()
                    .name.equalTo(splitUrl)
                    .findOne();

            assertThat(url1).isNotNull();
            assertThat(url1.getName()).isEqualTo(splitUrl);
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
//    @Test
//    void addCheckTest() throws MalformedURLException {
//
//        HttpResponse responsePost = Unirest
//                .post(baseUrl + "/urls")
//                .field("url", testUrl)
//                .asEmpty();
//        String host = new URL(testUrl).getHost();
//        int port = new URL(testUrl).getPort();
//
//        Url url1 = new QUrl()
//                .name.equalTo(host)
//                .findOne();
//        assert url1 != null;
//        long urlId = url1.getId();
//        HttpResponse responsePost2 = Unirest
//                .post(baseUrl + "/urls/" + urlId +"/checks")
//                .field("url", url1)
//                .asEmpty();
//
//        UrlCheck urlCheck = new QUrlCheck()
//                .url.equalTo(url1)
//                .findOne();
//        assertThat(urlCheck).isNotNull();
//        assertThat(urlCheck.getUrl()).isEqualTo(url1);
//        assertThat(urlCheck.getTitle()).isEqualTo("some title");
//        assertThat(urlCheck.getH1()).isEqualTo("some h1");
//    }

}
