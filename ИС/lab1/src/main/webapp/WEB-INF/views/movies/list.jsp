<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movies</title>
    <style>
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 6px; }
        th { background: #eee; }
        .pagination { margin-top: 12px; }
        .pagination a { margin: 0 4px; }
        .controls { background: #f9f9f9; padding: 10px; border: 1px solid #ddd; margin-top: 10px; }
    </style>
</head>
<body>
    <h1>Movies (total: ${total})</h1>

    <div style="background: #eef; padding: 10px; margin-bottom: 20px; border: 1px solid #ccd;">
        <b>Navigation:</b>
        <a href="${pageContext.request.contextPath}/movies" style="margin-right: 15px;">Movies</a>
        <a href="${pageContext.request.contextPath}/persons" style="margin-right: 15px;">Persons</a>
        <a href="${pageContext.request.contextPath}/special" style="margin-right: 15px;">Special Operations</a>
    </div>


    <p>
        <a href="${pageContext.request.contextPath}/movies/new">+ Add movie</a>
    </p>

    <!-- Панель фильтрации и сортировки -->
    <div class="controls">
        <form method="get" action="${pageContext.request.contextPath}/movies">
            <label>Filter by Name (exact):
                <input type="text" name="nameFilter" value="${nameFilter}">
            </label>

            <label style="margin-left: 15px;">Sort by:
                <select name="sort">
                    <option value="id" ${sort == 'id' ? 'selected' : ''}>ID (Default)</option>
                    <option value="name" ${sort == 'name' ? 'selected' : ''}>Name</option>
                    <option value="tagline" ${sort == 'tagline' ? 'selected' : ''}>Tagline</option>
                    <option value="genre" ${sort == 'genre' ? 'selected' : ''}>Genre</option>
                </select>
            </label>

            <button type="submit" style="margin-left: 10px;">Apply</button>
            <a href="${pageContext.request.contextPath}/movies" style="margin-left: 5px;">Clear</a>
        </form>
    </div>

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
        <c:forEach var="m" items="${movies}">
            <tr>
                <td>${m.id}</td>
                <td><a href="${pageContext.request.contextPath}/movies/${m.id}">${m.name}</a></td>
                <td>${m.genre}</td>
                <td>${m.budget}</td>
                <td>${m.totalBoxOffice}</td>
                <td>${m.oscarsCount}</td>
                <td>${m.length}</td>
                <td>${m.tagline}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/movies/${m.id}/edit">Edit</a>
                    <form action="${pageContext.request.contextPath}/movies/${m.id}/delete"
                          method="post" style="display:inline">
                        <button type="submit"
                                onclick="return confirm('Delete this movie?')">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="pagination">
        <c:if test="${page > 0}">
            <a href="?page=${page - 1}&nameFilter=${nameFilter}&sort=${sort}">← Prev</a>
        </c:if>
        Page ${page + 1} of ${totalPages}
        <c:if test="${page + 1 < totalPages}">
            <a href="?page=${page + 1}&nameFilter=${nameFilter}&sort=${sort}">Next →</a>
        </c:if>
    </div>
</body>
</html>

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
