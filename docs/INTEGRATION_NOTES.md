# Budget Buddies - Catatan Integrasi Authentication (Anggota 3)

Branch yang disarankan: `feature/authentication`
Basis: branch yang sudah berisi Servlet + JSP Anggota 2 (`feature/frontend-jsp`, atau `develop` kalau sudah di-merge).

## 1. File baru (tidak menimpa file siapa pun)

| File | Fungsi |
|---|---|
| `model/User.java` | Model tabel `Users` |
| `dao/UserDAO.java` | `findByEmail`, `findById`, `emailExists`, `insert` (PreparedStatement) |
| `util/PasswordUtil.java` | Hash/verify password PBKDF2-HMAC-SHA256 (bawaan JDK, tanpa dependency baru) |
| `servlet/AuthServlet.java` | `/login`, `/register`, `/logout` |
| `filter/AuthFilter.java` | Semua URL wajib login, kecuali `/login`, `/register`, `/css/*`, `/js/*` |
| `views/login.jsp`, `register.jsp`, `error.jsp` | Halaman publik dan halaman error |
| `database/seed_auth_patch.sql` | Hash valid untuk user demo |

## 2. Perubahan pada file milik anggota lain (perlu persetujuan pemiliknya)

### 2.1 `util/WebUtil.java` (Anggota 2) - WAJIB

`DEV_MODE = true` membuat `currentUserId()` selalu mengembalikan `U00001` walau belum login,
jadi siapa pun bisa membuka data user demo. Ubah satu baris:

```diff
-    private static final boolean DEV_MODE = true;
+    private static final boolean DEV_MODE = false;
```

(`AuthFilter` sudah memblokir pengguna yang belum login, tapi servlet harus tetap aman sendiri - lapisan kedua.)

### 2.2 `database/seed.sql` (Anggota 1)

Password user demo di seed (`demo_password_hash`) bukan hash yang valid. Jalankan `seed_auth_patch.sql`
setelah `seed.sql`, atau pindahkan isinya ke `seed.sql`.
Login demo: `demo@budgetbuddies.com` / `Demo1234`.

### 2.3 `config/DBConnection.java` (Anggota 1) - disarankan

Versi sekarang menulis `root` dan password kosong langsung di kode. Versi di `suggested-changes/DBConnection.java`
membaca `DB_URL`, `DB_USER`, `DB_PASSWORD` dari environment/system property; kalau tidak diset, memakai nilai
Laragon lokal yang sama seperti sekarang, jadi tidak mengubah cara kerja di laptop. Dibutuhkan untuk deployment.

### 2.4 `WEB-INF/web.xml` (bersama)

Versi baru menambahkan: cookie session `HttpOnly` + `SameSite=Lax`, session hanya lewat cookie (ID tidak muncul di URL),
dan halaman error 403/404/500. `session-timeout` tetap 30 menit. Kalau ada perubahan lain di web.xml, gabungkan manual.

### 2.5 `webapp/index.jsp` (opsional)

Sekarang mengarah ke `/transactions`. Karena dashboard sudah ada, lebih tepat mengarah ke `/dashboard`.

## 3. Kontrak untuk tim (tambahan untuk API_CONTRACT.md)

| Fitur | URL | Method | Parameter |
|---|---|---|---|
| Form login | `/login` | GET | - |
| Login | `/login` | POST | `email`, `password` |
| Form register | `/register` | GET | - |
| Register | `/register` | POST | `name`, `email`, `password`, `confirmPassword` |
| Logout | `/logout` | POST | - (tombol "Keluar" di `header.jsp` sudah POST) |

Atribut session setelah login:

| Atribut | Tipe | Keterangan |
|---|---|---|
| `userId` | String | Dipakai `WebUtil.currentUserId()` (`WebUtil.SESSION_USER_ID`) |
| `userName` | String | Ditampilkan di navbar (`sessionScope.userName`) |

Aturan: email disimpan huruf kecil dan unik. Password 8-128 karakter, wajib ada huruf dan angka.
Format hash di `U_password`: `pbkdf2_sha256$<iterasi>$<salt>$<hash>` (90 karakter, muat di `VARCHAR(255)`).

## 4. Cara memasukkan ke repo

```bash
git checkout feature/frontend-jsp          # atau develop
git checkout -b feature/authentication
# salin isi folder src/ dan suggested-changes/ ke proyek (jangan menimpa file lain)
git add .
git commit -m "feat: add user authentication"
git push -u origin feature/authentication
# lalu buat Pull Request ke develop, sertakan hasil pengujian dari TEST_CASES.md
```

## 5. Batasan yang diketahui (jujur)

- Kode sudah dikompilasi terhadap stub Servlet API dan logika `PasswordUtil` + `AuthFilter` sudah diuji otomatis
  secara terpisah. **Belum dijalankan di Tomcat + MySQL sungguhan.** Semua skenario di `TEST_CASES.md` harus dijalankan manual.
- Tidak ada pembatasan percobaan login (brute force) dan tidak ada token CSRF di form. Risiko CSRF dikurangi oleh
  `SameSite=Lax`, tapi bukan pengganti token. Bisa disebut sebagai pengembangan lanjutan di laporan.
- Hash 600.000 iterasi butuh sekitar 1 detik di lingkungan pengujian; di hosting gratis yang CPU-nya lemah bisa lebih lama.
  Kalau terasa lambat, turunkan `ITERATIONS` di `PasswordUtil` (hash lama tetap valid karena jumlah iterasi tersimpan di hash).
- Sesi tidak memeriksa ulang apakah user masih ada di database (belum ada fitur hapus akun).
