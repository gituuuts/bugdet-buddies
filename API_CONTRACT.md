# Budget Buddies - API Contract

## 1. Overview

Dokumen ini menjelaskan komunikasi antara Java Servlet, JSP, dan DAO pada aplikasi Budget Buddies.

### Architecture

Browser → Servlet → DAO → MySQL

Servlet bertugas menerima request, memanggil DAO, dan mengirimkan data ke JSP melalui request attributes.

JSP bertugas menampilkan data dan menyediakan form untuk pengguna.

## 2. General Rules

- HTTP Method: GET, POST
- Data transfer: HTTP Request Parameters
- Data display: Request Attributes
- View technology: JSP
- Database access: DAO
- Authentication: Dikelola oleh Authentication Module

## 3. Servlet and URL Mapping

| Feature | Servlet Name | URL | Method |
|---|---|---|---|
| Accounts | AccountServlet | /accounts | GET, POST |
| Categories | CategoryServlet | /categories | GET, POST |
| Transactions | TransactionServlet | /transactions | GET, POST |
| Budgets | BudgetServlet | /budgets | GET, POST |
| Savings Goals | SavingsGoalServlet | /savings-goals | GET, POST |
| Recurring Transactions | RecurringTransactionServlet | /recurring-transactions | GET, POST |
| Debts | DebtServlet | /debts | GET, POST |
| Debt Payments | DebtPaymentServlet | /debt-payments | GET, POST |

## 4. Request Parameters

### 4.1 Accounts

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| A_id | Account ID |
| A_name | Account name |
| A_account_type | Account type |
| A_initial_balance | Initial balance |

### 4.2 Categories

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| C_id | Category ID |
| C_name | Category name |
| C_type | Category type |

### 4.3 Transactions

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| T_id | Transaction ID |
| T_title | Transaction title |
| T_type | Income or Expense |
| T_amount | Transaction amount |
| T_transaction_date | Transaction date |
| Accounts_A_id | Account ID |
| Categories_C_id | Category ID |

### 4.4 Budgets

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| B_id | Budget ID |
| B_amount_limit | Budget limit |
| B_month | Budget month |
| B_year | Budget year |
| Categories_C_id | Category ID |

### 4.5 Savings Goals

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| SG_id | Savings goal ID |
| SG_name | Goal name |
| SG_target_amount | Target amount |
| SG_current_amount | Current amount |
| SG_target_date | Target date |
| SG_status | Goal status |

### 4.6 Recurring Transactions

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| RT_id | Recurring transaction ID |
| RT_title | Transaction title |
| RT_type | Income or Expense |
| RT_amount | Transaction amount |
| RT_frequency | Recurrence frequency |
| RT_start_date | Start date |
| RT_end_date | End date |
| RT_next_date | Next transaction date |
| RT_status | Transaction status |
| Accounts_A_id | Account ID |
| Categories_C_id | Category ID |

### 4.7 Debts

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| D_id | Debt ID |
| D_name | Debt name |
| D_type | Debt type |
| D_total_amount | Total amount |
| D_remaining_amount | Remaining amount |
| D_due_date | Due date |
| D_status | Payment status |

### 4.8 Debt Payments

| Parameter | Description |
|---|---|
| action | list, add, update, delete |
| DP_id | Payment ID |
| DP_amount | Payment amount |
| DP_payment_date | Payment date |
| DP_notes | Payment notes |
| Debts_D_id | Related debt ID |

## 5. Request Attributes

Servlet sends data to JSP using request.setAttribute().

| Servlet | Attribute | Data Type | Description |
|---|---|---|---|
| AccountServlet | accounts | List<Account> | Account list |
| CategoryServlet | categories | List<Category> | Category list |
| TransactionServlet | transactions | List<Transaction> | Transaction list |
| BudgetServlet | budgets | List<Budget> | Budget list |
| SavingsGoalServlet | savingsGoals | List<SavingsGoal> | Savings goal list |
| RecurringTransactionServlet | recurringTransactions | List<RecurringTransaction> | Recurring transaction list |
| DebtServlet | debts | List<Debt> | Debt list |
| DebtPaymentServlet | debtPayments | List<DebtPayment> | Debt payment list |

## 6. JSP Forward Mapping

| Servlet | JSP Destination |
|---|---|
| AccountServlet | /WEB-INF/views/accounts.jsp |
| CategoryServlet | /WEB-INF/views/categories.jsp |
| TransactionServlet | /WEB-INF/views/transactions.jsp |
| BudgetServlet | /WEB-INF/views/budgets.jsp |
| SavingsGoalServlet | /WEB-INF/views/savings-goals.jsp |
| RecurringTransactionServlet | /WEB-INF/views/recurring-transactions.jsp |
| DebtServlet | /WEB-INF/views/debts.jsp |
| DebtPaymentServlet | /WEB-INF/views/debt-payments.jsp |

## 7. Authentication and User Identification

- Authentication is handled by the Authentication Module.
- User identity should be obtained from the authenticated session.
- Servlet must not trust a user ID submitted directly by the browser.
- DAO operations must be restricted to data belonging to the authenticated user.

## 8. Integration Notes

This document is an initial integration contract.

Servlet mappings, request parameters, JSP paths, and request attributes must be confirmed by the backend, frontend, and authentication team members before final implementation.
