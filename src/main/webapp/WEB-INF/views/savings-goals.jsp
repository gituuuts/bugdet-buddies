<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Target Tabungan"/>
  <jsp:param name="active" value="savings"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Target tabungan</h1>
  <button type="button" class="btn btn-primary" id="btnAddGoal" data-bs-toggle="modal" data-bs-target="#goalModal">+ Tambah target</button>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Nama target</th>
        <th class="text-end">Terkumpul</th>
        <th class="text-end">Target</th>
        <th>Jatuh tempo</th>
        <th>Status</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="g" items="${savingsGoals}">
      <tr>
        <td class="fw-bold"><c:out value="${g.SG_name}"/></td>
        <td class="text-end">Rp <fmt:formatNumber value="${g.SG_current_amount}" maxFractionDigits="0"/></td>
        <td class="text-end">Rp <fmt:formatNumber value="${g.SG_target_amount}" maxFractionDigits="0"/></td>
        <td>
          <c:choose>
            <c:when test="${not empty g.SG_target_date}"><fmt:formatDate value="${g.SG_target_date}" pattern="dd MMM yyyy"/></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td><span class="badge rounded-pill badge-in"><c:out value="${g.SG_status}"/></span></td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-goal"
                    data-bs-toggle="modal" data-bs-target="#goalModal"
                    data-id="<c:out value='${g.SG_id}'/>"
                    data-name="<c:out value='${g.SG_name}'/>"
                    data-target="${g.SG_target_amount}"
                    data-current="${g.SG_current_amount}"
                    data-date="${g.SG_target_date}"
                    data-status="<c:out value='${g.SG_status}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-goal"
                    data-bs-toggle="modal" data-bs-target="#deleteGoalModal"
                    data-id="<c:out value='${g.SG_id}'/>"
                    data-name="<c:out value='${g.SG_name}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty savingsGoals}">
      <tr><td colspan="6" class="text-center muted py-4">Belum ada target tabungan. Klik "+ Tambah target" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="goalModal" tabindex="-1" aria-labelledby="goalModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="goalForm" method="POST" action="${ctx}/savings-goals" novalidate>
      <input type="hidden" name="action" id="goalAction" value="add">
      <input type="hidden" name="SG_id" id="goalId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="goalModalTitle">Tambah target tabungan</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="goalName">Nama target</label>
          <input id="goalName" name="SG_name" class="form-control" maxlength="100" required placeholder="mis. Dana Darurat">
          <div class="invalid-feedback">Nama target wajib diisi.</div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-6">
            <label class="form-label" for="goalTarget">Target nominal (Rp)</label>
            <input id="goalTarget" name="SG_target_amount" type="number" min="0.01" step="0.01" class="form-control" required>
            <div class="invalid-feedback">Target harus lebih dari 0.</div>
          </div>
          <div class="col-6">
            <label class="form-label" for="goalCurrent">Saldo terkumpul (Rp)</label>
            <input id="goalCurrent" name="SG_current_amount" type="number" min="0" step="0.01" class="form-control" required value="0">
            <div class="invalid-feedback">Saldo tidak boleh negatif.</div>
          </div>
        </div>
        <div class="row g-3">
          <div class="col-6">
            <label class="form-label" for="goalDate">Tanggal target</label>
            <input id="goalDate" name="SG_target_date" type="date" class="form-control">
          </div>
          <div class="col-6">
            <label class="form-label" for="goalStatus">Status</label>
            <select id="goalStatus" name="SG_status" class="form-select">
              <option value="Active">Active</option>
              <option value="Completed">Completed</option>
              <option value="Paused">Paused</option>
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

<div class="modal fade" id="deleteGoalModal" tabindex="-1" aria-labelledby="delGoalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/savings-goals">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="SG_id" id="delGoalId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delGoalTitle">Hapus target tabungan?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Target tabungan <b id="delGoalName"></b> akan dihapus permanen.</div>
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