package hexlet.code.controllers;

import hexlet.code.models.Url;
import hexlet.code.models.UrlCheck;
import hexlet.code.models.query.QUrl;
import hexlet.code.models.query.QUrlCheck;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class CheckController {
    public static final Handler CHECK_URL = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        HttpResponse<String> response = Unirest
                .get("https://" + url.getName())
                .asString();
        String body = response.getBody();
        Document html = Jsoup.parse(body);
        String title = html.title();
        String h1 = "-";
        if (html.body().getElementsByTag("h1").first() != null) {
            h1 = html.body().getElementsByTag("h1").first().text();
        }
        String description = "-";
        if (html.body().getElementsByClass("description").first() != null) {
//            description = html.body().getElementsByClass("description").first().text();
            description = "some description";
        }

        UrlCheck urlCheck = new UrlCheck(response.getStatus(), title, h1,
                description, url);

        urlCheck.save();
        List<UrlCheck> checkList = new QUrlCheck()
                .url.equalTo(url)
                .orderBy()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("checkList", checkList);
        ctx.redirect("/urls/" + id);
    };
}
