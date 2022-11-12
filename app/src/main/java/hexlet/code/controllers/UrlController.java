package hexlet.code.controllers;


import hexlet.code.models.Url;
import hexlet.code.models.query.QUrl;
import io.javalin.http.Handler;

import java.net.URL;
import java.util.List;

public final class UrlController {
    public static final Handler ADD_URL = ctx -> {
        String fullUrl = ctx.formParam("url");
        URL url;
        try {
            assert fullUrl != null;
            url = new URL(fullUrl);

        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
            return;
        }
        String partOfUrl = url.getHost();
        Url newUrl = new Url(partOfUrl);
        if (urlIsAvailable(partOfUrl)) {
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
        List<Url> urlList = new QUrl()
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
        ctx.attribute("url", url);
        ctx.render("show.html");
    };
}
