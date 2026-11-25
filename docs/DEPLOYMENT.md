# Budget Buddies - Panduan Deployment

Status: **belum diuji** di server mana pun. Jangan menulis "deployment berhasil" sebelum checklist bagian 6 selesai.

## 1. Kebutuhan

| Komponen | Versi | Catatan |
|---|---|---|
| JDK | 17 atau lebih baru | `pom.xml` memakai `maven.compiler.release` 17, jadi JDK 17+ wajib |
| Maven | 3.9.x | Untuk build WAR |
| Tomcat | **10.1.x** | Harus 10.x (Jakarta, `jakarta.servlet`). Tomcat 9 tidak akan jalan |
| MySQL / MariaDB | MySQL >= 8.0.16 atau MariaDB >= 10.2.1 | Versi lebih lama mengabaikan `CHECK` diam-diam. Catat versi final di laporan |

Semua anggota harus memakai versi JDK yang sama.

## 2. Menjalankan secara lokal (Laragon + Tomcat)

1. **Database.** Start MySQL di Laragon, lalu jalankan berurutan:
   `schema.sql`, `seed.sql`, `seed_auth_patch.sql` (semua di `src/main/database/`).
2. **Build.** Di folder proyek: `mvn clean package`. Hasil: `target/budget-buddies.war`.
3. **Deploy.** Salin WAR ke `<tomcat>/webapps/`, lalu jalankan `bin/catalina.bat run` (Windows) atau `bin/catalina.sh run`.
4. **Buka** `http://localhost:8080/budget-buddies/` dan login dengan `demo@budgetbuddies.com` / `Demo1234`.

Tanpa environment variable, aplikasi memakai default Laragon (`root`, password kosong, `localhost:3306`)
**setelah** `DBConnection.java` diganti dengan versi di `suggested-changes/`.

## 3. Konfigurasi database lewat environment (tanpa password di GitHub)

`DBConnection` membaca tiga nilai: `DB_URL`, `DB_USER`, `DB_PASSWORD` (environment variable atau system property `-D`).

Contoh `<tomcat>/bin/setenv.bat` (Windows):

```bat
set "CATALINA_OPTS=-DDB_URL=jdbc:mysql://HOST:3306/budget_buddies?serverTimezone=Asia/Jakarta -DDB_USER=USER -DDB_PASSWORD=PASSWORD"
```

Contoh `<tomcat>/bin/setenv.sh` (Linux):

```bash
export CATALINA_OPTS="-DDB_URL=jdbc:mysql://HOST:3306/budget_buddies?serverTimezone=Asia/Jakarta -DDB_USER=USER -DDB_PASSWORD=PASSWORD"
```

Jangan commit `setenv.*` yang berisi password. Jangan kirim password database ke AI.

## 4. Hosting gratis (syarat Quiz 2)

Hasil pencarian saya (November 2026) tidak menemukan satu pun penyedia yang bisa saya rekomendasikan dengan pasti.
Gambaran umumnya: hosting Tomcat/JSP yang benar-benar gratis jarang dan terbatas; RAM biasanya 128-512 MB,
MySQL sering dibatasi, dan SSL sering tidak termasuk. Syarat dan batas gratis berubah-ubah, jadi **cek sendiri
halaman resmi penyedia sebelum memilih**.

Langkah memilih:

1. **Tanya dosen** apa yang dihitung sebagai "free web hosting" (apakah platform cloud dengan free tier / trial boleh).
2. Cari opsi yang menerima **WAR/Tomcat** atau **Docker**. Kalau Docker, pakai `Dockerfile` di root paket ini.
3. Siapkan **MySQL terpisah** (banyak host gratis tidak menyediakan MySQL). Cari layanan MySQL/MariaDB gratis atau
   free tier, lalu isi `DB_URL`, `DB_USER`, `DB_PASSWORD` dari panelnya.
