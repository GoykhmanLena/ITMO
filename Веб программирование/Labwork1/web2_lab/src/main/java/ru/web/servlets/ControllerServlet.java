package ru.web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;


@WebServlet("/controller") // ControllerServlet будет обрабатывать все запросы
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Запросы по GET всегда перенаправляем на страницу с формой (index.jsp)
        // Устанавливаем тип контента перед форвардом
        response.setContentType("text/html;charset=UTF-8");
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Устанавливаем кодировку для запроса
        request.setCharacterEncoding("UTF-8");
        // Устанавливаем тип контента и кодировку для ответа до форварда
        response.setContentType("text/html;charset=UTF-8");

        // Проверяем, содержат ли параметры координаты X, Y и R
        if (request.getParameter("x") != null && request.getParameter("y") != null && request.getParameter("r") != null) {
            // Если параметры есть, делегируем запрос AreaCheckServlet
            request.getRequestDispatcher("/checker").forward(request, response);
        } else {
            // Если параметров нет (или их не хватает), перенаправляем на страницу с формой
            // Можно добавить сообщение об ошибке, если это POST-запрос без параметров
            request.setAttribute("errorMessage", "Missing X, Y, or R parameters in POST request.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}