package ru.web.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.web.models.PointResult;
import ru.web.utils.AreaChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet("/checker")
public class AreaCheckServlet extends HttpServlet {
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    public static final int SC_INTERNAL_SERVER_ERROR = 500;

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        if (context.getAttribute("results") == null) {
            context.setAttribute("results", Collections.synchronizedList(new ArrayList<PointResult>()));
        }
        System.out.println("AreaCheckServlet initialized. Results list in ServletContext.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Устанавливаем кодировку для запроса
        request.setCharacterEncoding("UTF-8");
        // Устанавливаем тип контента и кодировку для ответа до форварда
        response.setContentType("text/html;charset=UTF-8");

        double x;
        double y;
        double r;

        try {
            x = Double.parseDouble(request.getParameter("x"));
            y = Double.parseDouble(request.getParameter("y"));
            r = Double.parseDouble(request.getParameter("r"));

            if (!validate(x, y, r)) {
                System.out.println("Validation failed for parameters: x=" + x + ", y=" + y + ", r=" + r);
                request.setAttribute("errorMessage", "Invalid input data. Please check X, Y, and R values.");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }

            boolean isHit = AreaChecker.check(x, y, r);
            PointResult pointResult = new PointResult(x, y, r, isHit);

            // Получаем список результатов из контекста приложения
            ServletContext context = getServletContext();
            List<PointResult> results = (List<PointResult>) context.getAttribute("results");
            System.out.println("previous results " + results);

            if (results == null) {  // <- на всякий случай
                results = Collections.synchronizedList(new ArrayList<PointResult>());
                context.setAttribute("results", results);
            }

            System.out.println("now result: " + pointResult);
            // Добавляем новый результат в список
            results.add(0, pointResult);
            System.out.println("Results from context (after adding): " + results.size());


            // Устанавливаем текущий результат как атрибут запроса для отображения на resultPage.jsp
            request.setAttribute("currentPointResult", pointResult);

            request.setAttribute("allResults", results); // Теперь table.jsp сможет
            // Перенаправляем на JSP страницу для отображения результатов
            System.out.println("Forwarding to resultPage.jsp with currentPointResult: " + request.getAttribute("currentPointResult"));
            System.out.println("Forwarding to resultPage.jsp with allResults size: " + ((List<PointResult>)request.getAttribute("allResults")).size());

            request.getRequestDispatcher("/resultPage.jsp").forward(request, response);

        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Error parsing parameters: " + e.getMessage());
            request.setAttribute("errorMessage", "Invalid number format for X, Y, or R. Please enter numeric values.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An internal server error occurred: " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }

    // Метод валидации остается таким же, как в предыдущем ответе
    public boolean validate(double x, double y, double r) {
       // List<Double> validXValues = Arrays.asList(-4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0);
        boolean flag = true;

      //  if (!validXValues.contains(x)) {
        //    System.out.println("X validation failed: " + x);
          //  flag = false;
        //}
        if (x < -4 || x > 4){
            System.out.println("X validation failed: " + y);
            flag = false;
        }
        // В задании для Y сказано (-3 ... 5), это широкий диапазон, а не дискретные значения
        if (y < -3.0 || y > 5.0) {
            System.out.println("Y validation failed: " + y);
            flag = false;
        }

        List<Double> validRValues = Arrays.asList(1.0, 1.5, 2.0, 2.5, 3.0);
        if (!validRValues.contains(r)) {
            System.out.println("R validation failed: " + r);
            flag = false;
        }
        return flag;
    }
}