(function () {
  'use strict';

  var form = document.getElementById('txForm');
  if (!form) return;

  var el = {
    title: document.getElementById('txModalTitle'),
    action: document.getElementById('fAction'),
    id: document.getElementById('fId'),
    name: document.getElementById('fTitle'),
    amount: document.getElementById('fAmount'),
    category: document.getElementById('fCategory'),
    account: document.getElementById('fAccount'),
    date: document.getElementById('fDate'),
    desc: document.getElementById('fDesc'),
    typeOut: document.getElementById('tOut'),
    typeIn: document.getElementById('tIn')
  };

  function today() {
    var d = new Date();
    var m = String(d.getMonth() + 1).padStart(2, '0');
    var day = String(d.getDate()).padStart(2, '0');
    return d.getFullYear() + '-' + m + '-' + day;
  }

  function currentType() {
    return el.typeIn.checked ? 'Income' : 'Expense';
  }

  // Tampilkan hanya kategori yang jenisnya sama dengan jenis transaksi.
  function filterCategories(keepValue) {
    var type = currentType();
    var firstVisible = null;
    Array.prototype.forEach.call(el.category.options, function (opt) {
      var match = !opt.dataset.type || opt.dataset.type === type;
      opt.hidden = !match;
      opt.disabled = !match;
      if (match && !firstVisible) firstVisible = opt;
    });
    var selected = el.category.options[el.category.selectedIndex];
    var wanted = keepValue ? Array.prototype.find.call(el.category.options, function (o) {
      return o.value === keepValue && !o.disabled;
    }) : null;
    if (wanted) {
      el.category.value = wanted.value;
    } else if (!selected || selected.disabled) {
      el.category.value = firstVisible ? firstVisible.value : '';
    }
  }

  function resetValidation() {
    form.classList.remove('was-validated');
  }

  // ----- Mode tambah -----
  document.getElementById('btnAdd').addEventListener('click', function () {
    form.reset();
    resetValidation();
    el.title.textContent = 'Catat transaksi';
    el.action.value = 'add';
    el.id.value = '';
    el.typeOut.checked = true;
    el.date.value = today();
    filterCategories(null);
  });

  // ----- Mode edit -----
  document.querySelectorAll('.btn-edit').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      form.reset();
      resetValidation();
      el.title.textContent = 'Edit transaksi';
      el.action.value = 'update';
      el.id.value = d.id;
      el.name.value = d.title;
      el.desc.value = d.description || '';
      el.amount.value = d.amount;
      el.date.value = d.date;
      el.account.value = d.account;
      (d.type === 'Income' ? el.typeIn : el.typeOut).checked = true;
      filterCategories(d.category);
    });
  });

  // Ganti jenis -> sesuaikan daftar kategori
  [el.typeOut, el.typeIn].forEach(function (r) {
    r.addEventListener('change', function () { filterCategories(null); });
  });

  // ----- Validasi sisi klien -----
  form.addEventListener('submit', function (e) {
    if (!form.checkValidity()) {
      e.preventDefault();
      e.stopPropagation();
    }
    form.classList.add('was-validated');
  });

  // ----- Konfirmasi hapus -----
  document.querySelectorAll('.btn-delete').forEach(function (btn) {
    btn.addEventListener('click', function () {
      document.getElementById('delId').value = btn.dataset.id;
      document.getElementById('delTitle').textContent = btn.dataset.title;
    });
  });

  filterCategories(null);
})();
