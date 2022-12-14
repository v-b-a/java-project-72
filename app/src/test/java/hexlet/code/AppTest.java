package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public final class AppTest {
    private final int responseCode200 = 200;
    private final int responseCode302 = 302;
    private static Javalin app;
    private static String baseUrl;
    private final String verifiedUrl = "https://www.google.ru/webhp?authuser=1";
    private final String normalizedUrl = "https://www.google.ru";
    private static String htmlContent;
    private static String testUrl;
    private static MockWebServer mockServer;
    private Transaction transaction;
    private static Database database;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        mockServer = new MockWebServer();
        MockResponse content = new MockResponse();
        htmlContent = Files.readString(Paths.get("src/test/resources/fixtures.html"));

        content.setBody(htmlContent);
        mockServer.enqueue(content);
        mockServer.start();
        testUrl = mockServer.url("/").toString();
        database = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        app.stop();
        mockServer.shutdown();
    }

    @AfterEach
    void afterEach() {
        database.script().run("/truncate.sql");
    }


    @Test
    void testApp() {
        int port = app.port();
        assertThat(port).isNotNull();
    }


    @Test
    void testShowUrls() {
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", verifiedUrl).asEmpty();
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("???????????? ????????????????");
        assertThat(body).contains(normalizedUrl);
    }

    @Test
    void testShowUrl() {
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", verifiedUrl).asEmpty();
        Url dbUrl = new QUrl().name.equalTo(normalizedUrl).findOne();
        HttpResponse<String> response = Unirest.get(baseUrl + "/urls/" + dbUrl.getId()).asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("????????????????");
        assertThat(body).contains(normalizedUrl);
    }

    @Test
    void testCreate() {
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", verifiedUrl).asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls")
                .asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        Url dbUrl = new QUrl().name.equalTo(normalizedUrl).findOne();
        assertThat(dbUrl).isNotNull();
        assertThat(dbUrl.getName()).isEqualTo(normalizedUrl);
        assertThat(body).containsIgnoringCase("???????????????? ?????????????? ??????????????????");

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains(normalizedUrl);
    }

    @Test
    void testRepeatCreate() {
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", verifiedUrl).asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
        assertThat(responsePost.getHeaders().getFirst("Location")).isEqualTo("/urls");

        HttpResponse responsePost2 = Unirest.post(baseUrl + "/urls").field("url", verifiedUrl).asEmpty();
        assertThat(responsePost2.getStatus()).isEqualTo(responseCode302);
        assertThat(responsePost2.getHeaders().getFirst("Location")).isEqualTo("/");

        HttpResponse<String> response = Unirest.get(baseUrl + "/urls").asString();
        String body = response.getBody();
        assertThat(body).containsIgnoringCase("???????? ???????? ?????? ????????????????????");
    }

    @Test
    void testIncorrectUrl() {
        String incorrectValue1 = "tree";
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", incorrectValue1).asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("???????????????????????? URL");
    }

    @Test
    void testNullUrl() {
        HttpResponse responsePost = Unirest.post(baseUrl + "/urls").field("url", (Object) null).asEmpty();

        assertThat(responsePost.getStatus()).isEqualTo(responseCode302);
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        String body = response.getBody();

        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(body).contains("???????????????????????? URL");
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

        HttpResponse<String> responseChecks = Unirest.get(baseUrl + "/urls/" + dbUrl.getId()).asString();

        assertThat(responseChecks.getStatus()).isEqualTo(responseCode200);
        assertThat(responseChecks.getBody()).contains("???????????????? ?????????????? ??????????????????");


        UrlCheck dbUrlCheck = new QUrlCheck()
                .url.equalTo(dbUrl)
                .findOne();
        assertThat(dbUrlCheck.getStatusCode()).isEqualTo(responseCode200);
        assertThat(dbUrlCheck).isNotNull();
        assertThat(dbUrlCheck.getH1()).isEqualTo("some h1");
        assertThat(dbUrlCheck.getTitle()).isEqualTo("some title");
        assertThat(dbUrlCheck.getDescription()).isEqualTo("some description");
    }

    @Test
    void testIndex2() {
        HttpResponse<String> response = Unirest.get(baseUrl).asString();
        assertThat(response.getStatus()).isEqualTo(responseCode200);
        assertThat(response.getBody()).contains("???????????????????? ??????????????");
    }
}



