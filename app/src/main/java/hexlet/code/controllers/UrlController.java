package hexlet.code.controllers;

import hexlet.code.UrlDto;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.model.query.QUrl;
import hexlet.code.model.query.QUrlCheck;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.List;

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
        List<Url> urlList = new QUrl()
                .setFirstRow(page * ROWS_PER_PAGE)
                .setMaxRows(ROWS_PER_PAGE)
                .orderBy("id")
                .findPagedList()
                .getList();

        List<UrlDto> urlDtos = urlList.stream()
                        .map(url -> {
                            UrlDto urlDto = new UrlDto();
                            urlDto.setUrlCheckList(url.getUrlCheckList());
                            urlDto.setId(url.getId());
                            urlDto.setName(url.getName());
                            urlDto.setCreatedAt(url.getCreatedAt());
                            return urlDto;
                        }).toList();
        ctx.attribute("urls", urlDtos);
        ctx.render("list.html");
    };

    public static final Handler SHOW_URL = ctx -> {
        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        Url url = new QUrl()
                .id.equalTo(id)
                .findOne();

        List<UrlCheck> checkList = new QUrlCheck()
                .id.equalTo(url.getId())
//                .url.equalTo(url)
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
            throw new NotFoundResponse();
        }
        try {
            HttpResponse<String> response = Unirest
                    .get(url.getName())
                    .asString();
            String body = response.getBody();
            Document html = Jsoup.parse(body);
            String title = html.title();

            Element h1Element = html.selectFirst("h1");
            String h1 = h1Element == null ? "" : h1Element.text();

            Element descriptionElement = html.selectFirst("meta[name=description]");
            String description = descriptionElement == null ? "" : descriptionElement.attr("content");

            UrlCheck urlCheck = new UrlCheck(response.getStatus(), title, h1,
                    description, url);
            urlCheck.save();
            url.getUrlCheckList().add(urlCheck);
            url.save();
            List<UrlCheck> checkList = new QUrlCheck()
                    .id.equalTo(url.getId())
                    .orderBy()
                    .findList();

            ctx.attribute("url", url);
            ctx.attribute("checkList", checkList);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls/" + url.getId());
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }
    };

}
