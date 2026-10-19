<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — Cinema Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header">
                <span class="logo-icon" style="display:inline-flex; align-items:center; justify-content:center; width:48px; height:48px; background:linear-gradient(135deg, #6366f1, #4f46e5); color:white; border-radius:12px; font-size:1.5rem; margin-bottom:12px;">🎬</span>
                <h2>Welcome Back</h2>
                <p>Sign in to your Cinema Hub account</p>
            </div>

            <c:if test="${param.error != null}">
                <div class="alert-error">
                    Неверное имя пользователя или пароль!
                </div>
            </c:if>

            <c:if test="${param.logout != null}">
                <div style="background-color: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; padding: 12px; border-radius: 6px; margin-bottom: 20px; font-size: 0.9rem;">
                    Вы успешно вышли из системы.
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="Enter your username" required autofocus/>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Enter your password" required/>
                </div>
                <button type="submit" class="btn btn-primary" style="width: 100%; margin-top: 10px;">Sign In</button>
            </form>

            <div class="auth-footer">
                Don't have an account? <a href="${pageContext.request.contextPath}/register">Create one here</a>
            </div>
        </div>
    </div>
</body>
</html>
