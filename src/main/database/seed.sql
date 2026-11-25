USE budget_buddies;

-- =========================================
-- 1. USERS
-- =========================================
INSERT INTO Users (U_id, U_name, U_email, U_password)
VALUES
('U00001', 'Demo User', 'demo@budgetbuddies.com', 'demo_password_hash');

-- =========================================
-- 2. ACCOUNTS
-- =========================================
INSERT INTO Accounts
(A_id, A_name, A_account_type, A_initial_balance, Users_U_id)
VALUES
('A00001', 'Cash', 'Cash', 1500000.00, 'U00001'),
('A00002', 'Bank BCA', 'Bank', 5000000.00, 'U00001');

-- =========================================
-- 3. CATEGORIES
-- =========================================
INSERT INTO Categories
(C_id, C_name, C_type, Users_U_id)
VALUES
('C00001', 'Salary', 'Income', 'U00001'),
('C00002', 'Food', 'Expense', 'U00001'),
('C00003', 'Transportation', 'Expense', 'U00001'),
('C00004', 'Entertainment', 'Expense', 'U00001');

-- =========================================
-- 4. TRANSACTIONS
-- =========================================
INSERT INTO Transactions
(T_id, T_title, T_type, T_amount, T_transaction_date,
 Accounts_A_id, Categories_C_id)
VALUES
('T00001', 'Monthly Salary', 'Income', 5000000.00,
 '2026-11-01', 'A00002', 'C00001'),

('T00002', 'Lunch', 'Expense', 25000.00,
 '2026-11-02', 'A00001', 'C00002'),

('T00003', 'Online Transportation', 'Expense', 18000.00,
 '2026-11-03', 'A00001', 'C00003'),

('T00004', 'Movie Ticket', 'Expense', 50000.00,
 '2026-11-04', 'A00002', 'C00004');

-- =========================================
-- 5. BUDGETS
-- =========================================
INSERT INTO Budgets
(B_id, B_amount_limit, B_month, B_year,
 Users_U_id, Categories_C_id)
VALUES
('B00001', 1000000.00, 11, 2026, 'U00001', 'C00002'),
('B00002', 500000.00, 11, 2026, 'U00001', 'C00003'),
('B00003', 300000.00, 11, 2026, 'U00001', 'C00004');

-- =========================================
-- 6. SAVINGS GOALS
-- =========================================
INSERT INTO Savings_Goals
(SG_id, SG_name, SG_target_amount, SG_current_amount,
 SG_target_date, SG_status, Users_U_id)
VALUES
('SG0001', 'New Laptop', 10000000.00, 2500000.00,
 '2027-06-30', 'Active', 'U00001'),

('SG0002', 'Emergency Fund', 5000000.00, 1500000.00,
 '2027-12-31', 'Active', 'U00001');

-- =========================================
-- 7. RECURRING TRANSACTIONS
-- =========================================
INSERT INTO Recurring_Transactions
(RT_id, RT_title, RT_type, RT_amount, RT_frequency,
 RT_start_date, RT_end_date, RT_next_date, RT_status,
 Accounts_A_id, Categories_C_id)
VALUES
('RT0001', 'Monthly Internet', 'Expense', 300000.00,
 'Monthly', '2026-11-01', NULL, '2026-12-01', 'Active',
 'A00002', 'C00002'),

('RT0002', 'Monthly Salary', 'Income', 5000000.00,
 'Monthly', '2026-11-01', NULL, '2026-12-01', 'Active',
 'A00002', 'C00001');

-- =========================================
-- 8. DEBTS
-- =========================================
INSERT INTO Debts
(D_id, D_name, D_type, D_total_amount, D_remaining_amount,
 D_due_date, D_status, Users_U_id)
VALUES
('D00001', 'Laptop Installment', 'Payable',
 3000000.00, 2000000.00, '2027-03-01', 'Unpaid', 'U00001'),

('D00002', 'Borrowed Money', 'Receivable',
 1000000.00, 1000000.00, '2026-12-31', 'Unpaid', 'U00001');

-- =========================================
-- 9. DEBT PAYMENTS
-- =========================================
INSERT INTO Debt_Payments
(DP_id, DP_amount, DP_payment_date, DP_notes, Debts_D_id)
VALUES
('DP0001', 1000000.00, '2026-11-05',
 'First installment payment', 'D00001');
