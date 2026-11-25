-- Perbaikan seed untuk authentication.
-- Nilai lama 'demo_password_hash' BUKAN hash yang valid, jadi user demo tidak bisa login.
-- Jalankan SETELAH seed.sql (atau ganti baris INSERT Users di seed.sql dengan nilai ini).
--
-- Login demo:  demo@budgetbuddies.com  /  Demo1234
USE budget_buddies;

UPDATE Users
SET U_password = 'pbkdf2_sha256$600000$iMl6051JMD9iHDEqhK5a6Q==$Tc9chkX0UG0IYciMxrXiMWX+MfnAUlCju5n8h2TBGCU='
WHERE U_id = 'U00001';
