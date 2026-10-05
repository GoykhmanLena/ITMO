<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head><title>Person form</title></head>
<body>
    <h1>${person.id == null ? 'New person' : 'Edit person'}</h1>

    <form method="post"
          action="${pageContext.request.contextPath}/persons${person.id == null ? '' : '/'}${person.id == null ? '' : person.id}">

        <p>Name: <input name="name" value="${person.name}" required/></p>

        <p>Eye color:
            <select name="eyeColor" required>
                <c:forEach var="c" items="${colors}">
                    <option value="${c}" ${person.eyeColor == c ? 'selected' : ''}>${c}</option>
                </c:forEach>
            </select>
        </p>

        <p>Hair color:
            <select name="hairColor">
                <option value="">—</option>
                <c:forEach var="c" items="${colors}">
                    <option value="${c}" ${person.hairColor == c ? 'selected' : ''}>${c}</option>
                </c:forEach>
            </select>
        </p>

        <p>Weight: <input type="number" name="weight" value="${person.weight}" required/></p>

        <h3>Location</h3>
        <p>X: <input type="number" name="location.x" value="${person.location.x}" required/></p>
        <p>Y: <input type="number" step="0.01" name="location.y" value="${person.location.y}" required/></p>
        <p>Name: <input name="location.name" value="${person.location.name}"/></p>

        <button type="submit">Save</button>
        <a href="${pageContext.request.contextPath}/persons">Cancel</a>
    </form>
</body>
</html>
