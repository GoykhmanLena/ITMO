<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Special Operations</title>
    <style>
        .operation-card { border: 1px solid #ccc; padding: 15px; margin-bottom: 15px; background: #f9f9f9; }
        .operation-card h3 { margin-top: 0; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 6px; }
        th { background: #eee; }
    </style>
</head>
<body>
    <h1>Special Operations</h1>

    <div style="background: #eef; padding: 10px; margin-bottom: 20px; border: 1px solid #ccd;">
        <b>Navigation:</b>
        <a href="${pageContext.request.contextPath}/movies" style="margin-right: 15px;">Movies</a>
        <a href="${pageContext.request.contextPath}/persons" style="margin-right: 15px;">Persons</a>
        <a href="${pageContext.request.contextPath}/special" style="margin-right: 15px;">Special Operations</a>
    </div>

    <hr>

    <div class="operation-card">
        <h3>1. Delete movies by genre</h3>
        <form method="post" action="${pageContext.request.contextPath}/special/delete-by-genre">
            <select name="genre" required>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}">${g}</option>
                </c:forEach>
            </select>
            <button type="submit" onclick="return confirm('Are you sure you want to delete all movies of this genre?')">Delete</button>
        </form>
    </div>

    <div class="operation-card">
        <h3>2. Find movies by name prefix</h3>
        <form method="get" action="${pageContext.request.contextPath}/special/search-by-name">
            <input type="text" name="prefix" required placeholder="Enter prefix...">
            <button type="submit">Search</button>
        </form>
    </div>

    <div class="operation-card">
        <h3>3. Find movies by genre less than</h3>
        <form method="get" action="${pageContext.request.contextPath}/special/genre-less-than">
            <select name="genre" required>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}">${g}</option>
                </c:forEach>
            </select>
            <button type="submit">Search</button>
        </form>
    </div>

    <div class="operation-card">
        <h3>4. Find directors without Oscars</h3>
        <form method="get" action="${pageContext.request.contextPath}/special/directors-no-oscars">
            <button type="submit">Find Directors</button>
        </form>
    </div>

    <div class="operation-card">
        <h3>5. Redistribute Oscars</h3>
        <form method="post" action="${pageContext.request.contextPath}/special/redistribute-oscars">
            From:
            <select name="from" required>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}">${g}</option>
                </c:forEach>
            </select>
            To:
            <select name="to" required>
                <c:forEach var="g" items="${genres}">
                    <option value="${g}">${g}</option>
                </c:forEach>
            </select>
            <button type="submit">Redistribute</button>
        </form>
    </div>

    <!-- Область вывода результатов для операций 2 и 3 (Фильмы) -->
    <c:if test="${not empty moviesResult}">
        <h2>Result (Movies):</h2>
        <table>
            <tr><th>ID</th><th>Name</th><th>Genre</th><th>Oscars</th></tr>
            <c:forEach var="m" items="${moviesResult}">
                <tr><td>${m.id}</td><td>${m.name}</td><td>${m.genre}</td><td>${m.oscarsCount}</td></tr>
            </c:forEach>
        </table>
    </c:if>

    <!-- Область вывода результатов для операции 4 (Люди/Режиссеры) -->
    <c:if test="${not empty personsResult}">
        <h2>Result (Directors):</h2>
        <table>
            <tr><th>ID</th><th>Name</th><th>Eye Color</th><th>Location</th></tr>
            <c:forEach var="p" items="${personsResult}">
                <tr><td>${p.id}</td><td>${p.name}</td><td>${p.eyeColor}</td><td>${p.location.name}</td></tr>
            </c:forEach>
        </table>
    </c:if>

</body>
</html>
