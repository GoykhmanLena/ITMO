<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Special Operations — Cinema Hub</title>
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
                <a href="${pageContext.request.contextPath}/persons" class="nav-link">Persons</a>
                <a href="${pageContext.request.contextPath}/special" class="nav-link active">Special Operations</a>
            </nav>
            <div class="navbar-actions">
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Log out</a>
            </div>
        </div>
    </header>

    <main class="main-container">
        <div class="page-header">
            <h1 class="page-title">Special Database Operations</h1>
        </div>

        <div class="operations-grid">
            <!-- 1. Delete movies by genre -->
            <div class="operation-card">
                <div>
                    <h3>🗑️ 1. Delete Movies by Genre</h3>
                    <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 12px;">
                        Batch delete all movies belonging to the selected genre.
                    </p>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/special/delete-by-genre">
                    <div class="form-group" style="margin-bottom: 8px;">
                        <label for="del_genre">Select Genre:</label>
                        <select id="del_genre" name="genre" required>
                            <c:forEach var="g" items="${genres}">
                                <option value="${g}">${g}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete all movies of this genre?')">
                        Delete Movies
                    </button>
                </form>
            </div>

            <!-- 2. Find movies by name prefix -->
            <div class="operation-card">
                <div>
                    <h3>🔍 2. Find Movies by Prefix</h3>
                    <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 12px;">
                        Search for movies whose title starts with a specific prefix.
                    </p>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/special/search-by-name">
                    <div class="form-group" style="margin-bottom: 8px;">
                        <label for="prefix">Title Prefix:</label>
                        <input type="text" id="prefix" name="prefix" value="${searchPrefix != null ? searchPrefix : param.prefix}" required placeholder="e.g. Mo, The, Mov...">
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">Search Movies</button>
                </form>
            </div>

            <!-- 3. Find movies by genre less than -->
            <div class="operation-card">
                <div>
                    <h3>📉 3. Movies with Genre Less Than</h3>
                    <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 12px;">
                        Find movies ranked before the selected genre in order.
                    </p>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/special/genre-less-than">
                    <div class="form-group" style="margin-bottom: 8px;">
                        <label for="target_genre">Genre Threshold:</label>
                        <select id="target_genre" name="genre" required>
                            <c:forEach var="g" items="${genres}">
                                <option value="${g}" ${g == selectedGenreLessThan || param.genre == g.name() ? 'selected' : ''}>${g}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary btn-sm">Filter Movies</button>
                </form>
            </div>

            <!-- 4. Find directors without Oscars -->
            <div class="operation-card">
                <div>
                    <h3>🎭 4. Directors Without Oscars</h3>
                    <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 12px;">
                        Get distinct list of directors with zero or null Oscars in their movies.
                    </p>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/special/directors-no-oscars">
                    <button type="submit" class="btn btn-primary btn-sm" style="margin-top: 28px;">Find Directors</button>
                </form>
            </div>

            <!-- 5. Redistribute Oscars -->
            <div class="operation-card" style="grid-column: 1 / -1;">
                <div>
                    <h3>🔄 5. Redistribute Oscars Across Genres</h3>
                    <p style="color: var(--text-muted); font-size: 0.875rem; margin-bottom: 12px;">
                        Take all Oscars from source genre and distribute them evenly across target genre movies.
                    </p>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/special/redistribute-oscars" style="flex-direction: row; align-items: flex-end; gap: 16px; flex-wrap: wrap;">
                    <div class="form-group" style="margin-bottom: 0; min-width: 200px;">
                        <label for="from_genre">From Genre:</label>
                        <select id="from_genre" name="from" required>
                            <c:forEach var="g" items="${genres}">
                                <option value="${g}">${g}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 0; min-width: 200px;">
                        <label for="to_genre">To Genre:</label>
                        <select id="to_genre" name="to" required>
                            <c:forEach var="g" items="${genres}">
                                <option value="${g}">${g}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-success btn-sm" style="height: 38px;">Redistribute Oscars</button>
                </form>
            </div>
        </div>

        <!-- Result: Movies -->
        <c:if test="${moviesSearchExecuted}">
            <c:choose>
                <c:when test="${not empty moviesResult}">
                    <div class="card">
                        <h2 style="font-size: 1.3rem; margin-bottom: 16px; color: var(--text-main);">
                            Results (Movies Found: ${moviesResult.size()})
                        </h2>
                        <div class="table-container" style="box-shadow: none; margin-bottom: 0;">
                            <table>
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Genre</th>
                                    <th>Budget</th>
                                    <th>Total Box Office</th>
                                    <th>Oscars</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="m" items="${moviesResult}">
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
                                                <c:otherwise>&mdash;</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/movies/${m.id}" class="btn btn-secondary btn-sm">View</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card" style="text-align: center; padding: 32px; color: var(--text-muted);">
                        <h3 style="color: var(--text-main); margin-bottom: 8px;">Ничего не найдено</h3>
                        <p>По вашему запросу не найдено ни одного фильма.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- Result: Directors -->
        <c:if test="${directorsSearchExecuted}">
            <c:choose>
                <c:when test="${not empty personsResult}">
                    <div class="card">
                        <h2 style="font-size: 1.3rem; margin-bottom: 16px; color: var(--text-main);">
                            Results (Directors Found: ${personsResult.size()})
                        </h2>
                        <div class="table-container" style="box-shadow: none; margin-bottom: 0;">
                            <table>
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Eye Color</th>
                                    <th>Location</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="p" items="${personsResult}">
                                    <tr>
                                        <td>${p.id}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/persons/${p.id}" style="font-weight: 600;">
                                                ${p.name}
                                            </a>
                                        </td>
                                        <td><span class="badge" style="background:#e0f2fe; color:#0369a1;">${p.eyeColor}</span></td>
                                        <td>${p.location.name}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/persons/${p.id}" class="btn btn-secondary btn-sm">View</a>
                                            <a href="${pageContext.request.contextPath}/persons/${p.id}/edit" class="btn btn-secondary btn-sm">Edit</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card" style="text-align: center; padding: 32px; color: var(--text-muted);">
                        <h3 style="color: var(--text-main); margin-bottom: 8px;">Ничего не найдено</h3>
                        <p>Режиссеры без Оскаров не найдены.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>
    </main>
</body>
</html>
