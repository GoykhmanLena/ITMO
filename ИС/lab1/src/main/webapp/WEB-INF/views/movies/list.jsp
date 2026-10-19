<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Movies — Cinema Hub</title>
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
                <a href="${pageContext.request.contextPath}/movies" class="nav-link active">Movies</a>
                <a href="${pageContext.request.contextPath}/persons" class="nav-link">Persons</a>
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
                Movies
                <span class="count-badge">${total} total</span>
            </h1>
            <a href="${pageContext.request.contextPath}/movies/new" class="btn btn-primary">
                + Add Movie
            </a>
        </div>

        <div class="controls-card">
            <form method="get" action="${pageContext.request.contextPath}/movies" class="controls-form">
                <input type="hidden" name="size" value="${size}"/>

                <div class="form-inline-group">
                    <label for="nameFilter">Filter by Name:</label>
                    <input type="text" id="nameFilter" name="nameFilter" value="${nameFilter}" placeholder="Exact movie name...">
                </div>

                <div class="form-inline-group">
                    <label for="sort">Sort by:</label>
                    <select id="sort" name="sort">
                        <option value="id" ${sort == 'id' ? 'selected' : ''}>ID (Default)</option>
                        <option value="name" ${sort == 'name' ? 'selected' : ''}>Name</option>
                        <option value="tagline" ${sort == 'tagline' ? 'selected' : ''}>Tagline</option>
                        <option value="genre" ${sort == 'genre' ? 'selected' : ''}>Genre</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary btn-sm">Apply</button>
                <a href="${pageContext.request.contextPath}/movies" class="btn btn-secondary btn-sm">Clear</a>
            </form>
        </div>

        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Genre</th>
                    <th>Budget</th>
                    <th>Total Box Office</th>
                    <th>Oscars</th>
                    <th>Length</th>
                    <th>Tagline</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty movies}">
                        <tr>
                            <td colspan="9" style="text-align: center; padding: 32px; color: var(--text-muted);">
                                No movies found. Click "+ Add Movie" to create one.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="m" items="${movies}">
                            <tr>
                                <td>${m.id}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/movies/${m.id}" style="font-weight: 600;">
                                        ${m.name}
                                    </a>
                                </td>
                                <td><span class="badge badge-genre">${m.genre}</span></td>
                                <td>${m.budget}</td>
                                <td>${m.totalBoxOffice}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty m.oscarsCount}">
                                            <span class="badge badge-oscar">🏆 ${m.oscarsCount}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: var(--text-subtle);">&mdash;</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${m.length} min</td>
                                <td style="color: var(--text-muted); font-style: italic; max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                    "${m.tagline}"
                                </td>
                                <td>
                                    <div class="table-actions">
                                        <a href="${pageContext.request.contextPath}/movies/${m.id}/edit" class="btn btn-secondary btn-sm">
                                            Edit
                                        </a>
                                        <form action="${pageContext.request.contextPath}/movies/${m.id}/delete" method="post" style="display:inline">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Delete this movie?')">
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
                <a href="?page=${page - 1}&size=${size}&nameFilter=${nameFilter}&sort=${sort}">&larr; Previous</a>
            </c:if>

            <form method="get" action="${pageContext.request.contextPath}/movies">
                <input type="hidden" name="nameFilter" value="${nameFilter}"/>
                <input type="hidden" name="sort" value="${sort}"/>

                <label for="pageInput" style="font-weight: 500;">Page:</label>
                <input type="number" id="pageInput" name="pageInput" min="1" max="${totalPages > 0 ? totalPages : 1}" value="${page + 1}" required/>
                <span>of <strong>${totalPages > 0 ? totalPages : 1}</strong></span>

                <label for="pageSize" style="margin-left: 8px; font-weight: 500;">Per page:</label>
                <input type="number" id="pageSize" name="size" min="1" max="100" value="${size}" required/>

                <button type="submit">Go</button>
            </form>

            <c:if test="${page + 1 < totalPages}">
                <a href="?page=${page + 1}&size=${size}&nameFilter=${nameFilter}&sort=${sort}">Next &rarr;</a>
            </c:if>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script>
        var socket = new SockJS('${pageContext.request.contextPath}/ws');
        var stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/movies', function (message) {
                window.location.reload();
            });
        });
    </script>
</body>
</html>
