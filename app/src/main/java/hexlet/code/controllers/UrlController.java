package hexlet.code.controllers;


import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.List;

public final class UrlController {
    public static final Handler ADD_URL = ctx -> {
        String fullUrl = ctx.formParam("url");
        URL url;
        try {
            if (fullUrl == null) {
                throw new NullPointerException();
            }
            url = new URL(fullUrl);

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        String normalizedUrl = url.getProtocol() + "://" + url.getHost();
        Url newUrl = new Url(normalizedUrl);
        if (urlIsAvailable(normalizedUrl)) {
            ctx.sessionAttribute("flash", "Этот сайт уже существует");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");

    };

    private static boolean urlIsAvailable(String url) {
        int url1 = new QUrl()
                .name.equalTo(url)
                .findCount();
        return url1 != 0;
    }

    public static final Handler GET_URLS_LIST = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        List<Url> urlList = new QUrl()
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findList();
        ctx.attribute("urls", urlList);
        ctx.render("list.html");
    };
    public static final Handler SHOW_URL = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        List<UrlCheck> checkList = new QUrlCheck()
                .url.equalTo(url)
                .orderBy()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("checkList", checkList);

        ctx.render("show.html");
    };
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
        if (html.body().getElementsByClass("meta").attr("description") != null) {
            description = html.body().getElementsByClass("meta").attr("description");
//            description = "some description";
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
