<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${movie.name} — Cinema Hub</title>
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
            <div>
                <h1 class="page-title">${movie.name}</h1>
                <p style="color: var(--text-muted); font-style: italic; margin-top: 4px;">"${movie.tagline}"</p>
            </div>
            <div style="display: flex; gap: 10px;">
                <a href="${pageContext.request.contextPath}/movies/${movie.id}/edit" class="btn btn-primary">Edit Movie</a>
                <a href="${pageContext.request.contextPath}/movies" class="btn btn-secondary">&larr; Back to List</a>
            </div>
        </div>

        <div class="card">
            <h3 style="margin-bottom: 20px; font-size: 1.2rem; color: var(--text-main);">Movie Details</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-label">ID</div>
                    <div class="detail-value">${movie.id}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Genre</div>
                    <div class="detail-value"><span class="badge badge-genre">${movie.genre}</span></div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">MPAA Rating</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${not empty movie.mpaaRating}">
                                <span class="badge badge-rating">${movie.mpaaRating}</span>
                            </c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Budget</div>
                    <div class="detail-value">${movie.budget}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Total Box Office</div>
                    <div class="detail-value">${movie.totalBoxOffice}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">USA Box Office</div>
                    <div class="detail-value">${movie.usaBoxOffice}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Oscars Count</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${not empty movie.oscarsCount}">
                                <span class="badge badge-oscar">🏆 ${movie.oscarsCount}</span>
                            </c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Golden Palm Count</div>
                    <div class="detail-value">${movie.goldenPalmCount}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Length</div>
                    <div class="detail-value">${movie.length} min</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Creation Date</div>
                    <div class="detail-value" style="font-size: 0.95rem;">${movie.creationDate}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Coordinates</div>
                    <div class="detail-value">X: ${movie.coordinates.x}, Y: ${movie.coordinates.y}</div>
                </div>
            </div>

            <h3 style="margin: 28px 0 16px 0; font-size: 1.2rem; color: var(--text-main);">Crew & People</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-label">Operator *</div>
                    <div class="detail-value">
                        <a href="${pageContext.request.contextPath}/persons/${movie.operator.id}" title="View operator details">
                            ${movie.operator.name} &rarr;
                        </a>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Director</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${movie.director != null}">
                                <a href="${pageContext.request.contextPath}/persons/${movie.director.id}" title="View director details">
                                    ${movie.director.name} &rarr;
                                </a>
                            </c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="detail-item">
                    <div class="detail-label">Screenwriter</div>
                    <div class="detail-value">
                        <c:choose>
                            <c:when test="${movie.screenwriter != null}">
                                <a href="${pageContext.request.contextPath}/persons/${movie.screenwriter.id}" title="View screenwriter details">
                                    ${movie.screenwriter.name} &rarr;
                                </a>
                            </c:when>
                            <c:otherwise>&mdash;</c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>
