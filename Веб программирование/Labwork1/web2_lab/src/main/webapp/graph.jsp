<%--
  Created by IntelliJ IDEA.
  User: lena
  Date: 26.09.2025
  Time: 17:48
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Graph Component</title>
  <style>
    /* Добавьте стили для активной кнопки X */
    #x-block input[type='button'].active-x {
      background-color: #007bff; /* Пример активного цвета */
      color: white;
    }
    .error-message {
      color: red;
      font-weight: bold;
      margin-top: 10px;
    }
  </style>
</head>
<body>
<canvas id="graphCanvas" width="400" height="400"></canvas>
<script>
  // Используем глобальную переменную для хранения точек
  let clickedPoints = [];
  let currentClickedPoint = null; // Для хранения только что поставленной точки на resultPage

  // Эта функция будет вызвана из main.js для загрузки точек.
  // Она наполняет `clickedPoints` или `currentClickedPoint` из JSP переменных.
  function loadPoints() {
    // Определяем, на какой странице мы находимся
    const isResultPage = window.location.pathname.includes("/controller");

    if (isResultPage) {
      // На resultPage.jsp загружаем только текущую точку
      <core:if test="${not empty requestScope.currentPointResult}">
      currentClickedPoint = {
        x: parseFloat(${requestScope.currentPointResult.x}),
        y: parseFloat(${requestScope.currentPointResult.y}),
        r: parseFloat(${requestScope.currentPointResult.r}),
        isHit: ${requestScope.currentPointResult.getIsHit() ? 'true' : 'false'},
        timestamp: new Date("${requestScope.currentPointResult.timestamp}").getTime()
      };
      clickedPoints = []; // Очищаем список всех точек, если это resultPage
      </core:if>
      <core:if test="${empty requestScope.currentPointResult}">
      currentClickedPoint = null;
      </core:if>
      console.log("Loaded points:", clickedPoints); // Для отладки
      console.log("Current point:", currentClickedPoint); // Для отладки
    } else {
      // На index.jsp (и других, если график будет использоваться) загружаем все точки из applicationScope
      clickedPoints = [
        <core:forEach var="point" items="${applicationScope.results}" varStatus="loop">
        {
          x: parseFloat(${point.x}),
          y: parseFloat(${point.y}),
          r: parseFloat(${point.r}),
          isHit: ${point.getIsHit() ? 'true' : 'false'},
          timestamp: new Date("${point.timestamp}").getTime() // Передаем timestamp для JS
        }<core:if test="${!loop.last}">,</core:if>
        </core:forEach>
      ];
      currentClickedPoint = null; // Убедимся, что текущая точка не установлена
    }
    console.log("Loaded points:", clickedPoints); // Для отладки
    console.log("Current point:", currentClickedPoint); // Для отладки
  }
</script>
</body>
</html>