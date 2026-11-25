CREATE DATABASE IF NOT EXISTS budget_buddies;
USE budget_buddies;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Debt_Payments;
DROP TABLE IF EXISTS Recurring_Transactions;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS Budgets;
DROP TABLE IF EXISTS Savings_Goals;
DROP TABLE IF EXISTS Debts;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Accounts;
DROP TABLE IF EXISTS Users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- 1. USERS
-- =========================================
CREATE TABLE Users (
    U_id CHAR(6) NOT NULL,
    U_name VARCHAR(100) NOT NULL,
    U_email VARCHAR(100) NOT NULL,
    U_password VARCHAR(255) NOT NULL,
    U_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    U_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT Users_pk PRIMARY KEY (U_id),
    CONSTRAINT Users_email_unique UNIQUE (U_email)
) ENGINE=InnoDB;


-- =========================================
-- 2. ACCOUNTS
-- =========================================
CREATE TABLE Accounts (
    A_id CHAR(6) NOT NULL,
    A_name VARCHAR(100) NOT NULL,
    A_account_type VARCHAR(50) NOT NULL,
    A_initial_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    A_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    A_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Users_U_id CHAR(6) NOT NULL,

    CONSTRAINT Accounts_pk PRIMARY KEY (A_id),

    CONSTRAINT Accounts_balance_check
        CHECK (A_initial_balance >= 0),

    CONSTRAINT Accounts_Users_fk
        FOREIGN KEY (Users_U_id)
        REFERENCES Users (U_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    INDEX idx_accounts_user (Users_U_id)
) ENGINE=InnoDB;


-- =========================================
-- 3. CATEGORIES
-- =========================================
CREATE TABLE Categories (
    C_id CHAR(6) NOT NULL,
    C_name VARCHAR(100) NOT NULL,
    C_type VARCHAR(50) NOT NULL,
    C_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    C_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Users_U_id CHAR(6) NOT NULL,

    CONSTRAINT Categories_pk PRIMARY KEY (C_id),

    CONSTRAINT Categories_Users_fk
        FOREIGN KEY (Users_U_id)
        REFERENCES Users (U_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT Categories_user_name_type_unique
        UNIQUE (Users_U_id, C_name, C_type),

    INDEX idx_categories_user (Users_U_id)
) ENGINE=InnoDB;


-- =========================================
-- 4. TRANSACTIONS
-- =========================================
CREATE TABLE Transactions (
    T_id CHAR(6) NOT NULL,
    T_title VARCHAR(100) NOT NULL,
    T_description VARCHAR(255) NULL,
    T_type VARCHAR(50) NOT NULL,
    T_amount DECIMAL(15,2) NOT NULL,
    T_transaction_date DATE NOT NULL,
    T_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    T_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Accounts_A_id CHAR(6) NOT NULL,
    Categories_C_id CHAR(6) NOT NULL,

    CONSTRAINT Transactions_pk PRIMARY KEY (T_id),

    CONSTRAINT Transactions_amount_check
        CHECK (T_amount > 0),

    CONSTRAINT Transactions_Accounts_fk
        FOREIGN KEY (Accounts_A_id)
        REFERENCES Accounts (A_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT Transactions_Categories_fk
        FOREIGN KEY (Categories_C_id)
        REFERENCES Categories (C_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_transactions_account (Accounts_A_id),
    INDEX idx_transactions_category (Categories_C_id),
    INDEX idx_transactions_date (T_transaction_date)
) ENGINE=InnoDB;


-- =========================================
-- 5. BUDGETS
-- =========================================
CREATE TABLE Budgets (
    B_id CHAR(6) NOT NULL,
    B_amount_limit DECIMAL(15,2) NOT NULL,
    B_month INT NOT NULL,
    B_year INT NOT NULL,
    B_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    B_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Users_U_id CHAR(6) NOT NULL,
    Categories_C_id CHAR(6) NOT NULL,

    CONSTRAINT Budgets_pk PRIMARY KEY (B_id),

    CONSTRAINT Budgets_amount_check
        CHECK (B_amount_limit > 0),

    CONSTRAINT Budgets_month_check
        CHECK (B_month BETWEEN 1 AND 12),

    CONSTRAINT Budgets_year_check
        CHECK (B_year >= 2000),

    CONSTRAINT Budgets_Users_fk
        FOREIGN KEY (Users_U_id)
        REFERENCES Users (U_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT Budgets_Categories_fk
        FOREIGN KEY (Categories_C_id)
        REFERENCES Categories (C_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT Budgets_user_category_period_unique
        UNIQUE (Users_U_id, Categories_C_id, B_month, B_year),

    INDEX idx_budgets_user (Users_U_id)
) ENGINE=InnoDB;


-- =========================================
-- 6. SAVINGS GOALS
-- =========================================
CREATE TABLE Savings_Goals (
    SG_id CHAR(6) NOT NULL,
    SG_name VARCHAR(100) NOT NULL,
    SG_target_amount DECIMAL(15,2) NOT NULL,
    SG_current_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    SG_target_date DATE NULL,
    SG_status VARCHAR(30) NOT NULL DEFAULT 'Active',
    SG_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    SG_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Users_U_id CHAR(6) NOT NULL,

    CONSTRAINT Savings_Goals_pk PRIMARY KEY (SG_id),

    CONSTRAINT Savings_Goals_target_check
        CHECK (SG_target_amount > 0),

    CONSTRAINT Savings_Goals_current_check
        CHECK (SG_current_amount >= 0),

    CONSTRAINT Savings_Goals_Users_fk
        FOREIGN KEY (Users_U_id)
        REFERENCES Users (U_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    INDEX idx_savings_goals_user (Users_U_id)
) ENGINE=InnoDB;


-- =========================================
-- 7. RECURRING TRANSACTIONS
-- =========================================
CREATE TABLE Recurring_Transactions (
    RT_id CHAR(6) NOT NULL,
    RT_title VARCHAR(100) NOT NULL,
    RT_type VARCHAR(50) NOT NULL,
    RT_amount DECIMAL(15,2) NOT NULL,
    RT_frequency VARCHAR(30) NOT NULL,
    RT_start_date DATE NOT NULL,
    RT_end_date DATE NULL,
    RT_next_date DATE NOT NULL,
    RT_status VARCHAR(30) NOT NULL DEFAULT 'Active',
    RT_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    RT_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Accounts_A_id CHAR(6) NOT NULL,
    Categories_C_id CHAR(6) NOT NULL,

    CONSTRAINT Recurring_Transactions_pk PRIMARY KEY (RT_id),

    CONSTRAINT Recurring_Transactions_amount_check
        CHECK (RT_amount > 0),

    CONSTRAINT Recurring_Transactions_Accounts_fk
        FOREIGN KEY (Accounts_A_id)
        REFERENCES Accounts (A_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT Recurring_Transactions_Categories_fk
        FOREIGN KEY (Categories_C_id)
        REFERENCES Categories (C_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_recurring_account (Accounts_A_id),
    INDEX idx_recurring_category (Categories_C_id)
) ENGINE=InnoDB;


-- =========================================
-- 8. DEBTS
-- =========================================
CREATE TABLE Debts (
    D_id CHAR(6) NOT NULL,
    D_name VARCHAR(100) NOT NULL,
    D_type VARCHAR(30) NOT NULL,
    D_total_amount DECIMAL(15,2) NOT NULL,
    D_remaining_amount DECIMAL(15,2) NOT NULL,
    D_due_date DATE NULL,
    D_status VARCHAR(30) NOT NULL DEFAULT 'Unpaid',
    D_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    D_updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    Users_U_id CHAR(6) NOT NULL,

    CONSTRAINT Debts_pk PRIMARY KEY (D_id),

    CONSTRAINT Debts_total_check
        CHECK (D_total_amount > 0),

    CONSTRAINT Debts_remaining_check
        CHECK (
            D_remaining_amount >= 0
            AND D_remaining_amount <= D_total_amount
        ),

    CONSTRAINT Debts_Users_fk
        FOREIGN KEY (Users_U_id)
        REFERENCES Users (U_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    INDEX idx_debts_user (Users_U_id)
) ENGINE=InnoDB;


-- =========================================
-- 9. DEBT PAYMENTS
-- =========================================
CREATE TABLE Debt_Payments (
    DP_id CHAR(6) NOT NULL,
    DP_amount DECIMAL(15,2) NOT NULL,
    DP_payment_date DATE NOT NULL,
    DP_notes VARCHAR(255) NULL,
    DP_created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    Debts_D_id CHAR(6) NOT NULL,

    CONSTRAINT Debt_Payments_pk PRIMARY KEY (DP_id),

    CONSTRAINT Debt_Payments_amount_check
        CHECK (DP_amount > 0),

    CONSTRAINT Debt_Payments_Debts_fk
        FOREIGN KEY (Debts_D_id)
        REFERENCES Debts (D_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_debt_payments_debt (Debts_D_id)
) ENGINE=InnoDB;SHOW DATABASES;

USE budget_buddies;

SHOW TABLES;