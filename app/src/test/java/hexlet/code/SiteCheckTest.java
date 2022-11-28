//package hexlet.code;
//
//import hexlet.code.domain.models.Url;
//import hexlet.code.models.query.QUrl;
//import io.ebean.DB;
//import io.ebean.Database;
//import io.javalin.Javalin;
//import kong.unirest.HttpResponse;
//import kong.unirest.Unirest;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.Test;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public final class SiteCheckTest {
//    private final int responseCode200 = 200;
//    private final int responseCode302 = 302;
//    private static MockWebServer server = new MockWebServer();
//
//
//    @Test
//    void testInit() {
//        assertThat(true).isEqualTo(true);
//    }
//
//    private static Javalin app;
//    private static String baseUrl;
//    private static Url url;
//    private static Database database;
//    private static String testUrl;
//
//    @BeforeAll
//    public static void beforeAll() throws IOException {
//        app = App.getApp();
//        app.start(0);
//        int port = app.port();
//        baseUrl = "http://localhost:" + port;
//        database = DB.getDefault();
//
//        MockResponse response = new MockResponse()
//                .setHeader("title", "some title")
//                .setBody("{ "
//                        + "<!DOCTYPE html>\n"
//                        + "<html>\n"
//                        + "<head>"
//                        + "<title>some title</title>"
//                        + "</head>"
//                        + "<body>\n"
//                        + "<h1>some h1</h1>\n"
//                        + "<p >My first paragraph.</p>\n"
//                        + "<meta\n"
//                        + "  name=\"description\"\n"
//                        + "  content=\"The MDN Web Docs Learning Area aims to provide\n"
//                        + "complete beginners to the Web with all they need to know to get\n"
//                        + "started with developing web sites and applications.\" />"
//                        + "</body>\n"
//                        + "</html>\n"
//                        + "<h1>some<h1>"
//                        +   "}");
//
//        testUrl = server.url("/").toString();
//        server.enqueue(response);
//        server.start();
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        app.stop();
//    }
//
//    @BeforeEach
//    void beforeEach() throws IOException {
////        database.script().run("/truncate.sql");
//        int url1 = new QUrl()
//                .id.between(1, Integer.MAX_VALUE)
//                .delete();
//
//    }
//    @AfterEach
//    public void afterEach() throws IOException {
//        server.shutdown();
//    }
//
//
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
//
//}
