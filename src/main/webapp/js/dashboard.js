(function () {
  'use strict';

  var canvas = document.getElementById('cashflowChart');
  if (!canvas || typeof Chart === 'undefined') return;

  var incEl = document.getElementById('dashIncome');
  var expEl = document.getElementById('dashExpense');
  var income = incEl ? parseFloat(incEl.dataset.value || '0') : 0;
  var expense = expEl ? parseFloat(expEl.dataset.value || '0') : 0;

  new Chart(canvas.getContext('2d'), {
    type: 'bar',
    data: {
      labels: ['Pemasukan', 'Pengeluaran'],
      datasets: [{
        label: 'Nominal (Rp)',
        data: [income, expense],
        backgroundColor: ['#DDEBFA', '#FDE3EA'],
        borderColor: ['#2F5D9E', '#B5384F'],
        borderWidth: 2,
        borderRadius: 10
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
})();