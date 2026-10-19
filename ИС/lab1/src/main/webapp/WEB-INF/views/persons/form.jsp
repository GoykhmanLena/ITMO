<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${person.id == null ? 'New Person' : 'Edit Person'} — Cinema Hub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <!-- Top Navigation -->
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
            <h1 class="page-title">${person.id == null ? 'Create New Person' : 'Edit Person'}</h1>
            <a href="${pageContext.request.contextPath}/persons" class="btn btn-secondary">&larr; Back to List</a>
        </div>

        <div class="card" style="max-width: 680px; margin: 0 auto;">
            <form method="post"
                  action="${pageContext.request.contextPath}/persons${person.id == null ? '' : '/'}${person.id == null ? '' : person.id}">

                <h3 style="margin-bottom: 16px; color: var(--text-main);">Personal Information</h3>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="name">Name *</label>
                        <input type="text" id="name" name="name" value="${person.name}" required pattern=".*\S+.*" title="Строка не может быть пустой" placeholder="e.g. Christopher Nolan"/>
                    </div>

                    <div class="form-group">
                        <label for="weight">Weight (kg) *</label>
                        <input type="number" min="1" id="weight" name="weight" value="${person.weight}" required placeholder="75"/>
                    </div>
                </div>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="eyeColor">Eye Color *</label>
                        <select id="eyeColor" name="eyeColor" required>
                            <c:forEach var="c" items="${colors}">
                                <option value="${c}" ${person.eyeColor == c ? 'selected' : ''}>${c}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="hairColor">Hair Color (Optional)</label>
                        <select id="hairColor" name="hairColor">
                            <option value="">— none —</option>
                            <c:forEach var="c" items="${colors}">
                                <option value="${c}" ${person.hairColor == c ? 'selected' : ''}>${c}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <h3 style="margin: 24px 0 16px 0; color: var(--text-main);">Location</h3>

                <div class="form-grid-2">
                    <div class="form-group">
                        <label for="loc_x">Location X *</label>
                        <input type="number" id="loc_x" name="location.x" value="${person.location.x}" required placeholder="10"/>
                    </div>

                    <div class="form-group">
                        <label for="loc_y">Location Y *</label>
                        <input type="number" step="0.01" id="loc_y" name="location.y" value="${person.location.y}" required placeholder="20.5"/>
                    </div>
                </div>

                <div class="form-group">
                    <label for="loc_name">Location Name (Optional)</label>
                    <input type="text" id="loc_name" name="location.name" value="${person.location.name}" placeholder="e.g. Los Angeles"/>
                </div>

                <div style="margin-top: 32px; display: flex; align-items: center; gap: 16px;">
                    <button type="submit" class="btn btn-primary" style="padding: 12px 24px;">Save Person</button>
                    <a href="${pageContext.request.contextPath}/persons" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </main>
</body>
</html>
