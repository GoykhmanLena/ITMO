package ru.web.utils;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ErrorUtil {

    public static void sendError(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        var json = new Gson();
        Map<String, Object> jsonResponse = new HashMap<>() {{
            put("error", errorMessage);
            put("status", "Error");
            put("timestamp", System.currentTimeMillis()); // Добавим временную метку для отладки
        }};

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // Устанавливаем кодировку
        response.getWriter().write(json.toJson(jsonResponse));
        response.setStatus(statusCode);
        response.getWriter().flush(); // Убеждаемся, что все отправлено
    }
}