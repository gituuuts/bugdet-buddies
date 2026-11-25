<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<jsp:include page="partials/header.jsp">
  <jsp:param name="title" value="Kategori"/>
  <jsp:param name="active" value="categories"/>
</jsp:include>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h1 class="h3 m-0">Kategori transaksi</h1>
  <button type="button" class="btn btn-primary" id="btnAddCategory" data-bs-toggle="modal" data-bs-target="#categoryModal">+ Tambah kategori</button>
</div>

<div class="card p-3 table-responsive">
  <table class="table align-middle mb-0">
    <thead>
      <tr>
        <th>Nama kategori</th>
        <th>Jenis</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="c" items="${categories}">
      <tr>
        <td class="fw-bold"><c:out value="${c.c_name}"/></td>
        <td>
          <span class="badge rounded-pill ${c.c_type == 'Income' ? 'badge-in' : 'badge-out'}">
            ${c.c_type == 'Income' ? 'Pemasukan' : 'Pengeluaran'}
          </span>
        </td>
        <td>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-soft btn-sm btn-edit-category"
                    data-bs-toggle="modal" data-bs-target="#categoryModal"
                    data-id="<c:out value='${c.c_id}'/>"
                    data-name="<c:out value='${c.c_name}'/>"
                    data-type="<c:out value='${c.c_type}'/>">Edit</button>
            <button type="button" class="btn btn-danger btn-sm btn-delete-category"
                    data-bs-toggle="modal" data-bs-target="#deleteCategoryModal"
                    data-id="<c:out value='${c.c_id}'/>"
                    data-name="<c:out value='${c.c_name}'/>">Hapus</button>
          </div>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty categories}">
      <tr><td colspan="3" class="text-center muted py-4">Belum ada kategori. Klik "+ Tambah kategori" untuk mulai.</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="categoryModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" id="categoryForm" method="POST" action="${ctx}/categories" novalidate>
      <input type="hidden" name="action" id="catAction" value="add">
      <input type="hidden" name="C_id" id="catId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="categoryModalTitle">Tambah kategori</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">
        <div class="mb-3">
          <label class="form-label" for="catName">Nama kategori</label>
          <input id="catName" name="C_name" class="form-control" maxlength="100" required placeholder="mis. Makanan, Gaji">
          <div class="invalid-feedback">Nama kategori wajib diisi (maks. 100 karakter).</div>
        </div>
        <div class="seg mb-1">
          <input type="radio" name="C_type" id="cTypeOut" value="Expense" checked><label for="cTypeOut" class="l-out">Pengeluaran</label>
          <input type="radio" name="C_type" id="cTypeIn" value="Income"><label for="cTypeIn" class="l-in">Pemasukan</label>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-soft" data-bs-dismiss="modal">Batal</button>
        <button type="submit" class="btn btn-primary">Simpan</button>
      </div>
    </form>
  </div>
</div>

<div class="modal fade" id="deleteCategoryModal" tabindex="-1" aria-labelledby="delCategoryTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="POST" action="${ctx}/categories">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="C_id" id="delCategoryId" value="">
      <div class="modal-header">
        <h2 class="modal-title h5" id="delCategoryTitle">Hapus kategori?</h2>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
      </div>
      <div class="modal-body">Kategori <b id="delCategoryName"></b> akan dihapus permanen.</div>
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