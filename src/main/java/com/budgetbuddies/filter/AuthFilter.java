package com.budgetbuddies.filter;

import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * Pagar utama aplikasi (default-deny): SEMUA URL wajib login,
 * kecuali halaman publik di bawah ini.
 *
 * Servlet modul lain tetap memeriksa userId sendiri (lapisan kedua).
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    /** URL yang boleh dibuka tanpa login. */
    private static final Set<String> PUBLIC_PATHS = Set.of("/login", "/register");

    /** File statis yang boleh dibuka tanpa login (agar halaman login bisa memakai CSS/JS). */
    private static final String[] PUBLIC_PREFIXES = {"/css/", "/js/"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        boolean isStatic = startsWithAny(path, PUBLIC_PREFIXES);

        addSecurityHeaders(resp, isStatic);

        if (isStatic || PUBLIC_PATHS.contains(path) || isLoggedIn(req)) {
            chain.doFilter(request, response);
            return;
        }

        // Belum login -> ke halaman login (berlaku juga untuk POST)
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null
                && session.getAttribute(WebUtil.SESSION_USER_ID) instanceof String;
    }

    private void addSecurityHeaders(HttpServletResponse resp, boolean isStatic) {
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "same-origin");

        if (!isStatic) {
            // Setelah logout, tombol Back tidak boleh menampilkan halaman privat dari cache.
            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
        }
    }

    private static boolean startsWithAny(String path, String[] prefixes) {
        for (String p : prefixes) {
            if (path.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
