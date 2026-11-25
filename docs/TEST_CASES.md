# Budget Buddies - Test Cases

Isi kolom **Actual** dan **Status** (PASS/FAIL) saat menjalankan pengujian. Jangan menulis PASS tanpa menjalankannya.
Kalau FAIL: catat di `BUG_REPORT.md`, tandai sumbernya (Backend / Frontend / Database / Auth), dan uji ulang setelah diperbaiki.

Penguji: ____________  Tanggal: ____________  Branch/commit: ____________
Akun uji: **User A** = demo@budgetbuddies.com / Demo1234 (dari seed). **User B** = daftar sendiri lewat `/register`.

Ringkasan: total ___ | PASS ___ | FAIL ___ | belum diuji ___

---

## A. Authentication

| ID | Skenario | Langkah | Expected | Actual | Status |
|---|---|---|---|---|---|
| AUTH-01 | Register valid | Isi nama, email baru, password `Test1234`, konfirmasi sama | Diarahkan ke `/login` dengan pesan "Registrasi berhasil" | | |
| AUTH-02 | Password tersimpan sebagai hash | Setelah AUTH-01: `SELECT U_email, U_password FROM Users;` | Kolom `U_password` berawalan `pbkdf2_sha256$`, bukan `Test1234` | | |
| AUTH-03 | Email duplikat | Register dengan email yang sama | Ditolak: "Email sudah terdaftar" | | |
| AUTH-04 | Email duplikat beda huruf besar | Register `DEMO@BudgetBuddies.com` | Ditolak (email disimpan huruf kecil) | | |
| AUTH-05 | Format email salah | Isi `abc` / `abc@` / `abc@x` | Ditolak oleh browser atau server | | |
| AUTH-06 | Password terlalu pendek | Password `Ab1` | Ditolak: minimal 8 karakter | | |
| AUTH-07 | Password tanpa angka / tanpa huruf | `abcdefgh` lalu `12345678` | Ditolak: harus ada huruf dan angka | | |
| AUTH-08 | Konfirmasi password beda | Konfirmasi tidak sama | Ditolak | | |
| AUTH-09 | Field kosong | Kirim form kosong | Ditolak, data tidak tersimpan | | |
| AUTH-10 | Validasi server (bukan hanya browser) | Kirim POST ke `/register` tanpa melewati form (lihat curl di bagian H) dengan password `abc` | Tetap ditolak server | | |
| AUTH-11 | Nama berisi HTML | Register nama `<script>alert(1)</script>`, lalu login | Nama tampil sebagai teks di navbar, tidak menjalankan script | | |
| AUTH-12 | Login valid | Login User A | Masuk ke `/dashboard`, nama tampil di navbar | | |
| AUTH-13 | Password salah | Email benar, password salah | "Email atau password salah", tidak login | | |
| AUTH-14 | Email tidak terdaftar | Email acak | Pesan **sama persis** dengan AUTH-13 | | |
| AUTH-15 | Login kosong | Kirim form kosong | Ditolak | | |
| AUTH-16 | Email huruf besar saat login | Login `DEMO@BudgetBuddies.com` | Berhasil (dinormalisasi) | | |
| AUTH-17 | SQL injection di login | Email `' OR '1'='1' -- `, password apa saja | Gagal login, tidak ada error database di layar | | |
| AUTH-18 | Session ID berganti setelah login | DevTools > Application > Cookies: catat `JSESSIONID` sebelum dan sesudah login | Nilainya berbeda | | |
| AUTH-19 | Cookie aman | Lihat atribut cookie `JSESSIONID` | `HttpOnly` aktif, `SameSite=Lax` | | |
| AUTH-20 | Logout | Klik "Keluar" | Kembali ke `/login`, pesan "Anda telah keluar" | | |
| AUTH-21 | Dashboard setelah logout | Setelah logout, buka `/dashboard` langsung | Diarahkan ke `/login` | | |
| AUTH-22 | Tombol Back setelah logout | Setelah logout, tekan Back di browser | Halaman privat tidak tampil dari cache (diminta login) | | |
| AUTH-23 | Halaman privat tanpa login | Tanpa login, buka satu per satu: `/accounts`, `/categories`, `/transactions`, `/budgets`, `/savings-goals`, `/recurring-transactions`, `/debts`, `/debt-payments`, `/index.jsp` | Semua diarahkan ke `/login` | | |
| AUTH-24 | Login/register saat sudah login | Login, lalu buka `/login` | Diarahkan ke `/dashboard` | | |
| AUTH-25 | Logout lewat GET | Buka `/logout` di address bar saat login | Tidak keluar (hanya POST yang mengakhiri session) | | |
| AUTH-26 | Session timeout | Ubah `session-timeout` ke 1 menit sementara, login, tunggu 2 menit, klik menu | Diarahkan ke `/login`. Kembalikan ke 30 setelah uji | | |
| AUTH-27 | File statis | Buka `/css/custom.css` tanpa login | Bisa dibuka (dibutuhkan halaman login) | | |
| AUTH-28 | Halaman error | Buka `/halaman-ngawur` saat login | Halaman error ramah, **tanpa** stack trace | | |

