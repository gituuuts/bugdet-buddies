<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="id_ID"/>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Transaksi Berulang"/>
  <jsp:param name="active" value="recurring"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Transaksi berulang</h1>
  <button type="button" class="btn btn-primary" id="btnAddRecurring" data-bs-toggle="modal" data-bs-target="#recurringModal">+ Tambah jadwal</button>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Judul</th>
        <th>Jenis</th>
        <th class="text-end">Jumlah</th>
        <th>Frekuensi</th>
        <th>Jadwal berikutnya</th>
        <th>Akun &amp; Kategori</th>
        <th>Status</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="rt" items="${recurringTransactions}">
      <tr>
        <td class="fw-bold"><c:out value="${rt.RT_title}"/></td>
        <td>
          <span class="badge rounded-pill ${rt.RT_type == 'Income' ? 'badge-in' : 'badge-out'}">
            ${rt.RT_type == 'Income' ? 'Pemasukan' : 'Pengeluaran'}
          </span>
        </td>
        <td class="text-end">Rp <fmt:formatNumber value="${rt.RT_amount}" maxFractionDigits="0"/></td>
        <td><c:out value="${rt.RT_frequency}"/></td>
        <td><fmt:formatDate value="${rt.RT_next_date}" pattern="dd MMM yyyy"/></td>
        <td>
          <div><c:out value="${accountNames[rt.accounts_A_id]}"/></div>
          <div class="small muted"><c:out value="${categoryNames[rt.categories_C_id]}"/></div>
        </td>
        <td><span class="badge rounded-pill badge-in"><c:out value="${rt.RT_status}"/></span></td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-recurring"
                    data-bs-toggle="modal" data-bs-target="#recurringModal"
                    data-id="<c:out value='${rt.RT_id}'/>"
                    data-title="<c:out value='${rt.RT_title}'/>"
                    data-type="<c:out value='${rt.RT_type}'/>"
                    data-amount="${rt.RT_amount}"
                    data-frequency="<c:out value='${rt.RT_frequency}'/>"
                    data-start="${rt.RT_start_date}"
                    data-end="${rt.RT_end_date}"
                    data-next="${rt.RT_next_date}"
                    data-status="<c:out value='${rt.RT_status}'/>"
                    data-account="<c:out value='${rt.accounts_A_id}'/>"
                    data-category="<c:out value='${rt.categories_C_id}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-recurring"
                    data-bs-toggle="modal" data-bs-target="#deleteRecurringModal"
                    data-id="<c:out value='${rt.RT_id}'/>"
                    data-title="<c:out value='${rt.RT_title}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty recurringTransactions}">
      <tr><td colspan="8" class="text-center muted py-4">Belum ada jadwal transaksi berulang. Klik "+ Tambah jadwal" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="recurringModal" tabindex="-1" aria-labelledby="recurringModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="recurringForm" method="POST" action="${ctx}/recurring-transactions" novalidate>
      <input type="hidden" name="action" id="recAction" value="add">
      <input type="hidden" name="RT_id" id="recId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="recurringModalTitle">Tambah transaksi berulang</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="recTitle">Judul</label>
          <input id="recTitle" name="RT_title" class="form-control" maxlength="100" required placeholder="mis. Sewa Kost / Gaji Bulanan">
          <div class="invalid-feedback">Judul wajib diisi.</div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-6">
            <label class="form-label" for="recType">Jenis</label>
            <select id="recType" name="RT_type" class="form-select" required>
              <option value="Expense">Pengeluaran</option>
              <option value="Income">Pemasukan</option>
            </select>
          </div>
          <div class="col-6">
            <label class="form-label" for="recAmount">Jumlah (Rp)</label>
            <input id="recAmount" name="RT_amount" type="number" min="0.01" step="0.01" class="form-control" required>
            <div class="invalid-feedback">Jumlah harus lebih dari 0.</div>
          </div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-6">
            <label class="form-label" for="recFrequency">Frekuensi</label>
            <select id="recFrequency" name="RT_frequency" class="form-select" required>
              <option value="Daily">Daily</option>
              <option value="Weekly">Weekly</option>
              <option value="Monthly" selected>Monthly</option>
              <option value="Yearly">Yearly</option>
            </select>
          </div>
          <div class="col-6">
            <label class="form-label" for="recStatus">Status</label>
            <select id="recStatus" name="RT_status" class="form-select">
              <option value="Active">Active</option>
              <option value="Paused">Paused</option>
            </select>
          </div>
        </div>
        <div class="row g-3 mb-3">
          <div class="col-4">
            <label class="form-label" for="recStart">Mulai</label>
            <input id="recStart" name="RT_start_date" type="date" class="form-control" required>
          </div>
          <div class="col-4">
            <label class="form-label" for="recNext">Berikutnya</label>
            <input id="recNext" name="RT_next_date" type="date" class="form-control" required>
          </div>
          <div class="col-4">
            <label class="form-label" for="recEnd">Berakhir</label>
            <input id="recEnd" name="RT_end_date" type="date" class="form-control">
          </div>
        </div>
        <div class="row g-3">
          <div class="col-6">
            <label class="form-label" for="recAccount">Akun</label>
            <select id="recAccount" name="Accounts_A_id" class="form-select" required>
              <c:forEach var="a" items="${accounts}">
                <option value="<c:out value='${a.a_id}'/>"><c:out value="${a.a_name}"/></option>
              </c:forEach>
            </select>
          </div>
          <div class="col-6">
            <label class="form-label" for="recCategory">Kategori</label>
            <select id="recCategory" name="Categories_C_id" class="form-select" required>
              <c:forEach var="c" items="${categories}">
                <option value="<c:out value='${c.c_id}'/>"><c:out value="${c.c_name}"/></option>
              </c:forEach>
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

<div class="modal fade" id="deleteRecurringModal" tabindex="-1" aria-labelledby="delRecurringTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/recurring-transactions">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="RT_id" id="delRecurringId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delRecurringTitle">Hapus jadwal?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Jadwal <b id="delRecurringTitleText"></b> akan dihapus permanen.</div>
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