<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Pembayaran Utang"/>
  <jsp:param name="active" value="debt-payments"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Riwayat pembayaran utang &amp; piutang</h1>
  <button type="button" class="btn btn-primary" id="btnAddPayment" data-bs-toggle="modal" data-bs-target="#paymentModal">+ Catat pembayaran</button>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Tanggal</th>
        <th>Utang / Piutang</th>
        <th class="text-end">Nominal</th>
        <th>Catatan</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="dp" items="${debtPayments}">
      <tr>
        <td><fmt:formatDate value="${dp.DP_payment_date}" pattern="dd MMM yyyy"/></td>
        <td class="fw-bold"><c:out value="${debtNames[dp.debts_D_id]}"/></td>
        <td class="text-end">Rp <fmt:formatNumber value="${dp.DP_amount}" maxFractionDigits="0"/></td>
        <td>
          <c:choose>
            <c:when test="${not empty dp.DP_notes}"><c:out value="${dp.DP_notes}"/></c:when>
            <c:otherwise><span class="muted">-</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-payment"
                    data-bs-toggle="modal" data-bs-target="#paymentModal"
                    data-id="<c:out value='${dp.DP_id}'/>"
                    data-debt="<c:out value='${dp.debts_D_id}'/>"
                    data-amount="${dp.DP_amount}"
                    data-date="${dp.DP_payment_date}"
                    data-notes="<c:out value='${dp.DP_notes}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-payment"
                    data-bs-toggle="modal" data-bs-target="#deletePaymentModal"
                    data-id="<c:out value='${dp.DP_id}'/>"
                    data-label="<c:out value='${debtNames[dp.debts_D_id]}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty debtPayments}">
      <tr><td colspan="5" class="text-center muted py-4">Belum ada riwayat pembayaran. Klik "+ Catat pembayaran" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="paymentModal" tabindex="-1" aria-labelledby="paymentModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="paymentForm" method="POST" action="${ctx}/debt-payments" novalidate>
      <input type="hidden" name="action" id="payAction" value="add">
      <input type="hidden" name="DP_id" id="payId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="paymentModalTitle">Catat pembayaran</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="payDebt">Utang / piutang</label>
          <select id="payDebt" name="Debts_D_id" class="form-select" required>
            <c:forEach var="d" items="${debts}">
              <option value="<c:out value='${d.d_id}'/>">
                <c:out value="${d.d_name}"/> (Sisa: Rp <fmt:formatNumber value="${d.d_remaining_amount}" maxFractionDigits="0"/>)
              </option>
            </c:forEach>
          </select>
          <div class="invalid-feedback">Pilih utang atau piutang.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="payAmount">Nominal pembayaran (Rp)</label>
          <input id="payAmount" name="DP_amount" type="number" min="0.01" step="0.01" class="form-control" required>
          <div class="invalid-feedback">Nominal harus lebih dari 0.</div>
        </div>
        <div class="mb-3">
          <label class="form-label" for="payDate">Tanggal pembayaran</label>
          <input id="payDate" name="DP_payment_date" type="date" class="form-control" required>
          <div class="invalid-feedback">Tanggal wajib diisi.</div>
        </div>
        <div class="mb-1">
          <label class="form-label" for="payNotes">Catatan <span class="muted">(opsional)</span></label>
          <input id="payNotes" name="DP_notes" class="form-control" maxlength="255" placeholder="mis. Cicilan ke-1">
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-primary">Simpan</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="deletePaymentModal" tabindex="-1" aria-labelledby="delPaymentTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/debt-payments">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="DP_id" id="delPaymentId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delPaymentTitle">Hapus pembayaran?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Pembayaran untuk <b id="delPaymentLabel"></b> akan dihapus dan sisa utang akan disesuaikan.</div>
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