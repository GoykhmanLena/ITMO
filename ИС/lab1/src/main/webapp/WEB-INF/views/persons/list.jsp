<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Persons</title></head>
<body>
    <h1>Persons</h1>

    <div style="background: #eef; padding: 10px; margin-bottom: 20px; border: 1px solid #ccd;">
        <b>Navigation:</b>
        <a href="${pageContext.request.contextPath}/movies" style="margin-right: 15px;">Movies</a>
        <a href="${pageContext.request.contextPath}/persons" style="margin-right: 15px;">Persons</a>
        <a href="${pageContext.request.contextPath}/special" style="margin-right: 15px;">Special Operations</a>
    </div>

    <p>
        <a href="${pageContext.request.contextPath}/persons/new">+ Add person</a>
    </p>

    <table border="1" cellpadding="6">
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Eye color</th>
            <th>Hair color</th>
            <th>Weight</th>
            <th>Location</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${persons}">
            <tr>
                <td>${p.id}</td>
                <td>${p.name}</td>
                <td>${p.eyeColor}</td>
                <td>${p.hairColor}</td>
                <td>${p.weight}</td>
                <td>${p.location.name} (${p.location.x}, ${p.location.y})</td>
                <td>
                    <a href="${pageContext.request.contextPath}/persons/${p.id}/edit">Edit</a>
                    <form action="${pageContext.request.contextPath}/persons/${p.id}/delete"
                          method="post" style="display:inline">
                        <button type="submit" onclick="return confirm('Delete?')">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

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
