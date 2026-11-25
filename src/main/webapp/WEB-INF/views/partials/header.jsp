<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Dipakai: <jsp:include page="partials/header.jsp"><jsp:param name="title" value="..."/><jsp:param name="active" value="transactions"/></jsp:include> --%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="id">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${empty param.title ? 'Budget Buddies' : param.title}"/> | Budget Buddies</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@600;700;800&family=Plus+Jakarta+Sans:wght@500;600;700&display=swap" rel="stylesheet">
  <link href="${ctx}/css/custom.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg">
  <div class="container">
    <a class="navbar-brand" href="${ctx}/dashboard">Budget Buddies</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#nav" aria-label="Menu">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="nav">
      <ul class="navbar-nav mx-lg-auto gap-lg-1">
        <li><a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${ctx}/dashboard">Dashboard</a></li>
        <li><a class="nav-link ${param.active == 'transactions' ? 'active' : ''}" href="${ctx}/transactions">Transaksi</a></li>
        <li><a class="nav-link ${param.active == 'categories' ? 'active' : ''}" href="${ctx}/categories">Kategori</a></li>
        <li><a class="nav-link ${param.active == 'accounts' ? 'active' : ''}" href="${ctx}/accounts">Akun</a></li>
        <li><a class="nav-link ${param.active == 'budgets' ? 'active' : ''}" href="${ctx}/budgets">Anggaran</a></li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">Lainnya</a>
          <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="${ctx}/savings-goals">Target Tabungan</a></li>
            <li><a class="dropdown-item" href="${ctx}/recurring-transactions">Transaksi Berulang</a></li>
            <li><a class="dropdown-item" href="${ctx}/debts">Utang &amp; Piutang</a></li>
            <li><a class="dropdown-item" href="${ctx}/debt-payments">Pembayaran Utang</a></li>
          </ul>
        </li>
      </ul>
      <div class="d-flex align-items-center gap-3">
        <span class="muted"><c:out value="${empty sessionScope.userName ? 'Pengguna' : sessionScope.userName}"/></span>
        <%-- Endpoint /logout dari Anggota 3 --%>
        <form action="${ctx}/logout" method="POST" class="m-0"><button class="btn btn-soft btn-sm">Keluar</button></form>
      </div>
    </div>
  </div>
</nav>

<main class="container py-4">
  <c:if test="${not empty flashSuccess}">
    <div class="alert alert-success alert-dismissible fade show fw-bold" role="alert">
      <c:out value="${flashSuccess}"/>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Tutup"></button>
    </div>
  </c:if>
  <c:if test="${not empty flashError}">
    <div class="alert alert-danger alert-dismissible fade show fw-bold" role="alert">
      <c:out value="${flashError}"/>
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Tutup"></button>
    </div>
  </c:if>
