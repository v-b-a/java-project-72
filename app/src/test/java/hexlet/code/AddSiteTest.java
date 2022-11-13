package hexlet.code;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public final class AddSiteTest {
    static MockWebServer server = new MockWebServer();
    @BeforeAll
    public static void beforeAll() {
        MockResponse response = new MockResponse()
                .setHeader("title", "some title")
                .setBody("{ " +
                        "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>" +
                        "<title>some title</title>" +
                        "</head>" +
                        "<body>\n" +
                        "\n" +
                        "<h1>some h1</h1>\n" +
                        "\n" +
                        "<p >My first paragraph.</p>\n" +
                        "<meta\n" +
                        "  name=\"description\"\n" +
                        "  content=\"The MDN Web Docs Learning Area aims to provide\n" +
                        "complete beginners to the Web with all they need to know to get\n" +
                        "started with developing web sites and applications.\" />" +
                        "</body>\n" +
                        "</html>\n" +
                        "<h1>some<h1>" +
                        "}");

        String url = server.url("/").toString();
        server.enqueue(response);
    }

    @BeforeEach
    public static void beforeEach() throws IOException {
        server.start();
    }
    @AfterEach
    public static void afterEach() throws IOException {
        server.shutdown();
    }
//
//    @Test
//    public static void

}