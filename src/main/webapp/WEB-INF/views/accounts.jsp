<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Rekening &amp; Dompet"/>
  <jsp:param name="active" value="accounts"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Rekening &amp; dompet</h1>
  <button type="button" class="btn btn-primary" id="btnAddAccount" data-bs-toggle="modal" data-bs-target="#accountModal">+ Tambah rekening</button>
</div>

<div class="row g-3 mb-3">
  <div class="col-sm-6">
    <div class="stat" style="background:#DDEBFA">
      <div class="muted">Total saldo seluruh rekening</div>
      <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalBalance}" maxFractionDigits="0"/></h2>
    </div>
  </div>
  <div class="col-sm-6">
    <div class="stat" style="background:#FFE9A8">
      <div class="muted">Jumlah rekening aktif</div>
      <h2 class="mt-2 mb-0 h4"><c:out value="${accounts.size()}"/> rekening</h2>
    </div>
  </div>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Nama rekening</th>
        <th>Jenis</th>
        <th class="text-end">Saldo awal</th>
        <th class="text-end">Saldo berjalan</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="a" items="${accounts}">
      <c:set var="curBal" value="${balances[a.a_id]}"/>
      <tr>
        <td class="fw-bold"><c:out value="${a.a_name}"/></td>
        <td><span class="badge rounded-pill badge-in"><c:out value="${a.a_account_type}"/></span></td>
        <td class="text-end">Rp <fmt:formatNumber value="${a.a_initial_balance}" maxFractionDigits="0"/></td>
        <td class="text-end fw-bold">Rp <fmt:formatNumber value="${empty curBal ? a.a_initial_balance : curBal}" maxFractionDigits="0"/></td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-account"
                    data-bs-toggle="modal" data-bs-target="#accountModal"
                    data-id="<c:out value='${a.a_id}'/>"
                    data-name="<c:out value='${a.a_name}'/>"
                    data-type="<c:out value='${a.a_account_type}'/>"
                    data-balance="${a.a_initial_balance}">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-account"
                    data-bs-toggle="modal" data-bs-target="#deleteAccountModal"
                    data-id="<c:out value='${a.a_id}'/>"
                    data-name="<c:out value='${a.a_name}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty accounts}">
      <tr><td colspan="5" class="text-center muted py-4">Belum ada rekening. Klik "+ Tambah rekening" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="accountModal" tabindex="-1" aria-labelledby="accountModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="accountForm" method="POST" action="${ctx}/accounts" novalidate>
      <input type="hidden" name="action" id="accAction" value="add">
      <input type="hidden" name="A_id" id="accId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="accountModalTitle">Tambah rekening</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="accName">Nama rekening</label>
          <input id="accName" name="A_name" class="form-control" maxlength="100" required placeholder="mis. BCA Utama / Dompet Tunai">
          <div class="invalid-feedback">Nama rekening wajib diisi (maks. 100 karakter).</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="accType">Jenis rekening</label>
          <select id="accType" name="A_account_type" class="form-select" required>
            <option value="Bank">Bank</option>
            <option value="E-Wallet">E-Wallet</option>
            <option value="Cash">Cash</option>
            <option value="Savings">Savings</option>
          </select>
          <div class="invalid-feedback">Pilih jenis rekening.</div>
        </div>
        <div class="mb-1">
          <label class="form-label" for="accBalance">Saldo awal (Rp)</label>
          <input id="accBalance" name="A_initial_balance" type="number" min="0" step="0.01" class="form-control" required value="0">
          <div class="invalid-feedback">Saldo awal harus angka 0 atau lebih.</div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-primary">Simpan</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="deleteAccountModal" tabindex="-1" aria-labelledby="delAccountTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/accounts">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="A_id" id="delAccountId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delAccountTitle">Hapus rekening?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Rekening <b id="delAccountName"></b> akan dihapus permanen.</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-danger">Ya, hapus</button>
      </div>
    </form>
  </div>
</div>

<jsp:include page="partials/footer.jsp">
  <jsp:param name="script" value="/js/modules.js"/>
</jsp:include>