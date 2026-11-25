<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="status" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Terjadi Kesalahan | Budget Buddies</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap" rel="stylesheet">
  <link href="${ctx}/css/custom.css" rel="stylesheet">
</head>
<body>
<div class="guest">
  <div class="card p-4 p-md-5 text-center" style="max-width:480px;width:100%">
    <h1 class="display-4 mb-2" style="color:var(--blue)">
      <c:out value="${empty status ? 'Oops' : status}"/>
    </h1>
    <h2 class="h5 mb-3">
      <c:choose>
        <c:when test="${status == 404}">Halaman tidak ditemukan</c:when>
        <c:when test="${status == 403}">Akses ditolak</c:when>
        <c:otherwise>Terjadi kesalahan pada server</c:otherwise>
      </c:choose>
    </h2>
    <p class="muted">Silakan kembali ke dashboard atau coba lagi beberapa saat.</p>
    <a href="${ctx}/dashboard" class="btn btn-primary mt-2">Kembali ke Dashboard</a>
  </div>
</div>
</body>
</html>
