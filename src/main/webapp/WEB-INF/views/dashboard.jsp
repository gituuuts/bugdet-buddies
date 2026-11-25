<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Dashboard"/>
  <jsp:param name="active" value="dashboard"/>
</jsp:include>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
  <div>
    <h1 class="h3 m-0">Ringkasan keuangan</h1>
    <div class="muted small">Pantau saldo, pemasukan, pengeluaran, dan akses cepat modul</div>
  </div>
  <a href="${ctx}/transactions" class="btn btn-primary">+ Kelola transaksi</a>
</div>

<div class="row g-3 mb-4">
  <div class="col-md-4">
    <div class="stat" style="background:#DDEBFA">
      <div class="muted">Total saldo</div>
      <h2 class="mt-2 mb-0 h4" id="dashBalance" data-value="${totalBalance}">Rp <fmt:formatNumber value="${totalBalance}" maxFractionDigits="0"/></h2>
    </div>
  </div>
  <div class="col-md-4">
    <div class="stat" style="background:#FFE9A8">
      <div class="muted">Total pemasukan</div>
      <h2 class="mt-2 mb-0 h4" id="dashIncome" data-value="${totalIncome}">Rp <fmt:formatNumber value="${totalIncome}" maxFractionDigits="0"/></h2>
    </div>
  </div>
  <div class="col-md-4">
    <div class="stat" style="background:#FBD5E0">
      <div class="muted">Total pengeluaran</div>
      <h2 class="mt-2 mb-0 h4" id="dashExpense" data-value="${totalExpense}">Rp <fmt:formatNumber value="${totalExpense}" maxFractionDigits="0"/></h2>
    </div>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-lg-6">
    <div class="card p-3 h-100">
      <h2 class="h5 mb-3">Perbandingan pemasukan &amp; pengeluaran</h2>
      <canvas id="cashflowChart" height="180"></canvas>
    </div>
  </div>
  <div class="col-lg-6">
    <div class="card p-3 h-100">
      <h2 class="h5 mb-3">Pintasan modul CRUD</h2>
      <div class="row g-2">
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/accounts">Rekening (<c:out value="${accountCount}"/>)</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/categories">Kategori (<c:out value="${categoryCount}"/>)</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/transactions">Transaksi</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/budgets">Anggaran (<c:out value="${budgetCount}"/>)</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/savings-goals">Target Tabungan (<c:out value="${savingsCount}"/>)</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/recurring-transactions">Transaksi Berulang</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/debts">Utang &amp; Piutang (<c:out value="${debtCount}"/>)</a></div>
        <div class="col-6"><a class="btn btn-soft w-100 text-start" href="${ctx}/debt-payments">Pembayaran Utang</a></div>
      </div>
    </div>
  </div>
</div>

<div class="card p-3 table-responsive">
  <div class="d-flex justify-content-between align-items-center mb-2">
    <h2 class="h5 m-0">Transaksi terbaru</h2>
    <a href="${ctx}/transactions" class="small fw-bold">Lihat semua</a>
  </div>
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Tanggal</th>
        <th>Judul</th>
        <th>Kategori</th>
        <th>Akun</th>
        <th>Jenis</th>
        <th class="text-end">Jumlah</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="t" items="${recentTransactions}">
      <tr>
        <td><fmt:formatDate value="${t.t_transaction_date}" pattern="dd MMM yyyy"/></td>
        <td class="fw-bold"><c:out value="${t.t_title}"/></td>
        <td><c:out value="${categoryNames[t.categories_C_id]}"/></td>
        <td><c:out value="${accountNames[t.accounts_A_id]}"/></td>
        <td>
          <span class="badge rounded-pill ${t.t_type == 'Income' ? 'badge-in' : 'badge-out'}">
            ${t.t_type == 'Income' ? 'Pemasukan' : 'Pengeluaran'}
          </span>
        </td>
        <td class="text-end">${t.t_type == 'Income' ? '+' : '−'} Rp <fmt:formatNumber value="${t.t_amount}" maxFractionDigits="0"/></td>
      </tr>
    </c:forEach>
    <c:if test="${empty recentTransactions}">
      <tr><td colspan="6" class="text-center muted py-4">Belum ada transaksi terbaru.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<jsp:include page="partials/footer.jsp">
  <jsp:param name="script" value="/js/dashboard.js"/>
</jsp:include>