<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: sans-serif; margin: 40px; background-color: #f4f4f9; }
        .container { max-width: 400px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin-top: 5px; box-sizing: border-box; }
        button { margin-top: 15px; padding: 10px; width: 100%; background: #2196F3; color: white; border: none; cursor: pointer; }
        button:hover { background: #1976D2; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Please Login</h2>

        <c:if test="${param.error != null}">
            <div style="color: red; margin-bottom: 15px;">Неверное имя пользователя или пароль!</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <p>
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required/>
            </p>
            <p>
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required/>
            </p>
            <button type="submit">Log In</button>
        </form>

        <p style="text-align: center; margin-top: 20px;">
            Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Зарегистрироваться</a>
        </p>
    </div>
</body>
</html>
