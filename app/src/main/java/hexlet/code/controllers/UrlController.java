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
import java.util.Objects;

public final class UrlController {
    private static final int ROWS_PER_PAGE = 10;
    public static final Handler ADD_URL = ctx -> {
        String fullUrl = ctx.formParam("url");
        URL url;
        try {
            url = new URL(fullUrl);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        String normalizedUrl = url.getProtocol() + "://" + url.getAuthority();
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
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        PagedList<Url> urlList = new QUrl()
                .setFirstRow(page * ROWS_PER_PAGE)
                .setMaxRows(ROWS_PER_PAGE)
                .orderBy()
                .id.asc()
                .findPagedList();
        List<Url> urlList1 = urlList.getList();
        ctx.attribute("urls", urlList1);
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
        long urlId = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        Url url = new QUrl()
                .id.equalTo(urlId)
                .findOne();
        if (url == null) {
            ctx.redirect("/");
        }

        HttpResponse<String> response = Unirest
                .get(url.getName())
                .asString();
        String body = response.getBody();
        Document html = Jsoup.parse(body);
        String title = html.title();
        String h1 = Objects.requireNonNull(html.body()
                .getElementsByTag("h1")
                .first()).text();
        String description = html.selectFirst("meta[name=description]") != null
                ? Objects.requireNonNull(html.selectFirst("meta[name=description]")).attr("content")
                : "-";

        UrlCheck urlCheck = new UrlCheck(response.getStatus(), title, h1,
                description, url);

        urlCheck.save();
        List<UrlCheck> checkList = new QUrlCheck()
                .url.equalTo(url)
                .orderBy()
                .findList();

        ctx.attribute("url", url);
        ctx.attribute("checkList", checkList);
        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + urlId);
    };

}
