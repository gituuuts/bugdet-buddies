# Budget Buddies - Database Documentation

## 1. Database Overview

**Database Name:** `budget_buddies`

Budget Buddies menggunakan MySQL sebagai database untuk menyimpan dan mengelola data keuangan pengguna.

Database terdiri dari 9 tabel utama:

1. Users
2. Accounts
3. Categories
4. Transactions
5. Budgets
6. Savings_Goals
7. Recurring_Transactions
8. Debts
9. Debt_Payments

## 2. Table Description

### 2.1 Users

Menyimpan informasi pengguna aplikasi.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| U_id | CHAR(6) | PK | ID pengguna |
| U_name | VARCHAR(100) | NOT NULL | Nama pengguna |
| U_email | VARCHAR(100) | UNIQUE, NOT NULL | Email pengguna |
| U_password | VARCHAR(255) | NOT NULL | Password pengguna |
| U_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| U_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |

### 2.2 Accounts

Menyimpan rekening atau sumber dana milik pengguna.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| A_id | CHAR(6) | PK | ID akun |
| A_name | VARCHAR(100) | NOT NULL | Nama akun |
| A_account_type | VARCHAR(50) | NOT NULL | Jenis akun |
| A_initial_balance | DECIMAL(15,2) | DEFAULT 0.00 | Saldo awal |
| A_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| A_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Users_U_id | CHAR(6) | FK | Pemilik akun |

### 2.3 Categories

Menyimpan kategori pemasukan dan pengeluaran.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| C_id | CHAR(6) | PK | ID kategori |
| C_name | VARCHAR(100) | NOT NULL | Nama kategori |
| C_type | VARCHAR(50) | NOT NULL | Jenis kategori |
| C_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| C_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Users_U_id | CHAR(6) | FK | Pemilik kategori |

Kombinasi `Users_U_id`, `C_name`, dan `C_type` harus unik.

### 2.4 Transactions

Menyimpan seluruh transaksi pemasukan dan pengeluaran.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| T_id | CHAR(6) | PK | ID transaksi |
| T_title | VARCHAR(100) | NOT NULL | Judul transaksi |
| T_type | VARCHAR(50) | NOT NULL | Jenis transaksi |
| T_amount | DECIMAL(15,2) | NOT NULL | Nominal transaksi |
| T_transaction_date | DATE | NOT NULL | Tanggal transaksi |
| T_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| T_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Accounts_A_id | CHAR(6) | FK | Akun transaksi |
| Categories_C_id | CHAR(6) | FK | Kategori transaksi |

Nominal transaksi harus lebih besar dari 0.

### 2.5 Budgets

Menyimpan batas anggaran pengguna berdasarkan kategori dan periode.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| B_id | CHAR(6) | PK | ID anggaran |
| B_amount_limit | DECIMAL(15,2) | NOT NULL | Batas anggaran |
| B_month | INT | 1-12 | Bulan anggaran |
| B_year | INT | >= 2000 | Tahun anggaran |
| B_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| B_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Users_U_id | CHAR(6) | FK | Pemilik anggaran |
| Categories_C_id | CHAR(6) | FK | Kategori anggaran |

Kombinasi pengguna, kategori, bulan, dan tahun harus unik.

### 2.6 Savings_Goals

Menyimpan target tabungan pengguna.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| SG_id | CHAR(6) | PK | ID target tabungan |
| SG_name | VARCHAR(100) | NOT NULL | Nama target |
| SG_target_amount | DECIMAL(15,2) | NOT NULL | Target nominal |
| SG_current_amount | DECIMAL(15,2) | DEFAULT 0.00 | Tabungan terkumpul |
| SG_target_date | DATE | NULL | Target tanggal |
| SG_status | VARCHAR(30) | DEFAULT 'Active' | Status target |
| SG_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| SG_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Users_U_id | CHAR(6) | FK | Pemilik target |

### 2.7 Recurring_Transactions

