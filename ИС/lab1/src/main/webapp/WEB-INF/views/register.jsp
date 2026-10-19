<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — Cinema Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header">
                <span class="logo-icon" style="display:inline-flex; align-items:center; justify-content:center; width:48px; height:48px; background:linear-gradient(135deg, #10b981, #059669); color:white; border-radius:12px; font-size:1.5rem; margin-bottom:12px;">✨</span>
                <h2>Create Account</h2>
                <p>Join Cinema Hub today</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert-error">
                    ${error}
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/register">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="Choose a username" required autofocus/>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Create a password" required/>
                </div>
                <button type="submit" class="btn btn-success" style="width: 100%; margin-top: 10px;">Create Account</button>
            </form>

            <div class="auth-footer">
                Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a>
            </div>
        </div>
    </div>
</body>
</html>
