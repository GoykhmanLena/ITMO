<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Web lab 1</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="icon" href="${pageContext.request.contextPath}/styles/favicon.png">
    <%-- index.js должен загружаться ДО main.js, чтобы getR() был доступен --%>
    <script defer src="${pageContext.request.contextPath}/scripts/index.js"></script>
    <script defer src="${pageContext.request.contextPath}/scripts/main.js"></script> <%-- Изменено: defer --%>
</head>
<body>
<header class="header">
    <h2>Выполнил: Гойхман Елена</h2>
    <h2>Группа: P3212</h2>
    <h2>Вариант: 467170</h2>
</header>

<main class="container">
    <div class="graph">
        <jsp:include page="graph.jsp"/>
    </div>
    <div class="form" id="input-form">
        <jsp:include page="inputForm.jsp"/>
        <div id="error" class="error-message" <core:if test="${empty errorMessage}">hidden</core:if>>
            <core:out value="${errorMessage}"/>
        </div>
    </div>

    <div class="table">
        <table class="resultTable" id="result">
            <thead>
            <tr>
                <th>x</th>
                <th>y</th>
                <th>r</th>
                <th>result</th>
                <th>time</th>
            </tr>
            </thead>
            <tbody id="output">
            <jsp:include page="table.jsp"/>
            </tbody>
        </table>
    </div>
</main>

<footer class="footer">
    © lenchik
</footer>
</body>
</html>