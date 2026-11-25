<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Anggaran"/>
  <jsp:param name="active" value="budgets"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Anggaran bulanan</h1>
  <button type="button" class="btn btn-primary" id="btnAddBudget" data-bs-toggle="modal" data-bs-target="#budgetModal">+ Tambah anggaran</button>
</div>

<div class="row g-3 mb-3">
  <div class="col-md-4">
    <div class="stat" style="background:#DDEBFA">
      <div class="muted">Total batas anggaran</div>
      <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalLimit}" maxFractionDigits="0"/></h2>
    </div>
  </div>
  <div class="col-md-4">
    <div class="stat" style="background:#FBD5E0">
      <div class="muted">Total terpakai</div>
      <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalUsed}" maxFractionDigits="0"/></h2>
    </div>
  </div>
  <div class="col-md-4">
    <div class="stat" style="background:#FFE9A8">
      <div class="muted">Total sisa anggaran</div>
      <h2 class="mt-2 mb-0 h4">Rp <fmt:formatNumber value="${totalRemaining}" maxFractionDigits="0"/></h2>
    </div>
  </div>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Kategori</th>
        <th>Periode</th>
        <th class="text-end">Batas</th>
        <th class="text-end">Terpakai</th>
        <th class="text-end">Sisa</th>
        <th style="min-width:150px">Progres</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="b" items="${budgets}">
      <c:set var="used" value="${usedAmounts[b.b_id]}"/>
      <c:set var="rem" value="${remainingAmounts[b.b_id]}"/>
      <c:set var="pct" value="${usagePercents[b.b_id]}"/>
      <c:set var="barClass" value="${pct > 100 ? 'bar-over' : (pct >= 80 ? 'bar-warn' : 'bar-ok')}"/>
      <tr>
        <td class="fw-bold"><c:out value="${categoryNames[b.categories_C_id]}"/></td>
        <td><c:out value="${b.b_month}"/>/<c:out value="${b.b_year}"/></td>
        <td class="text-end">Rp <fmt:formatNumber value="${b.b_amount_limit}" maxFractionDigits="0"/></td>
        <td class="text-end">Rp <fmt:formatNumber value="${used}" maxFractionDigits="0"/></td>
        <td class="text-end fw-bold">Rp <fmt:formatNumber value="${rem}" maxFractionDigits="0"/></td>
        <td>
          <div class="d-flex justify-content-between small muted mb-1">
            <span><c:out value="${pct}"/>%</span>
          </div>
          <div class="bar">
            <div class="${barClass}" style="width:${pct > 100 ? 100 : (pct < 0 ? 0 : pct)}%"></div>
          </div>
        </td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-budget"
                    data-bs-toggle="modal" data-bs-target="#budgetModal"
                    data-id="<c:out value='${b.b_id}'/>"
                    data-category="<c:out value='${b.categories_C_id}'/>"
                    data-limit="${b.b_amount_limit}"
                    data-month="${b.b_month}"
                    data-year="${b.b_year}">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-budget"
                    data-bs-toggle="modal" data-bs-target="#deleteBudgetModal"
                    data-id="<c:out value='${b.b_id}'/>"
                    data-label="<c:out value='${categoryNames[b.categories_C_id]} (${b.b_month}/${b.b_year})'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty budgets}">
      <tr><td colspan="7" class="text-center muted py-4">Belum ada anggaran. Klik "+ Tambah anggaran" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="budgetModal" tabindex="-1" aria-labelledby="budgetModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="budgetForm" method="POST" action="${ctx}/budgets" novalidate>
      <input type="hidden" name="action" id="budAction" value="add">
      <input type="hidden" name="B_id" id="budId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="budgetModalTitle">Tambah anggaran</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="budCategory">Kategori</label>
          <select id="budCategory" name="Categories_C_id" class="form-select" required>
            <c:forEach var="c" items="${categories}">
              <option value="<c:out value='${c.c_id}'/>"><c:out value="${c.c_name}"/> (<c:out value="${c.c_type}"/>)</option>
            </c:forEach>
          </select>
          <div class="invalid-feedback">Pilih kategori.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="budLimit">Batas anggaran (Rp)</label>
          <input id="budLimit" name="B_amount_limit" type="number" min="0.01" step="0.01" class="form-control" required>
          <div class="invalid-feedback">Batas anggaran harus lebih besar dari 0.</div>
        </div>
        <div class="row g-3">
          <div class="col-6">
            <label class="form-label" for="budMonth">Bulan (1-12)</label>
            <input id="budMonth" name="B_month" type="number" min="1" max="12" class="form-control" required value="11">
            <div class="invalid-feedback">Bulan 1 sampai 12.</div>
          </div>
          <div class="col-6">
            <label class="form-label" for="budYear">Tahun</label>
            <input id="budYear" name="B_year" type="number" min="2000" max="2100" class="form-control" required value="2026">
            <div class="invalid-feedback">Tahun wajib diisi.</div>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-primary">Simpan</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="deleteBudgetModal" tabindex="-1" aria-labelledby="delBudgetTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/budgets">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="B_id" id="delBudgetId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delBudgetTitle">Hapus anggaran?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Anggaran <b id="delBudgetLabel"></b> akan dihapus permanen.</div>
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