<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Utang &amp; Piutang"/>
  <jsp:param name="active" value="debts"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Utang &amp; piutang</h1>
  <button type="button" class="btn btn-primary" id="btnAddDebt" data-bs-toggle="modal" data-bs-target="#debtModal">+ Tambah data</button>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Nama / Keterangan</th>
        <th>Jenis</th>
        <th class="text-end">Total</th>
        <th class="text-end">Sisa</th>
        <th>Jatuh tempo</th>
        <th>Status</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="d" items="${debts}">
      <tr>
        <td class="fw-bold"><c:out value="${d.d_name}"/></td>
        <td>
          <span class="badge rounded-pill ${d.d_type == 'Receivable' ? 'badge-in' : 'badge-out'}">
            ${d.d_type == 'Receivable' ? 'Piutang' : 'Utang'}
          </span>
        </td>
        <td class="text-end">Rp <fmt:formatNumber value="${d.d_total_amount}" maxFractionDigits="0"/></td>
        <td class="text-end fw-bold">Rp <fmt:formatNumber value="${d.d_remaining_amount}" maxFractionDigits="0"/></td>
        <td>
          <c:choose>
            <c:when test="${not empty d.d_due_date}"><fmt:formatDate value="${d.d_due_date}" pattern="dd MMM yyyy"/></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td><span class="badge rounded-pill badge-in"><c:out value="${d.d_status}"/></span></td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-debt"
                    data-bs-toggle="modal" data-bs-target="#debtModal"
                    data-id="<c:out value='${d.d_id}'/>"
                    data-name="<c:out value='${d.d_name}'/>"
                    data-type="<c:out value='${d.d_type}'/>"
                    data-total="${d.d_total_amount}"
                    data-due="${d.d_due_date}"
                    data-status="<c:out value='${d.d_status}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-debt"
                    data-bs-toggle="modal" data-bs-target="#deleteDebtModal"
                    data-id="<c:out value='${d.d_id}'/>"
                    data-name="<c:out value='${d.d_name}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty debts}">
      <tr><td colspan="7" class="text-center muted py-4">Belum ada data utang atau piutang. Klik "+ Tambah data" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="debtModal" tabindex="-1" aria-labelledby="debtModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="debtForm" method="POST" action="${ctx}/debts" novalidate>
      <input type="hidden" name="action" id="debtAction" value="add">
      <input type="hidden" name="D_id" id="debtId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="debtModalTitle">Tambah utang / piutang</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="debtName">Nama / keterangan</label>
          <input id="debtName" name="D_name" class="form-control" maxlength="100" required placeholder="mis. Pinjaman Buku / Piutang Teman">
          <div class="invalid-feedback">Nama wajib diisi.</div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-6">
            <label class="form-label" for="debtType">Jenis</label>
            <select id="debtType" name="D_type" class="form-select" required>
              <option value="Debt">Utang (Debt)</option>
              <option value="Receivable">Piutang (Receivable)</option>
            </select>
          </div>
          <div class="col-6">
            <label class="form-label" for="debtTotal">Total nominal (Rp)</label>
            <input id="debtTotal" name="D_total_amount" type="number" min="0.01" step="0.01" class="form-control" required>
            <div class="invalid-feedback">Total nominal harus lebih dari 0.</div>
          </div>
        </div>
        <div class="row g-3">
          <div class="col-6">
            <label class="form-label" for="debtDue">Jatuh tempo</label>
            <input id="debtDue" name="D_due_date" type="date" class="form-control">
          </div>
          <div class="col-6">
            <label class="form-label" for="debtStatus">Status</label>
            <select id="debtStatus" name="D_status" class="form-select">
              <option value="Unpaid">Unpaid</option>
              <option value="Partial">Partial</option>
              <option value="Paid">Paid</option>
            </select>
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

<div class="modal fade" id="deleteDebtModal" tabindex="-1" aria-labelledby="delDebtTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/debts">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="D_id" id="delDebtId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delDebtTitle">Hapus data?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Data <b id="delDebtName"></b> akan dihapus permanen.</div>
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