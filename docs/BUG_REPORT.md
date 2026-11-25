# Budget Buddies - Bug Report Awal (hasil review kode)

Sumber: membaca kode di `feature/backend-database` dan `feature/frontend-jsp`.
**Belum ada yang diuji di Tomcat + MySQL.** Kolom "Terverifikasi saat runtime" diisi setelah pengujian manual.

Status: Terbuka / Diperbaiki / Diuji ulang

| ID | Modul | Tingkat | Masalah | Pemilik | Perbaikan | Terverifikasi saat runtime | Status |
|---|---|---|---|---|---|---|---|
| BUG-01 | Frontend (`WebUtil`) | Tinggi (keamanan) | `DEV_MODE = true`: `currentUserId()` selalu mengembalikan `U00001` walau belum login, sehingga servlet tidak pernah menolak pengguna anonim. | Anggota 2 | Set `DEV_MODE = false` (lihat INTEGRATION_NOTES 2.1) | | Terbuka |
| BUG-02 | Database (`seed.sql`) | Tinggi | Password user demo `demo_password_hash` bukan hash valid, jadi user demo tidak bisa login. | Anggota 1 | Pakai `seed_auth_patch.sql` | | Terbuka |
| BUG-03 | Backend (`DBConnection`) | Sedang | User `root` dan password ditulis di kode, padahal panduan melarang credential di repository dan deployment butuh konfigurasi berbeda. | Anggota 1 | Baca dari environment (`suggested-changes/DBConnection.java`) | | Terbuka |
| BUG-04 | Dokumentasi | Rendah | `DATABASE.md` dan `API_CONTRACT.md` tidak mencantumkan kolom/parameter `T_description`, padahal ada di `schema.sql` dan `TransactionServlet`. | Anggota 1 | Perbarui dokumen | | Terbuka |
| BUG-05 | Frontend (`index.jsp`) | Rendah | Halaman awal masih diarahkan ke `/transactions` (komentar di file menyebut harus diganti setelah dashboard jadi). | Anggota 2 | Arahkan ke `/dashboard` | | Terbuka |
| BUG-06 | Authentication | Rendah | Belum ada pembatasan percobaan login dan belum ada token CSRF (dimitigasi `SameSite=Lax`). | Anggota 3 | Dicatat sebagai batasan / pengembangan lanjutan | | Terbuka |

## Catatan positif dari review (tetap perlu dibuktikan lewat pengujian)

- Semua DAO memakai `PreparedStatement` dan try-with-resources; tidak ada SQL di JSP.
- Kepemilikan data dicek di DAO. Untuk tabel tanpa kolom `Users_U_id` (`Transactions`, `Recurring_Transactions`,
  `Debt_Payments`), DAO memakai JOIN ke `Accounts` / `Debts`, sesuai struktur skema.
- Insert/update transaksi dan transaksi berulang memeriksa bahwa akun **dan** kategori milik user yang sama.
- Pembayaran utang (tambah/ubah/hapus) memakai database transaction (`setAutoCommit(false)`, commit/rollback, `FOR UPDATE`).
- `userId` selalu diambil dari session (`WebUtil.currentUserId`), bukan dari parameter browser.
- Pesan gagal hapus rekening/kategori sudah menjelaskan kemungkinan penyebab (masih dipakai transaksi, dst.).

## Format kendala integrasi (panduan 5.9)

```
KENDALA INTEGRASI
Anggota:
Modul:
Masalah:
File terkait:
Error:
Langkah yang sudah dicoba:
Dampak:
Bantuan yang dibutuhkan:
Solusi:
Status:
```
