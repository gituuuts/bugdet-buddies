<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Transaksi"/>
  <jsp:param name="active" value="transactions"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Riwayat transaksi</h1>
  <button type="button" class="btn btn-primary" id="btnAdd" data-bs-toggle="modal" data-bs-target="#txModal">+ Tambah</button>
</div>

<%-- Ringkasan sesuai hasil filter --%>
<div class="row g-3 mb-3">
  <div class="col-sm-6"><div class="stat" style="background:#FFE9A8"><div class="muted">Total pemasukan</div>
    <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalIncome}" maxFractionDigits="0"/></h2></div></div>
  <div class="col-sm-6"><div class="stat" style="background:#FBD5E0"><div class="muted">Total pengeluaran</div>
    <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalExpense}" maxFractionDigits="0"/></h2></div></div>
</div>

<%-- Cari judul + filter tanggal dan jenis (GET) --%>
<form method="GET" action="${ctx}/transactions" class="card p-3 mb-3">
  <div class="row g-3 align-items-end">
    <div class="col-md-3">
      <label class="form-label" for="q">Cari judul</label>
      <input id="q" type="search" name="q" value="<c:out value='${filterQ}'/>" class="form-control" placeholder="mis. makan siang">
    </div>
    <div class="col-md-2">
      <label class="form-label" for="from">Dari tanggal</label>
      <input id="from" type="date" name="from" value="<c:out value='${filterFrom}'/>" class="form-control">
    </div>
    <div class="col-md-2">
      <label class="form-label" for="to">Sampai tanggal</label>
      <input id="to" type="date" name="to" value="<c:out value='${filterTo}'/>" class="form-control">
    </div>
    <div class="col-md-2">
      <label class="form-label" for="type">Jenis</label>
      <select id="type" name="type" class="form-select">
        <option value="">Semua</option>
        <option value="Income"  ${filterType == 'Income'  ? 'selected' : ''}>Pemasukan</option>
        <option value="Expense" ${filterType == 'Expense' ? 'selected' : ''}>Pengeluaran</option>
      </select>
    </div>
    <div class="col-md-3 d-flex gap-2">
      <button class="btn btn-primary flex-fill">Terapkan</button>
      <a href="${ctx}/transactions" class="btn btn-soft">Reset</a>
    </div>
  </div>
</form>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr><th>Tanggal</th><th>Judul</th><th>Kategori</th><th>Akun</th><th>Jenis</th><th class="text-end">Jumlah</th><th>Aksi</th></tr>
    </thead>
    <tbody>
    <c:forEach var="t" items="${transactions}">
      <tr>
        <td><fmt:formatDate value="${t.t_transaction_date}" pattern="dd MMM yyyy"/></td>
        <td>
          <div class="fw-bold"><c:out value="${t.t_title}"/></div>
          <c:if test="${not empty t.t_description}"><div class="muted small"><c:out value="${t.t_description}"/></div></c:if>
        </td>
        <td><c:out value="${categoryNames[t.categories_C_id]}"/></td>
        <td><c:out value="${accountNames[t.accounts_A_id]}"/></td>
        <td>
          <span class="badge rounded-pill ${t.t_type == 'Income' ? 'badge-in' : 'badge-out'}">
            ${t.t_type == 'Income' ? 'Pemasukan' : 'Pengeluaran'}
          </span>
        </td>
        <td class="text-end">${t.t_type == 'Income' ? '+' : '−'} Rp <fmt:formatNumber value="${t.t_amount}" maxFractionDigits="0"/></td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit"
                    data-bs-toggle="modal" data-bs-target="#txModal"
                    data-id="<c:out value='${t.t_id}'/>"
                    data-title="<c:out value='${t.t_title}'/>"
                    data-description="<c:out value='${t.t_description}'/>"
                    data-type="<c:out value='${t.t_type}'/>"
                    data-amount="${t.t_amount}"
                    data-date="${t.t_transaction_date}"
                    data-account="<c:out value='${t.accounts_A_id}'/>"
                    data-category="<c:out value='${t.categories_C_id}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete"
                    data-bs-toggle="modal" data-bs-target="#deleteModal"
                    data-id="<c:out value='${t.t_id}'/>"
                    data-title="<c:out value='${t.t_title}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty transactions}">
      <tr><td colspan="7" class="text-center muted py-4">
        <c:choose>
          <c:when test="${not empty filterQ or not empty filterFrom or not empty filterTo or not empty filterType}">Tidak ada transaksi yang cocok dengan filter.</c:when>
          <c:otherwise>Belum ada transaksi. Klik "+ Tambah" untuk mulai 🌱</c:otherwise>
        </c:choose>
      </td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<%-- ===== Modal tambah / edit (satu form, mode diatur oleh JS) ===== --%>
