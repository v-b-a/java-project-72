package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private final int responseCode200 = 200;
    private final int responseCode302 = 302;
    private static Javalin app;
    private static String baseUrl;
    private final String verifiedUrl = "https://www.google.ru/webhp?authuser=1";
    private final String normalizedUrl = "https://www.google.ru";
    private static final String TITLE_TEST = "<title>some title</title>";
    private static final String H1_TEST = "<h1>some h1</h1>";
    private static final String DESCRIPTION_TEST = "<meta name=\"description\" content=\"some description\">";
    private static String testUrl;
    private static MockWebServer mockServer;
    private Transaction transaction;

    @Test
    void testInit() {
        assertThat(true).isEqualTo(true);
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        mockServer = new MockWebServer();
        MockResponse content = new MockResponse();
        content.setBody(TITLE_TEST + DESCRIPTION_TEST + H1_TEST);

        mockServer.enqueue(content);
        mockServer.start();
        testUrl = mockServer.url("/").toString();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    @BeforeEach
    void beforeEach() {
        transaction = DB.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
        int url1 = new QUrl()
                .id.between(1, Integer.MAX_VALUE)
                .delete();
        int urlCheck = new QUrlCheck()
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
    void testShowUrls() {
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", verifiedUrl)
                .asEmpty();
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("Список проверок");
        assertThat(body).contains(normalizedUrl);
    }

    @Test
    void testShowUrl() {
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", verifiedUrl)
                .asEmpty();
        Url dbUrl = new QUrl()
                .name.equalTo(normalizedUrl)
                .findOne();
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/" + dbUrl.getId())
                .asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("Проверка");
        assertThat(body).contains(normalizedUrl);
    }

    @Test
    void testCreate() {
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", verifiedUrl)
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
    void testRepeatCreate() {
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", verifiedUrl)
                .asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse responsePost2 = Unirest
                .post(baseUrl + "/urls")
                .field("url", verifiedUrl)
                .asEmpty();
        assertThat(responsePost2.getStatus()).isEqualTo(responseCode302);
        assertThat(responsePost2.getHeaders().getFirst("Location")).isEqualTo("/");

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();
        assertThat(body).containsIgnoringCase("Этот сайт уже существует");
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
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", (Object) null)
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
    public void testCheck() throws Exception {
        HttpResponse addCheckListPost = Unirest
                .post(baseUrl + "/urls")
                .field("url", testUrl)
                .asEmpty();

        URL url = new URL(testUrl);
                Url dbUrl = new QUrl()
                .name.equalTo(url.getProtocol() + "://" + url.getAuthority())
                .findOne();
        assertThat(dbUrl).isNotNull();
        assertThat(dbUrl.getName()).isEqualTo(url.getProtocol() + "://" + url.getAuthority());
        HttpResponse checkUrl = Unirest
                .post(baseUrl + "/urls/" + dbUrl.getId() + "/checks")
                .asEmpty();

        HttpResponse<String> responseChecks = Unirest
                .get(baseUrl + "/urls/" + dbUrl.getId())
                .asString();

        assertThat(responseChecks.getStatus()).isEqualTo(responseCode200);
        assertThat(responseChecks.getBody()).contains("Страница успешно проверена");


        UrlCheck dbUrlCheck = new QUrlCheck()
                .url.equalTo(dbUrl)
                .findOne();
        assertThat(dbUrlCheck.getStatusCode()).isEqualTo(responseCode200);
        assertThat(dbUrlCheck).isNotNull();
        assertThat(dbUrlCheck.getH1()).isEqualTo("some h1");
        assertThat(dbUrlCheck.getTitle()).isEqualTo("some title");
        assertThat(dbUrlCheck.getDescription()).isEqualTo("some description");
    }

}