4. Impor `schema.sql`, `seed.sql`, `seed_auth_patch.sql` ke database itu. Di server Linux nama tabel peka huruf besar/kecil;
   kode sudah konsisten memakai `Users`, `Accounts`, dst., jadi **jangan ubah huruf nama tabel** saat impor.
5. Deploy, lalu jalankan checklist bagian 6 dan isi bagian F di `TEST_CASES.md`.

Catatan khusus:
- Dockerfile memakai `ROOT.war`, jadi aplikasi terbuka di `/` (bukan `/budget-buddies/`). Kode memakai `contextPath`, jadi aman.
- Beberapa platform memberi port lewat variabel `PORT`. Tomcat default di port 8080; kalau platform mewajibkan port lain,
  sesuaikan konfigurasi platform atau `server.xml`.
- Hash password 600.000 iterasi bisa lambat di CPU kecil. Kalau login terasa berat, turunkan `ITERATIONS` di `PasswordUtil`.
- Aplikasi Java di tier gratis sering "tidur" saat tidak dipakai; buka sebelum demo agar tidak kaget.
- Setelah online, jalankan juga `SameSite`/cookie check (AUTH-19). Kalau situs memakai HTTPS, boleh menambahkan
  `<secure>true</secure>` di `cookie-config` pada `web.xml`.

## 5. Isi repository GitHub

Wajib ada: source code, `pom.xml`, `schema.sql`, `seed.sql`, `seed_auth_patch.sql`, `README.md`, dokumentasi.
Tidak boleh ada: password/credential, `target/`, file `.env`. `.gitignore` saat ini sudah menyaring `target/`.
Tambahkan juga `setenv.bat`, `setenv.sh`, `.env` ke `.gitignore`.

## 6. Checklist deployment

- [ ] JDK 17+ dan Tomcat 10.1 terpasang, versinya dicatat
- [ ] Database dibuat dan tiga file SQL berhasil dijalankan
- [ ] `mvn clean package` berhasil, WAR terbentuk
- [ ] Environment variable database diset di server (bukan di kode)
- [ ] Halaman login terbuka
- [ ] Login dan logout berhasil
- [ ] Satu operasi CRUD di tiap modul berhasil
- [ ] Log server diperiksa, tidak ada exception tak terduga
- [ ] Tidak ada credential di repository (`git grep -i password`)
- [ ] URL online tercatat untuk laporan

## 7. Error umum

| Gejala | Kemungkinan penyebab | Perbaikan |
|---|---|---|
| `UnsupportedClassVersionError` | JDK yang menjalankan Tomcat lebih lama dari 17 | Pakai JDK 17+ untuk Tomcat |
| 404 untuk semua halaman | URL tanpa nama WAR, atau WAR tidak ter-deploy | Buka `/budget-buddies/`; cek log `catalina.out` |
| `ClassNotFoundException: jakarta.servlet...` atau halaman JSP error | Tomcat 9 dipakai | Pakai Tomcat 10.1 |
| `No suitable driver` / `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Driver MySQL tidak ikut di WAR | Build ulang dengan `mvn clean package` (dependency non-`provided` ikut masuk WAR) |
| `Communications link failure` | MySQL mati / host atau port salah / firewall | Cek `DB_URL`, status MySQL, izin akses dari host aplikasi |
| `Access denied for user` | `DB_USER`/`DB_PASSWORD` salah | Periksa nilai environment |
| `Table '...' doesn't exist` di Linux | Nama tabel berbeda huruf besar/kecil saat impor | Impor `schema.sql` apa adanya |
| Port 8080 sudah dipakai | Aplikasi lain memakai port itu | Hentikan aplikasi itu atau ganti port Tomcat |
| Login selalu "Email atau password salah" untuk user demo | `seed_auth_patch.sql` belum dijalankan | Jalankan patch |
| Semua halaman terbuka tanpa login | `WebUtil.DEV_MODE` masih `true` dan/atau `AuthFilter` tidak ikut ter-build | Set `DEV_MODE = false`; pastikan `filter/AuthFilter.java` ada di WAR |
