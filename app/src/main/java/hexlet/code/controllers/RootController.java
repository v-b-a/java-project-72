package hexlet.code.controllers;

import io.javalin.http.Handler;

public final class RootController {
    public static final Handler MAIN_PAGE = ctx -> ctx.render("index.html");
}
