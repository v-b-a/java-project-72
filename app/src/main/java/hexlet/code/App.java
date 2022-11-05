package hexlet.code;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
    public static Javalin getApp() {
        //Создаём приложение, Включаем логгирование
        Javalin app = Javalin.create(JavalinConfig::enableDevLogging);
        // Добавляем маршруты в приложение
        addRoutes(app);

        // Обработчик before запускается перед каждым запросом
        // Устанавливаем атрибут ctx для запросов
        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });
        return app;
    }
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5000");
        return Integer.parseInt(port);
    }
    private static void addRoutes(Javalin app) {
        // Для GET-запроса на маршрут / будет выполняться
        // обработчик welcome в контроллере RootController
        app.get("/", ctx -> ctx.result("Hello World2"));
    }
}