## B. Authorization (data antar-user)

Persiapan: login sebagai User A, catat ID data di tiap modul (`A00001`, `C00001`, `T00001`, dst. dari seed).
Login sebagai User B (akun baru, data kosong). Kirim request berisi ID milik User A (lewat DevTools: ubah nilai `<input type="hidden">`,
atau curl di bagian H).

| ID | Modul | Skenario | Expected | Actual | Status |
|---|---|---|---|---|---|
| AUTHZ-01 | Accounts | User B menghapus `A_id` milik A | Gagal, akun A masih ada | | |
| AUTHZ-02 | Accounts | User B mengubah `A_id` milik A | Gagal / "tidak ditemukan", data A tidak berubah | | |
| AUTHZ-03 | Categories | User B hapus / ubah `C_id` milik A | Gagal, data A utuh | | |
| AUTHZ-04 | Transactions | User B hapus / ubah `T_id` milik A | Gagal, data A utuh | | |
| AUTHZ-05 | Transactions | User B menambah transaksi dengan `Accounts_A_id` milik A | Ditolak | | |
| AUTHZ-06 | Transactions | User B menambah transaksi dengan akun miliknya tapi `Categories_C_id` milik A | Ditolak | | |
| AUTHZ-07 | Budgets | User B hapus / ubah `B_id` milik A | Gagal, data A utuh | | |
| AUTHZ-08 | Budgets | User B membuat budget dengan kategori milik A | Ditolak | | |
| AUTHZ-09 | Savings Goals | User B hapus / ubah `SG_id` milik A | Gagal, data A utuh | | |
| AUTHZ-10 | Recurring | User B hapus / ubah `RT_id` milik A | Gagal, data A utuh | | |
| AUTHZ-11 | Recurring | User B membuat transaksi berulang dengan akun/kategori milik A | Ditolak | | |
| AUTHZ-12 | Debts | User B hapus / ubah `D_id` milik A | Gagal, data A utuh | | |
| AUTHZ-13 | Debt Payments | User B menambah pembayaran ke `Debts_D_id` milik A | Ditolak, sisa utang A tidak berubah | | |
| AUTHZ-14 | Debt Payments | User B hapus / ubah `DP_id` milik A | Gagal, sisa utang A tidak berubah | | |
| AUTHZ-15 | Semua list | Login User B, buka semua halaman daftar | Tidak ada satu pun data User A yang tampil | | |
| AUTHZ-16 | Parameter `Users_U_id` palsu | Kirim parameter tambahan `Users_U_id=U00001` saat User B menambah data | Diabaikan, data tersimpan atas nama User B | | |

## C. CRUD per modul

Untuk setiap baris: kerjakan lewat browser, lalu cek tampilan **dan** isi tabel di MySQL.

### C1. Accounts
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| ACC-01 | Create rekening valid | Muncul di daftar, ada di DB | | |
| ACC-02 | Read: daftar menampilkan nama, tipe, saldo | Sesuai data | | |
| ACC-03 | Update nama/tipe/saldo awal | Perubahan tersimpan | | |
| ACC-04 | Delete rekening yang belum dipakai | Hilang dari daftar dan DB | | |
| ACC-05 | Delete rekening yang sudah punya transaksi | Ditolak dengan pesan jelas, data transaksi utuh | | |
| ACC-06 | Nama kosong / saldo negatif / saldo bukan angka | Ditolak dengan pesan jelas | | |

