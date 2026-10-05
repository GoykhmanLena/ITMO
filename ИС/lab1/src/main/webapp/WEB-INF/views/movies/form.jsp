<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Movie form</title>
    <style>
        .error { color: red; font-size: 0.9em; display: block; margin-top: 2px; margin-bottom: 10px; }
        .form-group { margin-bottom: 15px; }
        .person-block { padding: 15px; border: 2px dashed #ccc; margin-top: 10px; background: #fdfdfd; }
    </style>
</head>
<body>
    <h1>${movie.id == null ? 'New movie' : 'Edit movie'}</h1>

    <form:form method="post" modelAttribute="movie"
               action="${pageContext.request.contextPath}/movies${movie.id == null ? '' : '/'}${movie.id == null ? '' : movie.id}">

        <form:errors path="*" cssStyle="color: red; border: 1px solid red; padding: 10px; display: block; margin-bottom: 15px;"/>

        <div class="form-group">
            <p>Name: <form:input path="name" required="required" pattern=".*\S+.*" title="Строка не может быть пустой"/></p>
            <form:errors path="name" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Tagline: <form:input path="tagline" required="required" pattern=".*\S+.*" title="Строка не может быть пустой"/></p>
            <form:errors path="tagline" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Genre:
                <form:select path="genre" required="required">
                    <c:forEach var="g" items="${genres}">
                        <form:option value="${g}">${g}</form:option>
                    </c:forEach>
                </form:select>
            </p>
            <form:errors path="genre" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Budget: <form:input type="number" step="0.01" min="0.01" path="budget" required="required"/></p>
            <form:errors path="budget" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Total Box Office: <form:input type="number" min="1" path="totalBoxOffice" required="required"/></p>
            <form:errors path="totalBoxOffice" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>USA Box Office: <form:input type="number" step="0.01" min="0.01" path="usaBoxOffice" required="required"/></p>
            <form:errors path="usaBoxOffice" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Oscars: <form:input type="number" min="1" path="oscarsCount"/></p>
            <form:errors path="oscarsCount" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Golden Palm Count: <form:input type="number" min="1" path="goldenPalmCount" required="required"/></p>
            <form:errors path="goldenPalmCount" cssClass="error"/>
        </div>

        <div class="form-group">
            <p>Length: <form:input type="number" min="1" path="length" required="required"/></p>
            <form:errors path="length" cssClass="error"/>
        </div>

        <h3>Coordinates</h3>
        <div class="form-group">
            <p>X: <form:input type="number" path="coordinates.x" required="required"/></p>
            <form:errors path="coordinates.x" cssClass="error"/>
        </div>
        <div class="form-group">
            <p>Y: <form:input type="number" step="0.01" min="-175.99" path="coordinates.y" required="required"/></p>
            <form:errors path="coordinates.y" cssClass="error"/>
        </div>

        <!-- ================= OPERATOR (Обязательный) ================= -->
        <h3>Operator *</h3>
        <div class="form-group">
            <label style="margin-right: 15px;">
                <input type="radio" name="op_mode" value="existing"
                       ${movie.operator == null || movie.operator.id != null ? 'checked' : ''}
                       onchange="togglePerson('op', this.value, true)"> Select existing
            </label>
            <label>
                <input type="radio" name="op_mode" value="new"
                       ${movie.operator != null && movie.operator.id == null ? 'checked' : ''}
                       onchange="togglePerson('op', this.value, true)"> Create new
            </label>

            <div id="op_existing" style="margin-top: 10px;">
                <form:select path="operator.id" id="op_id">
                    <form:option value="">— select —</form:option>
                    <c:forEach var="p" items="${persons}">
                        <form:option value="${p.id}">${p.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div id="op_new" class="person-block" style="display:none; border-color: #4CAF50;">
                <p>Name: <form:input path="operator.name" id="op_name" disabled="true" pattern=".*\S+.*"/></p>
                <p>Eye Color:
                    <form:select path="operator.eyeColor" id="op_eye" disabled="true">
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Hair Color (optional):
                    <form:select path="operator.hairColor" id="op_hair" disabled="true">
                        <form:option value="">— none —</form:option>
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Weight: <form:input type="number" min="1" path="operator.weight" id="op_weight" disabled="true"/></p>

                <h4>Location</h4>
                <p>X: <form:input type="number" path="operator.location.x" id="op_loc_x" disabled="true"/></p>
                <p>Y: <form:input type="number" step="0.01" path="operator.location.y" id="op_loc_y" disabled="true"/></p>
                <p>Location Name (optional): <form:input path="operator.location.name" id="op_loc_name" disabled="true"/></p>
            </div>
        </div>

        <!-- ================= DIRECTOR (Необязательный) ================= -->
        <h3>Director</h3>
        <div class="form-group">
            <label style="margin-right: 15px;">
                <input type="radio" name="dir_mode" value="none"
                       ${movie.director == null ? 'checked' : ''}
                       onchange="togglePerson('dir', this.value, false)"> None
            </label>
            <label style="margin-right: 15px;">
                <input type="radio" name="dir_mode" value="existing"
                       ${movie.director != null && movie.director.id != null ? 'checked' : ''}
                       onchange="togglePerson('dir', this.value, false)"> Select existing
            </label>
            <label>
                <input type="radio" name="dir_mode" value="new"
                       ${movie.director != null && movie.director.id == null ? 'checked' : ''}
                       onchange="togglePerson('dir', this.value, false)"> Create new
            </label>

            <div id="dir_existing" style="display:none; margin-top: 10px;">
                <form:select path="director.id" id="dir_id">
                    <form:option value="">— select —</form:option>
                    <c:forEach var="p" items="${persons}">
                        <form:option value="${p.id}">${p.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div id="dir_new" class="person-block" style="display:none; border-color: #2196F3;">
                <p>Name: <form:input path="director.name" id="dir_name" disabled="true" pattern=".*\S+.*"/></p>
                <p>Eye Color:
                    <form:select path="director.eyeColor" id="dir_eye" disabled="true">
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Hair Color (optional):
                    <form:select path="director.hairColor" id="dir_hair" disabled="true">
                        <form:option value="">— none —</form:option>
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Weight: <form:input type="number" min="1" path="director.weight" id="dir_weight" disabled="true"/></p>

                <h4>Location</h4>
                <p>X: <form:input type="number" path="director.location.x" id="dir_loc_x" disabled="true"/></p>
                <p>Y: <form:input type="number" step="0.01" path="director.location.y" id="dir_loc_y" disabled="true"/></p>
                <p>Location Name (optional): <form:input path="director.location.name" id="dir_loc_name" disabled="true"/></p>
            </div>
        </div>

        <!-- ================= SCREENWRITER (Необязательный) ================= -->
        <h3>Screenwriter</h3>
        <div class="form-group">
            <label style="margin-right: 15px;">
                <input type="radio" name="sw_mode" value="none"
                       ${movie.screenwriter == null ? 'checked' : ''}
                       onchange="togglePerson('sw', this.value, false)"> None
            </label>
            <label style="margin-right: 15px;">
                <input type="radio" name="sw_mode" value="existing"
                       ${movie.screenwriter != null && movie.screenwriter.id != null ? 'checked' : ''}
                       onchange="togglePerson('sw', this.value, false)"> Select existing
            </label>
            <label>
                <input type="radio" name="sw_mode" value="new"
                       ${movie.screenwriter != null && movie.screenwriter.id == null ? 'checked' : ''}
                       onchange="togglePerson('sw', this.value, false)"> Create new
            </label>

            <div id="sw_existing" style="display:none; margin-top: 10px;">
                <form:select path="screenwriter.id" id="sw_id">
                    <form:option value="">— select —</form:option>
                    <c:forEach var="p" items="${persons}">
                        <form:option value="${p.id}">${p.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>

            <div id="sw_new" class="person-block" style="display:none; border-color: #9C27B0;">
                <p>Name: <form:input path="screenwriter.name" id="sw_name" disabled="true" pattern=".*\S+.*"/></p>
                <p>Eye Color:
                    <form:select path="screenwriter.eyeColor" id="sw_eye" disabled="true">
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Hair Color (optional):
                    <form:select path="screenwriter.hairColor" id="sw_hair" disabled="true">
                        <form:option value="">— none —</form:option>
                        <c:forEach var="c" items="${colors}">
                            <form:option value="${c}">${c}</form:option>
                        </c:forEach>
                    </form:select>
                </p>
                <p>Weight: <form:input type="number" min="1" path="screenwriter.weight" id="sw_weight" disabled="true"/></p>

                <h4>Location</h4>
                <p>X: <form:input type="number" path="screenwriter.location.x" id="sw_loc_x" disabled="true"/></p>
                <p>Y: <form:input type="number" step="0.01" path="screenwriter.location.y" id="sw_loc_y" disabled="true"/></p>
                <p>Location Name (optional): <form:input path="screenwriter.location.name" id="sw_loc_name" disabled="true"/></p>
            </div>
        </div>

        <div style="margin-top: 20px;">
            <button type="submit" style="padding: 10px 20px;">Save Movie</button>
            <a href="${pageContext.request.contextPath}/movies" style="margin-left: 15px;">Cancel</a>
        </div>
    </form:form>

    <script>
        function togglePerson(prefix, mode, isRequiredGlobal) {
            let isExisting = (mode === 'existing');
            let isNew = (mode === 'new');

            let existingDiv = document.getElementById(prefix + '_existing');
            if (existingDiv) existingDiv.style.display = isExisting ? 'block' : 'none';

            let newDiv = document.getElementById(prefix + '_new');
            if (newDiv) newDiv.style.display = isNew ? 'block' : 'none';

            let idSelect = document.getElementById(prefix + '_id');
            if (idSelect) {
                idSelect.disabled = !isExisting;
                idSelect.required = isExisting && isRequiredGlobal;
            }

            let fields = ['name', 'eye', 'hair', 'weight', 'loc_x', 'loc_y', 'loc_name'];
            let requiredFields = ['name', 'eye', 'weight', 'loc_x', 'loc_y'];

            fields.forEach(f => {
                let el = document.getElementById(prefix + '_' + f);
                if (el) {
                    el.disabled = !isNew;
                    el.required = isNew && requiredFields.includes(f);
                }
            });
        }

        window.onload = function() {
            let opMode = document.querySelector('input[name="op_mode"]:checked');
            if (opMode) togglePerson('op', opMode.value, true);

            let dirMode = document.querySelector('input[name="dir_mode"]:checked');
            if (dirMode) togglePerson('dir', dirMode.value, false);

            let swMode = document.querySelector('input[name="sw_mode"]:checked');
            if (swMode) togglePerson('sw', swMode.value, false);
        };
    </script>
</body>
</html>
