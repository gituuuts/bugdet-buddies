<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Masuk | Budget Buddies</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap" rel="stylesheet">
  <link href="${ctx}/css/custom.css" rel="stylesheet">
</head>
<body>
<div class="guest">
  <div class="card p-4 p-md-5" style="max-width:440px;width:100%">
    <h1 class="h3 text-center mb-1" style="color:var(--blue)">Budget Buddies</h1>
    <p class="muted text-center mb-4">Masuk untuk mengelola keuanganmu</p>

    <c:if test="${not empty flashSuccess}">
      <div class="alert alert-success fw-bold" role="alert"><c:out value="${flashSuccess}"/></div>
    </c:if>
    <c:if test="${not empty flashError}">
      <div class="alert alert-danger fw-bold" role="alert"><c:out value="${flashError}"/></div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="alert alert-danger fw-bold" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <form action="${ctx}/login" method="post" class="needs-validation" novalidate>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control" id="email" name="email" maxlength="100"
               value="<c:out value='${email}'/>" autocomplete="email" required autofocus>
        <div class="invalid-feedback">Masukkan email yang valid.</div>
      </div>
      <div class="mb-4">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" id="password" name="password"
               autocomplete="current-password" required>
        <div class="invalid-feedback">Password wajib diisi.</div>
      </div>
      <button type="submit" class="btn btn-primary w-100">Masuk</button>
    </form>

    <p class="text-center muted mt-4 mb-0">
      Belum punya akun? <a href="${ctx}/register" class="fw-bold">Daftar</a>
    </p>
  </div>
</div>

<script>
  document.querySelectorAll('.needs-validation').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!form.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
      form.classList.add('was-validated');
    });
  });
</script>
</body>
</html>
