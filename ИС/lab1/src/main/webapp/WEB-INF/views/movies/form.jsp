<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${movie.id == null ? 'New Movie' : 'Edit Movie'} — Cinema Hub</title>
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
            <h1 class="page-title">${movie.id == null ? 'Create New Movie' : 'Edit Movie'}</h1>
            <a href="${pageContext.request.contextPath}/movies" class="btn btn-secondary">&larr; Back to List</a>
        </div>

        <div class="card" style="max-width: 860px; margin: 0 auto;">
            <form:form method="post" modelAttribute="movie"
                       action="${pageContext.request.contextPath}/movies${movie.id == null ? '' : '/'}${movie.id == null ? '' : movie.id}">

                <form:errors path="*" cssClass="alert-error" element="div"/>

                <h3 style="margin-bottom: 16px; color: var(--text-main);">General Information</h3>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="name">Movie Name *</label>
                        <form:input path="name" id="name" required="required" pattern=".*\\S+.*" title="Строка не может быть пустой" placeholder="e.g. Inception"/>
                        <form:errors path="name" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="tagline">Tagline *</label>
                        <form:input path="tagline" id="tagline" required="required" pattern=".*\\S+.*" title="Строка не может быть пустой" placeholder="e.g. Your mind is the scene of the crime"/>
                        <form:errors path="tagline" cssClass="error"/>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="genre">Genre *</label>
                        <form:select path="genre" id="genre" required="required">
                            <c:forEach var="g" items="${genres}">
                                <form:option value="${g}">${g}</form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="genre" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="mpaaRating">MPAA Rating (Optional)</label>
                        <form:select path="mpaaRating" id="mpaaRating">
                            <form:option value="">— none —</form:option>
                            <c:forEach var="rating" items="${mpaaRatings}">
                                <form:option value="${rating}">${rating}</form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="mpaaRating" cssClass="error"/>
                    </div>
                </div>

                <div class="form-grid-3">
                    <div class="form-group">
                        <label for="budget">Budget *</label>
                        <form:input type="number" step="0.01" min="0.01" path="budget" id="budget" required="required" placeholder="1000000"/>
                        <form:errors path="budget" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="totalBoxOffice">Total Box Office *</label>
                        <form:input type="number" min="1" path="totalBoxOffice" id="totalBoxOffice" required="required" placeholder="5000000"/>
                        <form:errors path="totalBoxOffice" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="usaBoxOffice">USA Box Office *</label>
                        <form:input type="number" step="0.01" min="0.01" path="usaBoxOffice" id="usaBoxOffice" required="required" placeholder="2000000"/>
                        <form:errors path="usaBoxOffice" cssClass="error"/>
                    </div>
                </div>

                <div class="form-grid-3">
                    <div class="form-group">
                        <label for="oscarsCount">Oscars Count</label>
                        <form:input type="number" min="1" path="oscarsCount" id="oscarsCount" placeholder="0 or leave empty"/>
                        <form:errors path="oscarsCount" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="goldenPalmCount">Golden Palm Count *</label>
                        <form:input type="number" min="1" path="goldenPalmCount" id="goldenPalmCount" required="required" placeholder="1"/>
                        <form:errors path="goldenPalmCount" cssClass="error"/>
                    </div>

                    <div class="form-group">
                        <label for="length">Length (minutes) *</label>
                        <form:input type="number" min="1" path="length" id="length" required="required" placeholder="120"/>
                        <form:errors path="length" cssClass="error"/>
                    </div>
                </div>

                <h3 style="margin: 24px 0 16px 0; color: var(--text-main);">Coordinates</h3>
                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="coordinates.x">Coordinate X *</label>
                        <form:input type="number" path="coordinates.x" id="coordinates.x" required="required"/>
                        <form:errors path="coordinates.x" cssClass="error"/>
                    </div>
                    <div class="form-group">
                        <label for="coordinates.y">Coordinate Y (> -176) *</label>
                        <form:input type="number" step="0.01" min="-175.99" path="coordinates.y" id="coordinates.y" required="required"/>
                        <form:errors path="coordinates.y" cssClass="error"/>
                    </div>
                </div>

                <h3 style="margin: 28px 0 8px 0; color: var(--text-main);">Operator *</h3>
                <div class="form-group">
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" name="op_mode" value="existing"
                                   ${movie.operator == null || movie.operator.id != null ? 'checked' : ''}
                                   onchange="togglePerson('op', this.value, true)"> Select existing person
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="op_mode" value="new"
                                   ${movie.operator != null && movie.operator.id == null ? 'checked' : ''}
                                   onchange="togglePerson('op', this.value, true)"> Create new person
                        </label>
                    </div>

                    <div id="op_existing" style="margin-top: 12px;">
                        <form:select path="operator.id" id="op_id">
                            <form:option value="">— select person —</form:option>
                            <c:forEach var="p" items="${persons}">
                                <form:option value="${p.id}">${p.name} (ID: ${p.id})</form:option>
                            </c:forEach>
                        </form:select>
                    </div>

                    <div id="op_new" class="person-block" style="display:none; border-left-color: #10b981;">
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="op_name">Name *</label>
                                <form:input path="operator.name" id="op_name" disabled="true" pattern=".*\\S+.*"/>
                            </div>
                            <div class="form-group">
                                <label for="op_weight">Weight *</label>
                                <form:input type="number" min="1" path="operator.weight" id="op_weight" disabled="true"/>
                            </div>
                        </div>
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="op_eye">Eye Color *</label>
                                <form:select path="operator.eyeColor" id="op_eye" disabled="true">
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="form-group">
                                <label for="op_hair">Hair Color</label>
                                <form:select path="operator.hairColor" id="op_hair" disabled="true">
                                    <form:option value="">— none —</form:option>
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>
                        <h4>Location</h4>
                        <div class="form-grid-3">
                            <div class="form-group">
                                <label for="op_loc_x">X *</label>
                                <form:input type="number" path="operator.location.x" id="op_loc_x" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="op_loc_y">Y *</label>
                                <form:input type="number" step="0.01" path="operator.location.y" id="op_loc_y" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="op_loc_name">Location Name</label>
                                <form:input path="operator.location.name" id="op_loc_name" disabled="true"/>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- ================= DIRECTOR ================= -->
                <h3 style="margin: 28px 0 8px 0; color: var(--text-main);">Director (Optional)</h3>
                <div class="form-group">
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" name="dir_mode" value="none"
                                   ${movie.director == null ? 'checked' : ''}
                                   onchange="togglePerson('dir', this.value, false)"> None
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="dir_mode" value="existing"
                                   ${movie.director != null && movie.director.id != null ? 'checked' : ''}
                                   onchange="togglePerson('dir', this.value, false)"> Select existing
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="dir_mode" value="new"
                                   ${movie.director != null && movie.director.id == null ? 'checked' : ''}
                                   onchange="togglePerson('dir', this.value, false)"> Create new
                        </label>
                    </div>

                    <div id="dir_existing" style="display:none; margin-top: 12px;">
                        <form:select path="director.id" id="dir_id">
                            <form:option value="">— select person —</form:option>
                            <c:forEach var="p" items="${persons}">
                                <form:option value="${p.id}">${p.name} (ID: ${p.id})</form:option>
                            </c:forEach>
                        </form:select>
                    </div>

                    <div id="dir_new" class="person-block" style="display:none; border-left-color: #3b82f6;">
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="dir_name">Name *</label>
                                <form:input path="director.name" id="dir_name" disabled="true" pattern=".*\\S+.*"/>
                            </div>
                            <div class="form-group">
                                <label for="dir_weight">Weight *</label>
                                <form:input type="number" min="1" path="director.weight" id="dir_weight" disabled="true"/>
                            </div>
                        </div>
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="dir_eye">Eye Color *</label>
                                <form:select path="director.eyeColor" id="dir_eye" disabled="true">
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="form-group">
                                <label for="dir_hair">Hair Color</label>
                                <form:select path="director.hairColor" id="dir_hair" disabled="true">
                                    <form:option value="">— none —</form:option>
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>
                        <h4>Location</h4>
                        <div class="form-grid-3">
                            <div class="form-group">
                                <label for="dir_loc_x">X *</label>
                                <form:input type="number" path="director.location.x" id="dir_loc_x" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="dir_loc_y">Y *</label>
                                <form:input type="number" step="0.01" path="director.location.y" id="dir_loc_y" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="dir_loc_name">Location Name</label>
                                <form:input path="director.location.name" id="dir_loc_name" disabled="true"/>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- ================= SCREENWRITER ================= -->
                <h3 style="margin: 28px 0 8px 0; color: var(--text-main);">Screenwriter (Optional)</h3>
                <div class="form-group">
                    <div class="radio-group">
                        <label class="radio-label">
                            <input type="radio" name="sw_mode" value="none"
                                   ${movie.screenwriter == null ? 'checked' : ''}
                                   onchange="togglePerson('sw', this.value, false)"> None
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="sw_mode" value="existing"
                                   ${movie.screenwriter != null && movie.screenwriter.id != null ? 'checked' : ''}
                                   onchange="togglePerson('sw', this.value, false)"> Select existing
                        </label>
                        <label class="radio-label">
                            <input type="radio" name="sw_mode" value="new"
                                   ${movie.screenwriter != null && movie.screenwriter.id == null ? 'checked' : ''}
                                   onchange="togglePerson('sw', this.value, false)"> Create new
                        </label>
                    </div>

                    <div id="sw_existing" style="display:none; margin-top: 12px;">
                        <form:select path="screenwriter.id" id="sw_id">
                            <form:option value="">— select person —</form:option>
                            <c:forEach var="p" items="${persons}">
                                <form:option value="${p.id}">${p.name} (ID: ${p.id})</form:option>
                            </c:forEach>
                        </form:select>
                    </div>

                    <div id="sw_new" class="person-block" style="display:none; border-left-color: #8b5cf6;">
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="sw_name">Name *</label>
                                <form:input path="screenwriter.name" id="sw_name" disabled="true" pattern=".*\\S+.*"/>
                            </div>
                            <div class="form-group">
                                <label for="sw_weight">Weight *</label>
                                <form:input type="number" min="1" path="screenwriter.weight" id="sw_weight" disabled="true"/>
                            </div>
                        </div>
                        <div class="form-grid-2">
                            <div class="form-group">
                                <label for="sw_eye">Eye Color *</label>
                                <form:select path="screenwriter.eyeColor" id="sw_eye" disabled="true">
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                            <div class="form-group">
                                <label for="sw_hair">Hair Color</label>
                                <form:select path="screenwriter.hairColor" id="sw_hair" disabled="true">
                                    <form:option value="">— none —</form:option>
                                    <c:forEach var="c" items="${colors}">
                                        <form:option value="${c}">${c}</form:option>
                                    </c:forEach>
                                </form:select>
                            </div>
                        </div>
                        <h4>Location</h4>
                        <div class="form-grid-3">
                            <div class="form-group">
                                <label for="sw_loc_x">X *</label>
                                <form:input type="number" path="screenwriter.location.x" id="sw_loc_x" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="sw_loc_y">Y *</label>
                                <form:input type="number" step="0.01" path="screenwriter.location.y" id="sw_loc_y" disabled="true"/>
                            </div>
                            <div class="form-group">
                                <label for="sw_loc_name">Location Name</label>
                                <form:input path="screenwriter.location.name" id="sw_loc_name" disabled="true"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div style="margin-top: 32px; display: flex; align-items: center; gap: 16px;">
                    <button type="submit" class="btn btn-primary" style="padding: 12px 24px;">Save Movie</button>
                    <a href="${pageContext.request.contextPath}/movies" class="btn btn-secondary">Cancel</a>
                </div>
            </form:form>
        </div>
    </main>

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
