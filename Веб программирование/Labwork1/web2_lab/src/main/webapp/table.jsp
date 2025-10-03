<%--
  Created by IntelliJ IDEA.
  User: lena
  Date: 26.09.2025
  Time: 17:55
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Results Table</title>
</head>
<body>
<core:forEach var="point" items="${applicationScope.results}">
    <tr>
        <td><fmt:formatNumber value="${point.x}" pattern="0.##"/></td>
        <td><fmt:formatNumber value="${point.y}" pattern="0.##"/></td>
        <td><fmt:formatNumber value="${point.r}" pattern="0.##"/></td>
        <td>${point.getIsHit() ? "Hit" : "Miss"}</td>
        <td><fmt:formatDate value="${point.timestamp}" pattern="HH:mm:ss dd.MM.yyyy"/></td>
    </tr>
</core:forEach>
</body>
</html>