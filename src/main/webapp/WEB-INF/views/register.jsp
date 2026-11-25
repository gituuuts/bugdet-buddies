<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Daftar | Budget Buddies</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap" rel="stylesheet">
  <link href="${ctx}/css/custom.css" rel="stylesheet">
</head>
<body>
<div class="guest">
  <div class="card p-4 p-md-5" style="max-width:460px;width:100%">
    <h1 class="h3 text-center mb-1" style="color:var(--blue)">Buat Akun</h1>
    <p class="muted text-center mb-4">Mulai catat keuanganmu bersama Budget Buddies</p>

    <c:if test="${not empty error}">
      <div class="alert alert-danger fw-bold" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <form action="${ctx}/register" method="post" class="needs-validation" novalidate>
      <div class="mb-3">
        <label for="name" class="form-label">Nama</label>
        <input type="text" class="form-control" id="name" name="name" maxlength="100"
               value="<c:out value='${name}'/>" autocomplete="name" required autofocus>
        <div class="invalid-feedback">Nama wajib diisi.</div>
      </div>
      <div class="mb-3">
        <label for="email" class="form-label">Email</label>
        <input type="email" class="form-control" id="email" name="email" maxlength="100"
               value="<c:out value='${email}'/>" autocomplete="email" required>
        <div class="invalid-feedback">Masukkan email yang valid.</div>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Password</label>
        <input type="password" class="form-control" id="password" name="password"
               minlength="8" maxlength="128" pattern="(?=.*[A-Za-z])(?=.*\d).{8,}"
               autocomplete="new-password" required>
        <div class="form-text">Minimal 8 karakter, mengandung huruf dan angka.</div>
        <div class="invalid-feedback">Password minimal 8 karakter, harus ada huruf dan angka.</div>
      </div>
      <div class="mb-4">
        <label for="confirmPassword" class="form-label">Ulangi Password</label>
        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
               autocomplete="new-password" required>
        <div class="invalid-feedback" id="confirmFeedback">Konfirmasi password tidak sama.</div>
      </div>
      <button type="submit" class="btn btn-primary w-100">Daftar</button>
    </form>

    <p class="text-center muted mt-4 mb-0">
      Sudah punya akun? <a href="${ctx}/login" class="fw-bold">Masuk</a>
    </p>
  </div>
</div>

<script>
  document.querySelectorAll('.needs-validation').forEach(function (form) {
    var pw = form.querySelector('#password');
    var confirmPw = form.querySelector('#confirmPassword');

    function checkMatch() {
      confirmPw.setCustomValidity(pw.value === confirmPw.value ? '' : 'Tidak sama');
    }
    pw.addEventListener('input', checkMatch);
    confirmPw.addEventListener('input', checkMatch);

    form.addEventListener('submit', function (e) {
      checkMatch();
      if (!form.checkValidity()) { e.preventDefault(); e.stopPropagation(); }
      form.classList.add('was-validated');
    });
  });
</script>
</body>
</html>