<div class="modal fade" id="txModal" tabindex="-1" aria-labelledby="txModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="txForm" method="POST" action="${ctx}/transactions" novalidate>
      <input type="hidden" name="action" id="fAction" value="add">
      <input type="hidden" name="T_id" id="fId" value="">

      <div class="modal-header">
        <h2 class="modal-title h5" id="txModalTitle">Catat transaksi</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>

      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="fTitle">Judul</label>
          <input id="fTitle" name="T_title" class="form-control" maxlength="100" required placeholder="mis. Makan siang">
          <div class="invalid-feedback">Judul wajib diisi (maks. 100 karakter).</div>
        </div>

        <div class="seg mb-3">
          <input type="radio" name="T_type" id="tOut" value="Expense" checked><label for="tOut" class="l-out">Pengeluaran</label>
          <input type="radio" name="T_type" id="tIn"  value="Income"><label for="tIn" class="l-in">Pemasukan</label>
        </div>

        <div class="mb-3">
          <label class="form-label" for="fAmount">Jumlah (Rp)</label>
          <input id="fAmount" name="T_amount" type="number" min="0.01" step="0.01" class="form-control" required>
          <div class="invalid-feedback">Jumlah harus lebih besar dari 0.</div>
        </div>

        <div class="row g-3 mb-3">
          <div class="col-md-6">
            <label class="form-label" for="fCategory">Kategori</label>
            <select id="fCategory" name="Categories_C_id" class="form-select" required>
              <c:forEach var="c" items="${categories}">
                <option value="<c:out value='${c.c_id}'/>" data-type="<c:out value='${c.c_type}'/>"><c:out value="${c.c_name}"/></option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Pilih kategori.</div>
            <c:if test="${empty categories}"><div class="small muted mt-1">Belum ada kategori. <a href="${ctx}/categories">Buat dulu</a></div></c:if>
          </div>
          <div class="col-md-6">
            <label class="form-label" for="fAccount">Akun</label>
            <select id="fAccount" name="Accounts_A_id" class="form-select" required>
              <c:forEach var="a" items="${accounts}">
                <option value="<c:out value='${a.a_id}'/>"><c:out value="${a.a_name}"/></option>
              </c:forEach>
            </select>
            <div class="invalid-feedback">Pilih akun.</div>
            <c:if test="${empty accounts}"><div class="small muted mt-1">Belum ada akun. <a href="${ctx}/accounts">Buat dulu</a></div></c:if>
          </div>
        </div>

        <div class="mb-3">
          <label class="form-label" for="fDate">Tanggal</label>
          <input id="fDate" name="T_transaction_date" type="date" class="form-control" required>
          <div class="invalid-feedback">Tanggal wajib diisi.</div>
        </div>

        <div class="mb-1">
          <label class="form-label" for="fDesc">Deskripsi <span class="muted">(opsional)</span></label>
          <textarea id="fDesc" name="T_description" class="form-control" rows="2" maxlength="255"></textarea>
        </div>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-primary">Simpan</button>
      </div>
    </form>
  </div>
</div>

<%-- ===== Modal konfirmasi hapus ===== --%>
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/transactions">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="T_id" id="delId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="deleteModalTitle">Hapus transaksi?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Transaksi <b id="delTitle"></b> akan dihapus permanen.</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-danger">Ya, hapus</button>
      </div>
    </form>
  </div>
</div>

<jsp:include page="partials/footer.jsp">
  <jsp:param name="script" value="/js/transactions.js"/>
</jsp:include>