### C2. Categories
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| CAT-01 | Create kategori Income dan Expense | Muncul di daftar | | |
| CAT-02 | Update nama/jenis | Tersimpan | | |
| CAT-03 | Create kategori dengan nama + jenis yang sama (duplikat) | Ditolak dengan pesan ramah (bukan error database) | | |
| CAT-04 | Delete kategori belum dipakai | Terhapus | | |
| CAT-05 | Delete kategori yang dipakai transaksi/budget/transaksi berulang | Ditolak dengan pesan jelas | | |
| CAT-06 | Nama kosong / jenis tidak valid | Ditolak | | |

### C3. Transactions
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| TRX-01 | Create transaksi (Income dan Expense) | Muncul, total pemasukan/pengeluaran ikut berubah | | |
| TRX-02 | Create dengan deskripsi opsional kosong dan terisi | Keduanya berhasil | | |
| TRX-03 | Update judul/jumlah/tanggal/akun/kategori | Tersimpan | | |
| TRX-04 | Delete transaksi | Hilang dari daftar dan DB | | |
| TRX-05 | Cari berdasarkan judul (sebagian kata, huruf besar/kecil) | Hanya yang cocok tampil | | |
| TRX-06 | Filter tanggal dan jenis (tunggal dan digabung dengan pencarian) | Hasil benar | | |
| TRX-07 | Jumlah 0, negatif, huruf, sangat besar | Ditolak dengan pesan jelas | | |
| TRX-08 | Judul kosong / lebih dari 100 karakter | Ditolak | | |
| TRX-09 | Tanggal tidak valid | Ditolak | | |
| TRX-10 | Belum punya akun/kategori | Form memberi petunjuk yang jelas, tidak error | | |

### C4. Budgets
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| BUD-01 | Create budget (kategori, bulan, tahun, batas) | Muncul di daftar | | |
| BUD-02 | Tombol **Edit** ada dan berfungsi (perbaikan dari website lama) | Perubahan tersimpan | | |
| BUD-03 | Delete budget | Terhapus | | |
| BUD-04 | Penggunaan dan sisa anggaran sesuai total transaksi Expense kategori itu di bulan itu | Hitungan cocok dengan perhitungan manual | | |
| BUD-05 | Budget ganda (kategori + bulan + tahun sama) | Ditolak dengan pesan ramah | | |
| BUD-06 | Bulan 0 / 13, tahun < 2000, batas <= 0 | Ditolak | | |
| BUD-07 | Penggunaan melebihi batas | Indikator berubah (warna/pesan) sesuai desain | | |

### C5. Savings Goals
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| SAV-01 | Create target tabungan | Muncul di daftar | | |
| SAV-02 | Update jumlah terkumpul / status / tanggal target | Tersimpan, progres ikut berubah | | |
| SAV-03 | Delete | Terhapus | | |
| SAV-04 | Target 0 / negatif, terkumpul negatif | Ditolak | | |
| SAV-05 | Tanggal target kosong (opsional) | Berhasil | | |
| SAV-06 | Terkumpul > target | Perilaku sesuai desain (tercatat selesai atau ditolak); catat hasilnya | | |

### C6. Recurring Transactions
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| REC-01 | Create transaksi berulang | Muncul di jadwal | | |
| REC-02 | Update frekuensi / tanggal berikutnya / status | Tersimpan | | |
| REC-03 | Delete | Terhapus | | |
| REC-04 | Jumlah <= 0, judul kosong | Ditolak | | |
| REC-05 | Tanggal berakhir sebelum tanggal mulai | Catat hasilnya; idealnya ditolak | | |
| REC-06 | Tanggal berakhir kosong (opsional) | Berhasil | | |

### C7. Debts
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| DEB-01 | Create utang / piutang | Muncul di daftar | | |
| DEB-02 | Update nama / jatuh tempo / status | Tersimpan | | |
| DEB-03 | Delete utang tanpa pembayaran | Terhapus | | |
| DEB-04 | Delete utang yang sudah punya pembayaran | Ditolak dengan pesan jelas, riwayat utuh | | |
| DEB-05 | Sisa > total, total <= 0, sisa negatif | Ditolak | | |

