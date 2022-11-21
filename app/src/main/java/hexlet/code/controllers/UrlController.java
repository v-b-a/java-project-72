package hexlet.code.controllers;


import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.models.query.QUrl;
import hexlet.code.models.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.URL;
import java.util.List;

public final class UrlController {
    public static final Handler ADD_URL = ctx -> {
        String fullUrl = ctx.formParam("url");
        URL url;
        if (fullUrl == null) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        url = new URL(fullUrl);
        String normalizedUrl = url.getProtocol() + "://" + url.getHost() + ":" +url.getPort();
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
        PagedList<Url> urlList = new QUrl()
                .setFirstRow(page * rowsPerPage)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();
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
}
