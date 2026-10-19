<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Persons — Cinema Hub</title>
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
            <h1 class="page-title">
                Persons
                <span class="count-badge">${total} total</span>
            </h1>
            <a href="${pageContext.request.contextPath}/persons/new" class="btn btn-primary">
                + Add Person
            </a>
        </div>

        <!-- Persons Table -->
        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Eye Color</th>
                    <th>Hair Color</th>
                    <th>Weight</th>
                    <th>Location</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty persons}">
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                No persons found. Click "+ Add Person" to create one.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="p" items="${persons}">
                            <tr>
                                <td>${p.id}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/persons/${p.id}" style="font-weight: 600;">
                                        ${p.name}
                                    </a>
                                </td>
                                <td><span class="badge" style="background:#e0f2fe; color:#0369a1;">${p.eyeColor}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.hairColor}">
                                            <span class="badge" style="background:#fef3c7; color:#92400e;">${p.hairColor}</span>
                                        </c:when>
                                        <c:otherwise>&mdash;</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${p.weight}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.location}">
                                            ${p.location.name} <span style="color:var(--text-muted); font-size:0.85rem;">(${p.location.x}, ${p.location.y})</span>
                                        </c:when>
                                        <c:otherwise>&mdash;</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="table-actions">
                                        <a href="${pageContext.request.contextPath}/persons/${p.id}" class="btn btn-secondary btn-sm">View</a>
                                        <a href="${pageContext.request.contextPath}/persons/${p.id}/edit" class="btn btn-secondary btn-sm">Edit</a>
                                        <form action="${pageContext.request.contextPath}/persons/${p.id}/delete" method="post" style="display:inline">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Delete this person?')">
                                                Delete
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <div class="pagination">
            <c:if test="${page > 0}">
                <a href="?page=${page - 1}&size=${size}">&larr; Previous</a>
            </c:if>

            <form method="get" action="${pageContext.request.contextPath}/persons">
                <label for="pageInput" style="font-weight: 500;">Page:</label>
                <input type="number" id="pageInput" name="pageInput" min="1" max="${totalPages > 0 ? totalPages : 1}" value="${page + 1}" required/>
                <span>of <strong>${totalPages > 0 ? totalPages : 1}</strong></span>

                <label for="pageSize" style="margin-left: 8px; font-weight: 500;">Per page:</label>
                <input type="number" id="pageSize" name="size" min="1" max="100" value="${size}" required/>

                <button type="submit">Go</button>
            </form>

            <c:if test="${page + 1 < totalPages}">
                <a href="?page=${page + 1}&size=${size}">Next &rarr;</a>
            </c:if>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script>
        var socket = new SockJS('${pageContext.request.contextPath}/ws');
        var stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/topic/persons', function (message) {
                window.location.reload();
            });
        });
    </script>
</body>
</html>
