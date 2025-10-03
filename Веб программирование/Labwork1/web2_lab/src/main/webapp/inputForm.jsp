<%--
  Created by IntelliJ IDEA.
  User: lena
  Date: 26.09.2025
  Time: 17:55
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
  <title>Input Form</title>
</head>
<body>
<form action="/web2_lab/controller" method="POST" id="data-form">
  <fieldset id="x-block">
    <legend>Выберите X:</legend>
    <%-- Кнопки для выбора X. Значение будет устанавливаться в скрытое поле JS --%>
    <input type="button" value="-4">
    <input type="button" value="-3">
    <input type="button" value="-2">
    <input type="button" value="-1">
    <input type="button" value="0">
    <input type="button" value="1">
    <input type="button" value="2">
    <input type="button" value="3">
    <input type="button" value="4">
    <%-- Скрытое поле для передачи значения X --%>
    <input type="hidden" name="x" id="hidden-x-input">
  </fieldset>

  <label for="y">Введите Y (-3 … 5):</label>
  <input type="text" id="y" name="y" required placeholder="от -3 до 5">

  <fieldset id="r-block">
    <legend>Выберите R:</legend>
    <label><input type="radio" name="r" value="1.0"> 1</label>
    <label><input type="radio" name="r" value="1.5"> 1.5</label>
    <label><input type="radio" name="r" value="2.0"> 2</label>
    <label><input type="radio" name="r" value="2.5"> 2.5</label>
    <label><input type="radio" name="r" value="3.0"> 3</label>
  </fieldset>

  <button type="submit">Отправить</button>
</form>
</body>
</html>