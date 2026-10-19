<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${person.name} — Cinema Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <header class="navbar">
        <div class="navbar-container">
            <a href="${pageContext.request.contextPath}/movies" class="navbar-brand">
                <span class="logo-icon">🎬</span>
                <span>Cinema Hub</span>
            </a>
            <nav class="navbar-nav">
                <a href="${pageContext.request.contextPath}/movies" class="nav-link">Movies</a>
                <a href="${pageContext.request.contextPath}/persons" class="nav-link active">Persons</a>
                <a href="${pageContext.request.contextPath}/special" class="nav-link">Special Operations</a>
            </nav>
            <div class="navbar-actions">
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Log out</a>
            </div>
        </div>
    </header>

    <main class="main-container">
        <div class="page-header">
            <div>
                <h1 class="page-title">${person.name}</h1>
                <p style="color: var(--text-muted); margin-top: 4px;">Person ID: ${person.id}</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/persons/${person.id}/edit" class="btn btn-primary">Edit Person</a>
                <a href="javascript:history.back()" class="btn btn-secondary">&larr; Back</a>
                <a href="${pageContext.request.contextPath}/persons" class="btn btn-secondary">Persons List</a>
            </div>
        </div>

        <div class="card">
            <h3 style="margin-bottom: 20px; font-size: 1.2rem; color: var(--text-main);">Person Details</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${person.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Name</div>
                    <div class="detail-value">${person.name}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Eye Color</div>
                    <div class="detail-value"><span class="badge" style="background:#e0f2fe; color:#0369a1;">${person.eyeColor}</span></div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Hair Color</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${not empty person.hairColor}">
                                <span class="badge" style="background:#fef3c7; color:#92400e;">${person.hairColor}</span>
                            </c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Weight</div>
                    <div class="detail-value">${person.weight}</div>
                </div>
                <c:if test="${not empty person.birthday}">
                    <div class="detail-item">
                        <div class="detail-label">Birthday</div>
                        <div class="detail-value">${person.birthday}</div>
                    </div>
                </c:if>
            </div>

            <h3 style="margin: 28px 0 16px 0; font-size: 1.2rem; color: var(--text-main);">Location</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-label">Location Name</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${not empty person.location.name}">${person.location.name}</c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Coordinate X</div>
                    <div class="detail-value">${person.location.x}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Coordinate Y</div>
                    <div class="detail-value">${person.location.y}</div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
