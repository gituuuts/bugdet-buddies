package com.budgetbuddies.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper kecil untuk Servlet: ambil user dari session, flash message, dan ID.
 */
public final class WebUtil {

    /** Nama atribut session yang diisi Anggota 3 saat login. WAJIB disepakati. */
    public static final String SESSION_USER_ID = "userId";

    /**
     * SEMENTARA untuk development sebelum login Anggota 3 selesai.
     * Set false (atau hapus) setelah authentication jadi.
     */
    private static final boolean DEV_MODE = false;
    private static final String DEV_USER_ID = "U00001"; // user dari seed.sql

    private WebUtil() {
    }

    /** User ID selalu dari session server, bukan dari parameter browser. */
    public static String currentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(SESSION_USER_ID) != null) {
            return (String) session.getAttribute(SESSION_USER_ID);
        }
        return DEV_MODE ? DEV_USER_ID : null;
    }

    // ---------- Flash message (pola Post-Redirect-Get) ----------

    public static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute("flashSuccess", message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute("flashError", message);
    }

    /** Pindahkan flash dari session ke request, lalu hapus dari session. */
    public static void moveFlashToRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        for (String key : new String[]{"flashSuccess", "flashError"}) {
            Object value = session.getAttribute(key);
            if (value != null) {
                req.setAttribute(key, value);
                session.removeAttribute(key);
            }
        }
    }

    // ---------- ID CHAR(6) ----------

    /**
     * Buat ID 6 karakter, mis. "T48213". Ada kemungkinan kecil bentrok,
     * jadi pemanggil mengulang jika insert gagal.
     * Idealnya Anggota 1 menyediakan generator ID di DAO.
     */
    public static String newId(String prefix) {
        int n = ThreadLocalRandom.current().nextInt(100000);
        return prefix + String.format("%05d", n);
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