Menyimpan transaksi berulang secara berkala.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| RT_id | CHAR(6) | PK | ID transaksi berulang |
| RT_title | VARCHAR(100) | NOT NULL | Judul transaksi |
| RT_type | VARCHAR(50) | NOT NULL | Jenis transaksi |
| RT_amount | DECIMAL(15,2) | NOT NULL | Nominal transaksi |
| RT_frequency | VARCHAR(30) | NOT NULL | Frekuensi transaksi |
| RT_start_date | DATE | NOT NULL | Tanggal mulai |
| RT_end_date | DATE | NULL | Tanggal berakhir |
| RT_next_date | DATE | NOT NULL | Jadwal berikutnya |
| RT_status | VARCHAR(30) | DEFAULT 'Active' | Status transaksi |
| RT_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| RT_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Accounts_A_id | CHAR(6) | FK | Akun transaksi |
| Categories_C_id | CHAR(6) | FK | Kategori transaksi |

### 2.8 Debts

Menyimpan informasi utang dan piutang pengguna.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| D_id | CHAR(6) | PK | ID utang/piutang |
| D_name | VARCHAR(100) | NOT NULL | Nama utang/piutang |
| D_type | VARCHAR(30) | NOT NULL | Jenis utang/piutang |
| D_total_amount | DECIMAL(15,2) | NOT NULL | Total nominal |
| D_remaining_amount | DECIMAL(15,2) | NOT NULL | Sisa nominal |
| D_due_date | DATE | NULL | Tenggat pembayaran |
| D_status | VARCHAR(30) | DEFAULT 'Unpaid' | Status pembayaran |
| D_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| D_updated_at | TIMESTAMP | ON UPDATE CURRENT_TIMESTAMP | Waktu perubahan |
| Users_U_id | CHAR(6) | FK | Pemilik data |

Sisa nominal harus berada di antara 0 dan total nominal.

### 2.9 Debt_Payments

Menyimpan riwayat pembayaran utang/piutang.

| Column | Data Type | Constraint | Description |
|---|---|---|---|
| DP_id | CHAR(6) | PK | ID pembayaran |
| DP_amount | DECIMAL(15,2) | NOT NULL | Nominal pembayaran |
| DP_payment_date | DATE | NOT NULL | Tanggal pembayaran |
| DP_notes | VARCHAR(255) | NULL | Catatan pembayaran |
| DP_created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Waktu pembuatan |
| Debts_D_id | CHAR(6) | FK | Utang/piutang terkait |

## 3. Entity Relationships

Relasi antar tabel:

| Parent Table | Child Table | Relationship |
|---|---|---|
| Users | Accounts | One-to-Many |
| Users | Categories | One-to-Many |
| Users | Budgets | One-to-Many |
| Users | Savings_Goals | One-to-Many |
| Users | Debts | One-to-Many |
| Accounts | Transactions | One-to-Many |
| Categories | Transactions | One-to-Many |
| Categories | Budgets | One-to-Many |
| Accounts | Recurring_Transactions | One-to-Many |
| Categories | Recurring_Transactions | One-to-Many |
| Debts | Debt_Payments | One-to-Many |

**Note:** `Recurring_Transactions` tidak memiliki Foreign Key langsung ke `Users`. Kepemilikan transaksi berulang ditelusuri melalui `Accounts` dan `Categories`.

## 4. Foreign Key Rules

| Relationship | ON UPDATE | ON DELETE |
|---|---|---|
| Accounts → Users | CASCADE | CASCADE |
| Categories → Users | CASCADE | CASCADE |
| Transactions → Accounts | CASCADE | RESTRICT |
| Transactions → Categories | CASCADE | RESTRICT |
| Budgets → Users | CASCADE | CASCADE |
| Budgets → Categories | CASCADE | RESTRICT |
| Savings_Goals → Users | CASCADE | CASCADE |
| Recurring_Transactions → Accounts | CASCADE | RESTRICT |
| Recurring_Transactions → Categories | CASCADE | RESTRICT |
| Debts → Users | CASCADE | CASCADE |
| Debt_Payments → Debts | CASCADE | RESTRICT |

## 5. Database Files

| File | Description |
|---|---|
| schema.sql | Membuat struktur database dan tabel |
| seed.sql | Memasukkan data awal untuk pengujian |
| DATABASE.md | Dokumentasi struktur database dan relasi |

## 6. Notes

- Primary Key menggunakan format `CHAR(6)`.
- ID dibuat dan diberikan secara manual oleh aplikasi.
- Foreign Key digunakan untuk menjaga integritas relasi data.
- Nominal keuangan menggunakan `DECIMAL(15,2)`.
- Database menggunakan storage engine InnoDB.
