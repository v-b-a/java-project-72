package hexlet.code.controllers;


import hexlet.code.models.Url;
import hexlet.code.models.query.QUrl;
import io.javalin.http.Handler;

import java.net.URL;

public final class UrlController {
    public static Handler addUrl = ctx -> {
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
//        if (urlIsAvailable(fullUrl)) {
//            ctx.sessionAttribute("flash", "Страница уже существует");
//            ctx.sessionAttribute("flash-type", "danger");
//            ctx.redirect("/");
//            return;
//        }
        String partOfUrl = url.getHost();
        Url newUrl = new Url(partOfUrl);
        newUrl.save();
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls");

    };
//    private static boolean urlIsAvailable(String url) {
//        Url url1 = new QUrl()
//                .name.equalTo(url)
//                .findOne();
//        return url1 != null;
//    }
    public static Handler getUrlsList = ctx -> {
        ctx.render("list.html");
    };
}
