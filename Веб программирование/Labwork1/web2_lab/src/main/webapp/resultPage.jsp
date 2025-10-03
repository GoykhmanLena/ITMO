<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Результаты проверки</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="icon" href="${pageContext.request.contextPath}/styles/favicon.png">
    <%-- Добавляем скрипты для графика --%>
    <script defer src="${pageContext.request.contextPath}/scripts/index.js"></script>
    <script defer src="${pageContext.request.contextPath}/scripts/main.js"></script>
</head>
<body>
<header class="header">
    <h2>Результаты проверки точки</h2>
</header>

<main class="container">
    <div class="result-details">
        <h3>Текущий результат:</h3>
        <core:if test="${not empty currentPointResult}">
            <table>
                <tr><th>Параметр</th><th>Значение</th></tr>
                <tr><td>X</td><td><fmt:formatNumber value="${currentPointResult.getX()}" pattern="0.##"/></td></tr>
                <tr><td>Y</td><td><fmt:formatNumber value="${currentPointResult.y}" pattern="0.##"/></td></tr>
                <tr><td>R</td><td><fmt:formatNumber value="${currentPointResult.r}" pattern="0.##"/></td></tr>
                <tr><td>Результат</td><td>${currentPointResult.getIsHit() ? "Попадание" : "Промах"}</td></tr>
                <tr><td>Время</td><td><fmt:formatDate value="${currentPointResult.timestamp}" pattern="HH:mm:ss dd.MM.yyyy"/></td></tr>
            </table>
            <%-- ДОБАВЛЯЕМ СЮДА ЭЛЕМЕНТ IMG --%>
            <img id="resultImage" class="result-image" src="" alt="Результат проверки">
        </core:if>
        <core:if test="${empty currentPointResult}">
            <p>Нет данных о текущей проверке.</p>
            <%-- Если данных нет, можно показать дефолтную картинку или ничего --%>
            <img id="resultImage" class="result-image" src="" alt="Нет данных">
        </core:if>
    </div>

    <div class="graph"> <%-- Добавляем div для графика --%>
        <jsp:include page="graph.jsp"/>
    </div>

    <div class="table">
        <h3>Все предыдущие результаты:</h3>
        <table class="resultTable">
            <thead>
            <tr>
                <th>x</th>
                <th>y</th>
                <th>r</th>
                <th>result</th>
                <th>time</th>
            </tr>
            </thead>
            <tbody>
            <%-- table.jsp теперь включается в body resultPage.jsp --%>
            <jsp:include page="table.jsp"/>
            </tbody>
        </table>
    </div>

    <div class="navigation">
        <p><a href="${pageContext.request.contextPath}">Вернуться на главную страницу</a></p>
    </div>
</main>

<footer class="footer">
    © lenchik
</footer>
</body>
</html>