### C8. Debt Payments
| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| DPY-01 | Create pembayaran | Muncul di riwayat; sisa utang berkurang sebesar pembayaran | | |
| DPY-02 | Pembayaran melunasi seluruh sisa | Sisa 0, status utang menyesuaikan (catat) | | |
| DPY-03 | Pembayaran lebih besar dari sisa utang | Ditolak, sisa utang tidak berubah | | |
| DPY-04 | Update jumlah pembayaran | Sisa utang dihitung ulang dengan benar | | |
| DPY-05 | Delete pembayaran | Sisa utang kembali bertambah sebesar pembayaran itu | | |
| DPY-06 | Jumlah 0 / negatif / huruf | Ditolak | | |
| DPY-07 | Konsistensi: total utang = sisa + jumlah semua pembayaran | Selalu benar setelah rangkaian DPY-01 s.d. DPY-05 | | |

## D. Konsistensi data dan foreign key (cek langsung di MySQL)

| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| DB-01 | Jalankan `schema.sql` pada database kosong | Sembilan tabel terbentuk tanpa error | | |
| DB-02 | Jalankan `seed.sql` lalu `seed_auth_patch.sql` | Data masuk, user demo bisa login | | |
| DB-03 | `SHOW CREATE TABLE Transactions;` | CHECK dan FOREIGN KEY ada (butuh MySQL >= 8.0.16 / MariaDB >= 10.2.1 agar CHECK berlaku) | | |
| DB-04 | Catat versi: `SELECT VERSION();` | Versi tercatat di laporan | | |
| DB-05 | Insert langsung yang melanggar CHECK (mis. `T_amount = -5`) | Ditolak database | | |
| DB-06 | Pembayaran utang gagal di tengah proses (mis. melebihi sisa) | Tidak ada data setengah tersimpan (rollback) | | |

## E. UI dan responsif

| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| UI-01 | Semua tombol di 8 modul + dashboard berfungsi | Tidak ada tombol mati | | |
| UI-02 | Tampilan saat belum ada data (user baru) di tiap halaman | Ada keadaan kosong yang jelas, tidak error | | |
| UI-03 | Layar kecil (DevTools: lebar 375px) | Navbar dan tabel tetap terbaca/dapat digeser | | |
| UI-04 | Konfirmasi sebelum menghapus | Ada konfirmasi di tiap modul | | |
| UI-05 | Pesan sukses/error | Tampil dan hilang setelah refresh (pola PRG) | | |
| UI-06 | Dashboard menampilkan saldo, pemasukan, pengeluaran, transaksi terbaru sesuai data | Angka cocok dengan data | | |

## F. Deployment

| ID | Skenario | Expected | Actual | Status |
|---|---|---|---|---|
| DEP-01 | `mvn clean package` | `target/budget-buddies.war` terbentuk | | |
| DEP-02 | Deploy WAR di Tomcat 10.1 lokal | Aplikasi terbuka, tidak ada error di log | | |
| DEP-03 | Koneksi database lewat environment variable (bukan hard-code) | Berhasil terhubung | | |
| DEP-04 | Login + logout di server deployment | Berfungsi | | |
| DEP-05 | Satu operasi CRUD di setiap modul di server deployment | Berfungsi | | |
| DEP-06 | Tidak ada password/credential di repository GitHub | `git grep -i password` hanya menemukan nama kolom/variabel | | |
| DEP-07 | Periksa log server setelah pengujian | Tidak ada exception tak terduga | | |

## G. Bukti pengujian

Simpan screenshot hasil tiap kelompok (AUTH, AUTHZ, CRUD per modul, deployment) di folder `docs/screenshots/`.
Screenshot yang sama bisa dipakai untuk laporan Quiz 2.

## H. Cara mengirim request manual (untuk AUTH-10 dan AUTHZ)

1. Login di browser, DevTools > Application > Cookies, salin nilai `JSESSIONID`.
2. Kirim request, contoh menghapus transaksi milik user lain (ganti host, path, dan ID):

```bash
curl -i -X POST "http://localhost:8080/budget-buddies/transactions" \
  -H "Cookie: JSESSIONID=<nilai-session-user-B>" \
  -d "action=delete&T_id=T00001"
```

Hasil yang benar: respons `302` kembali ke daftar, pesan "Gagal menghapus transaksi", dan data User A tetap ada.

Registrasi tanpa lewat form (AUTH-10):

```bash
curl -i -X POST "http://localhost:8080/budget-buddies/register" \
  -d "name=Uji&email=uji@example.com&password=abc&confirmPassword=abc"
```

Hasil yang benar: respons `400` dan akun tidak terbuat.
