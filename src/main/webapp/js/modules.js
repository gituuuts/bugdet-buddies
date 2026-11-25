(function () {
  'use strict';

  function today() {
    var d = new Date();
    var m = String(d.getMonth() + 1).padStart(2, '0');
    var day = String(d.getDate()).padStart(2, '0');
    return d.getFullYear() + '-' + m + '-' + day;
  }

  function bindValidation(form) {
    if (!form) return;
    form.addEventListener('submit', function (e) {
      if (!form.checkValidity()) {
        e.preventDefault();
        e.stopPropagation();
      }
      form.classList.add('was-validated');
    });
  }

  function bindDeleteModal(btnSelector, idInputId, labelInputId) {
    document.querySelectorAll(btnSelector).forEach(function (btn) {
      btn.addEventListener('click', function () {
        var idEl = document.getElementById(idInputId);
        var lblEl = document.getElementById(labelInputId);
        if (idEl) idEl.value = btn.dataset.id || '';
        if (lblEl) lblEl.textContent = btn.dataset.name || btn.dataset.label || '';
      });
    });
  }

  // ----- ACCOUNTS -----
  var accForm = document.getElementById('accountForm');
  bindValidation(accForm);
  var btnAddAcc = document.getElementById('btnAddAccount');
  if (btnAddAcc && accForm) {
    btnAddAcc.addEventListener('click', function () {
      accForm.reset();
      accForm.classList.remove('was-validated');
      document.getElementById('accountModalTitle').textContent = 'Tambah rekening';
      document.getElementById('accAction').value = 'add';
      document.getElementById('accId').value = '';
      document.getElementById('accBalance').value = '0';
    });
  }
  document.querySelectorAll('.btn-edit-account').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      accForm.reset();
      accForm.classList.remove('was-validated');
      document.getElementById('accountModalTitle').textContent = 'Edit rekening';
      document.getElementById('accAction').value = 'update';
      document.getElementById('accId').value = d.id;
      document.getElementById('accName').value = d.name;
      document.getElementById('accType').value = d.type;
      document.getElementById('accBalance').value = d.balance;
    });
  });
  bindDeleteModal('.btn-delete-account', 'delAccountId', 'delAccountName');

  // ----- CATEGORIES -----
  var catForm = document.getElementById('categoryForm');
  bindValidation(catForm);
  var btnAddCat = document.getElementById('btnAddCategory');
  if (btnAddCat && catForm) {
    btnAddCat.addEventListener('click', function () {
      catForm.reset();
      catForm.classList.remove('was-validated');
      document.getElementById('categoryModalTitle').textContent = 'Tambah kategori';
      document.getElementById('catAction').value = 'add';
      document.getElementById('catId').value = '';
      document.getElementById('cTypeOut').checked = true;
    });
  }
  document.querySelectorAll('.btn-edit-category').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      catForm.reset();
      catForm.classList.remove('was-validated');
      document.getElementById('categoryModalTitle').textContent = 'Edit kategori';
      document.getElementById('catAction').value = 'update';
      document.getElementById('catId').value = d.id;
      document.getElementById('catName').value = d.name;
      if (d.type === 'Income') {
        document.getElementById('cTypeIn').checked = true;
      } else {
        document.getElementById('cTypeOut').checked = true;
      }
    });
  });
  bindDeleteModal('.btn-delete-category', 'delCategoryId', 'delCategoryName');

  // ----- BUDGETS -----
  var budForm = document.getElementById('budgetForm');
  bindValidation(budForm);
  var btnAddBud = document.getElementById('btnAddBudget');
  if (btnAddBud && budForm) {
    btnAddBud.addEventListener('click', function () {
      budForm.reset();
      budForm.classList.remove('was-validated');
      document.getElementById('budgetModalTitle').textContent = 'Tambah anggaran';
      document.getElementById('budAction').value = 'add';
      document.getElementById('budId').value = '';
      var now = new Date();
      document.getElementById('budMonth').value = String(now.getMonth() + 1);
      document.getElementById('budYear').value = String(now.getFullYear());
    });
  }
  document.querySelectorAll('.btn-edit-budget').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      budForm.reset();
      budForm.classList.remove('was-validated');
      document.getElementById('budgetModalTitle').textContent = 'Edit anggaran';
      document.getElementById('budAction').value = 'update';
      document.getElementById('budId').value = d.id;
      document.getElementById('budCategory').value = d.category;
      document.getElementById('budLimit').value = d.limit;
      document.getElementById('budMonth').value = d.month;
      document.getElementById('budYear').value = d.year;
    });
  });
  bindDeleteModal('.btn-delete-budget', 'delBudgetId', 'delBudgetLabel');

  // ----- SAVINGS GOALS -----
  var goalForm = document.getElementById('goalForm');
  bindValidation(goalForm);
  var btnAddGoal = document.getElementById('btnAddGoal');
  if (btnAddGoal && goalForm) {
    btnAddGoal.addEventListener('click', function () {
      goalForm.reset();
      goalForm.classList.remove('was-validated');
      document.getElementById('goalModalTitle').textContent = 'Tambah target tabungan';
      document.getElementById('goalAction').value = 'add';
      document.getElementById('goalId').value = '';
      document.getElementById('goalCurrent').value = '0';
      document.getElementById('goalStatus').value = 'Active';
    });
  }
  document.querySelectorAll('.btn-edit-goal').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      goalForm.reset();
      goalForm.classList.remove('was-validated');
      document.getElementById('goalModalTitle').textContent = 'Edit target tabungan';
      document.getElementById('goalAction').value = 'update';
      document.getElementById('goalId').value = d.id;
      document.getElementById('goalName').value = d.name;
      document.getElementById('goalTarget').value = d.target;
      document.getElementById('goalCurrent').value = d.current;
      document.getElementById('goalDate').value = d.date || '';
      document.getElementById('goalStatus').value = d.status || 'Active';
    });
  });
  bindDeleteModal('.btn-delete-goal', 'delGoalId', 'delGoalName');

  // ----- RECURRING TRANSACTIONS -----
  var recForm = document.getElementById('recurringForm');
  bindValidation(recForm);
  var btnAddRec = document.getElementById('btnAddRecurring');
  if (btnAddRec && recForm) {
    btnAddRec.addEventListener('click', function () {
      recForm.reset();
      recForm.classList.remove('was-validated');
      document.getElementById('recurringModalTitle').textContent = 'Tambah transaksi berulang';
      document.getElementById('recAction').value = 'add';
      document.getElementById('recId').value = '';
      document.getElementById('recStart').value = today();
      document.getElementById('recNext').value = today();
      document.getElementById('recStatus').value = 'Active';
    });
  }
  document.querySelectorAll('.btn-edit-recurring').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      recForm.reset();
      recForm.classList.remove('was-validated');
      document.getElementById('recurringModalTitle').textContent = 'Edit transaksi berulang';
      document.getElementById('recAction').value = 'update';
      document.getElementById('recId').value = d.id;
      document.getElementById('recTitle').value = d.title;
      document.getElementById('recType').value = d.type;
      document.getElementById('recAmount').value = d.amount;
      document.getElementById('recFrequency').value = d.frequency;
      document.getElementById('recStart').value = d.start || '';
      document.getElementById('recEnd').value = d.end || '';
      document.getElementById('recNext').value = d.next || '';
      document.getElementById('recStatus').value = d.status || 'Active';
      document.getElementById('recAccount').value = d.account;
      document.getElementById('recCategory').value = d.category;
    });
  });
  bindDeleteModal('.btn-delete-recurring', 'delRecurringId', 'delRecurringTitleText');

  // ----- DEBTS -----
  var debtForm = document.getElementById('debtForm');
  bindValidation(debtForm);
  var btnAddDebt = document.getElementById('btnAddDebt');
  if (btnAddDebt && debtForm) {
    btnAddDebt.addEventListener('click', function () {
      debtForm.reset();
      debtForm.classList.remove('was-validated');
      document.getElementById('debtModalTitle').textContent = 'Tambah utang / piutang';
      document.getElementById('debtAction').value = 'add';
      document.getElementById('debtId').value = '';
      document.getElementById('debtStatus').value = 'Unpaid';
    });
  }
  document.querySelectorAll('.btn-edit-debt').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      debtForm.reset();
      debtForm.classList.remove('was-validated');
      document.getElementById('debtModalTitle').textContent = 'Edit utang / piutang';
      document.getElementById('debtAction').value = 'update';
      document.getElementById('debtId').value = d.id;
      document.getElementById('debtName').value = d.name;
      document.getElementById('debtType').value = d.type;
      document.getElementById('debtTotal').value = d.total;
      document.getElementById('debtDue').value = d.due || '';
      document.getElementById('debtStatus').value = d.status || 'Unpaid';
    });
  });
  bindDeleteModal('.btn-delete-debt', 'delDebtId', 'delDebtName');

  // ----- DEBT PAYMENTS -----
  var payForm = document.getElementById('paymentForm');
  bindValidation(payForm);
  var btnAddPay = document.getElementById('btnAddPayment');
  if (btnAddPay && payForm) {
    btnAddPay.addEventListener('click', function () {
      payForm.reset();
      payForm.classList.remove('was-validated');
      document.getElementById('paymentModalTitle').textContent = 'Catat pembayaran';
      document.getElementById('payAction').value = 'add';
      document.getElementById('payId').value = '';
      document.getElementById('payDate').value = today();
    });
  }
  document.querySelectorAll('.btn-edit-payment').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var d = btn.dataset;
      payForm.reset();
      payForm.classList.remove('was-validated');
      document.getElementById('paymentModalTitle').textContent = 'Edit pembayaran';
      document.getElementById('payAction').value = 'update';
      document.getElementById('payId').value = d.id;
      document.getElementById('payDebt').value = d.debt;
      document.getElementById('payAmount').value = d.amount;
      document.getElementById('payDate').value = d.date || '';
      document.getElementById('payNotes').value = d.notes || '';
    });
  });
  bindDeleteModal('.btn-delete-payment', 'delPaymentId', 'delPaymentLabel');
})();