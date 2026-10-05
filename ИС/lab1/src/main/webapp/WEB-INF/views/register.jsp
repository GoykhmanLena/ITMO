<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
    <style>
        body { font-family: sans-serif; margin: 40px; background-color: #f4f4f9; }
        .container { max-width: 400px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .error { color: red; margin-bottom: 15px; }
        input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin-top: 5px; box-sizing: border-box; }
        button { margin-top: 15px; padding: 10px; width: 100%; background: #4CAF50; color: white; border: none; cursor: pointer; }
        button:hover { background: #45a049; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Register New User</h2>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <p>
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" required/>
            </p>
            <p>
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required/>
            </p>
            <button type="submit">Register</button>
        </form>

        <p style="text-align: center; margin-top: 20px;">
            Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a>
        </p>
    </div>
</body>
</html>
