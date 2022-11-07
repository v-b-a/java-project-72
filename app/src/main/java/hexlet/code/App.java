package hexlet.code;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) {
                config.enableDevLogging();
            }
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });


        //Создаём приложение, Включаем логгирование
//        Javalin app = Javalin.create(JavalinConfig::enableDevLogging);
        // Добавляем маршруты в приложение
        addRoutes(app);

        // Обработчик before запускается перед каждым запросом
        // Устанавливаем атрибут ctx для запросов
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");

        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.parseInt(port);
    }
    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }
    private static boolean isProduction() {
        return getMode().equals("production");

    }
    private static void addRoutes(Javalin app) {
        // Для GET-запроса на маршрут / будет выполняться
        app.get("/", ctx -> ctx.result("Hello World2"));

//        app.get("/about", RootController.about);
//
//        app.routes(() -> {
//            path("articles", () -> {
//                get(ArticleController.listArticles);
//                post(ArticleController.createArticle);
//                get("new", ArticleController.newArticle);
//                path("{id}", () -> {
//                    get(ArticleController.showArticle);
//                });
//            });
//        });
    }
}


