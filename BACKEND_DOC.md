
# BUDGET BUDDIES - BACKEND DOCUMENTATION

## 1. Backend Structure

Backend menggunakan Java dengan konsep MVC dan JDBC.

Package:
- config: koneksi database
- model: representasi data tabel
- dao: operasi database (CRUD)

## 2. Database

Database: budget_buddies

Tables:
1. Users
2. Accounts
3. Categories
4. Transactions
5. Budgets
6. Savings_Goals
7. Recurring_Transactions
8. Debts
9. Debt_Payments

## 3. Model dan DAO

| No | Model | DAO | Fungsi |
|---|---|---|---|
| 1 | Account | AccountDAO | Mengelola akun keuangan |
| 2 | Category | CategoryDAO | Mengelola kategori transaksi |
| 3 | Transaction | TransactionDAO | Mengelola transaksi |
| 4 | Budget | BudgetDAO | Mengelola anggaran |
| 5 | SavingsGoal | SavingsGoalDAO | Mengelola target tabungan |
| 6 | RecurringTransaction | RecurringTransactionDAO | Mengelola transaksi berulang |
| 7 | Debt | DebtDAO | Mengelola utang/piutang |
| 8 | DebtPayment | DebtPaymentDAO | Mengelola pembayaran utang |

## 4. Database Connection

Class: DBConnection.java

Method:
- getConnection()
- Return: Connection

## 5. Catatan Integrasi

- Semua operasi database menggunakan JDBC.
- DAO menggunakan PreparedStatement.
- Method CREATE, READ, UPDATE, DELETE tersedia pada setiap DAO.
- ID harus disiapkan sebelum operasi CREATE.
- Data pengguna harus dibatasi berdasarkan userId.
- Servlet dapat memanggil method DAO untuk menghubungkan backend dengan JSP.


## 6. DAO Method Reference

### 6.1 AccountDAO
| Method | Parameter | Return |
|---|---|---|
| getAllAccounts | String userId | List<Account> |
| addAccount | Account account | boolean |
| updateAccount | Account account | boolean |
| deleteAccount | String accountId, String userId | boolean |

### 6.2 CategoryDAO
| Method | Parameter | Return |
|---|---|---|
| getAllCategories | String userId | List<Category> |
| addCategory | Category category | boolean |
| updateCategory | Category category | boolean |
| deleteCategory | String categoryId, String userId | boolean |

### 6.3 TransactionDAO
| Method | Parameter | Return |
|---|---|---|
| getAllTransactions | String userId | List<Transaction> |
| addTransaction | Transaction transaction, String userId | boolean |
| updateTransaction | Transaction transaction, String userId | boolean |
| deleteTransaction | String transactionId, String userId | boolean |

### 6.4 BudgetDAO
| Method | Parameter | Return |
|---|---|---|
| getAllBudgets | String userId | List<Budget> |
| addBudget | Budget budget | boolean |
| updateBudget | Budget budget | boolean |
| deleteBudget | String budgetId, String userId | boolean |

### 6.5 SavingsGoalDAO
| Method | Parameter | Return |
|---|---|---|
| getAllSavingsGoals | String userId | List<SavingsGoal> |
| addSavingsGoal | SavingsGoal goal | boolean |
| updateSavingsGoal | SavingsGoal goal | boolean |
| deleteSavingsGoal | String goalId, String userId | boolean |

### 6.6 RecurringTransactionDAO
| Method | Parameter | Return |
|---|---|---|
| getAllRecurringTransactions | String userId | List<RecurringTransaction> |
| addRecurringTransaction | RecurringTransaction transaction, String userId | boolean |
| updateRecurringTransaction | RecurringTransaction transaction, String userId | boolean |
| deleteRecurringTransaction | String transactionId, String userId | boolean |

### 6.7 DebtDAO
| Method | Parameter | Return |
|---|---|---|
| getAllDebts | String userId | List<Debt> |
| addDebt | Debt debt | boolean |
| updateDebt | Debt debt | boolean |
| deleteDebt | String debtId, String userId | boolean |

### 6.8 DebtPaymentDAO
| Method | Parameter | Return |
|---|---|---|
| getAllDebtPayments | String userId | List<DebtPayment> |
| addDebtPayment | DebtPayment payment, String userId | boolean |
| updateDebtPayment | DebtPayment payment, String userId | boolean |
| deleteDebtPayment | String paymentId, String userId | boolean |
