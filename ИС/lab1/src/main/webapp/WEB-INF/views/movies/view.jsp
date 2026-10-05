<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><title>${movie.name}</title></head>
<body>
    <h1>${movie.name}</h1>
    <p><b>ID:</b> ${movie.id}</p>
    <p><b>Genre:</b> ${movie.genre}</p>
    <p><b>Budget:</b> ${movie.budget}</p>
    <p><b>Total Box Office:</b> ${movie.totalBoxOffice}</p>
    <p><b>USA Box Office:</b> ${movie.usaBoxOffice}</p>
    <p><b>Oscars:</b> ${movie.oscarsCount}</p>
    <p><b>Golden Palm:</b> ${movie.goldenPalmCount}</p>
    <p><b>Length:</b> ${movie.length}</p>
    <p><b>Tagline:</b> ${movie.tagline}</p>
    <p><b>MPAA:</b> ${movie.mpaaRating}</p>
    <p><b>Creation date:</b> ${movie.creationDate}</p>

    <h2>Related</h2>
    <p><b>Coordinates:</b> x=${movie.coordinates.x}, y=${movie.coordinates.y}</p>
    <p><b>Director:</b> ${movie.director != null ? movie.director.name : '—'}</p>
    <p><b>Screenwriter:</b> ${movie.screenwriter != null ? movie.screenwriter.name : '—'}</p>
    <p><b>Operator:</b> ${movie.operator.name}</p>

    <p>
        <a href="${pageContext.request.contextPath}/movies/${movie.id}/edit">Edit</a>
        |
        <a href="${pageContext.request.contextPath}/movies">Back to list</a>
    </p>
</body>
</html>
