package com.budgetbuddies.servlet;

import com.budgetbuddies.dao.UserDAO;
import com.budgetbuddies.dao.UserDAO.InsertResult;
import com.budgetbuddies.model.User;
import com.budgetbuddies.util.PasswordUtil;
import com.budgetbuddies.util.WebUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Authentication:
 *   GET  /login      -> form login
 *   POST /login      -> proses login   (param: email, password)
 *   GET  /register   -> form register
 *   POST /register   -> proses register (param: name, email, password, confirmPassword)
 *   POST /logout     -> akhiri session
 *
 * Session setelah login: atribut "userId" (WebUtil.SESSION_USER_ID) dan "userName".
 */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private static final String LOGIN_VIEW = "/WEB-INF/views/login.jsp";
    private static final String REGISTER_VIEW = "/WEB-INF/views/register.jsp";

    public static final String SESSION_USER_NAME = "userName";

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MIN_PASSWORD = 8;
    private static final int MAX_PASSWORD = 128;

    private final UserDAO userDAO = new UserDAO();

    // =====================================================
    // GET
    // =====================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // /logout hanya boleh lewat POST; GET tidak mengakhiri session
        if ("/logout".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        if (isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        WebUtil.moveFlashToRequest(req);
        req.getRequestDispatcher("/register".equals(path) ? REGISTER_VIEW : LOGIN_VIEW)
                .forward(req, resp);
    }

    // =====================================================
    // POST
    // =====================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        switch (req.getServletPath()) {
            case "/login":
                handleLogin(req, resp);
                break;
            case "/register":
                handleRegister(req, resp);
                break;
            case "/logout":
                handleLogout(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ---------- LOGIN ----------
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = WebUtil.trim(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String password = req.getParameter("password"); // jangan di-trim

        req.setAttribute("email", email);

        if (email.isEmpty() || password == null || password.isEmpty()) {
            failLogin(req, resp, "Email dan password wajib diisi.");
            return;
        }

        User user = userDAO.findByEmail(email);

        // Selalu hitung hash, walau user tidak ada, agar waktu respon seragam.
        String storedHash = (user == null) ? PasswordUtil.DUMMY_HASH : user.getU_password();
        boolean passwordOk = PasswordUtil.verify(password, storedHash);

        if (user == null || !passwordOk) {
            // Pesan sama untuk "email tidak ada" dan "password salah".
            failLogin(req, resp, "Email atau password salah.");
            return;
        }

        // Ganti ID session setelah login (mencegah session fixation).
        HttpSession session = req.getSession(true);
        req.changeSessionId();
        session.setAttribute(WebUtil.SESSION_USER_ID, user.getU_id());
        session.setAttribute(SESSION_USER_NAME, user.getU_name());

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private void failLogin(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
    }

    // ---------- REGISTER ----------
    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = WebUtil.trim(req.getParameter("name"));
        String email = WebUtil.trim(req.getParameter("email")).toLowerCase(Locale.ROOT);
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        req.setAttribute("name", name);
        req.setAttribute("email", email);

        String error = validateRegistration(name, email, password, confirm);
        if (error != null) {
            failRegister(req, resp, error, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (userDAO.emailExists(email)) {
            failRegister(req, resp, "Email sudah terdaftar. Silakan login.", HttpServletResponse.SC_CONFLICT);
            return;
        }

        User user = new User();
        user.setU_name(name);
        user.setU_email(email);
        user.setU_password(PasswordUtil.hash(password));

        // ID CHAR(6): ulangi bila kebetulan bentrok dengan ID yang sudah ada.
        for (int i = 0; i < 5; i++) {
            user.setU_id(WebUtil.newId("U"));
            InsertResult result = userDAO.insert(user);

            if (result == InsertResult.SUCCESS) {
                WebUtil.flashSuccess(req, "Registrasi berhasil. Silakan login.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (result == InsertResult.DUPLICATE_EMAIL) {
                failRegister(req, resp, "Email sudah terdaftar. Silakan login.", HttpServletResponse.SC_CONFLICT);
                return;
            }
            if (result == InsertResult.ERROR) {
                break;
            }
            // DUPLICATE_ID -> coba ID lain
        }
        failRegister(req, resp, "Registrasi gagal. Silakan coba lagi.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private String validateRegistration(String name, String email, String password, String confirm) {
        if (name.isEmpty()) return "Nama wajib diisi.";
        if (name.length() > 100) return "Nama maksimal 100 karakter.";
        if (email.isEmpty()) return "Email wajib diisi.";
        if (email.length() > 100) return "Email maksimal 100 karakter.";
        if (!EMAIL.matcher(email).matches()) return "Format email tidak valid.";
        if (password == null || password.isEmpty()) return "Password wajib diisi.";
        if (password.length() < MIN_PASSWORD) return "Password minimal " + MIN_PASSWORD + " karakter.";
        if (password.length() > MAX_PASSWORD) return "Password maksimal " + MAX_PASSWORD + " karakter.";
        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return "Password harus mengandung huruf dan angka.";
        }
        if (!password.equals(confirm)) return "Konfirmasi password tidak sama.";
        return null;
    }

    private void failRegister(HttpServletRequest req, HttpServletResponse resp, String message, int status)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        resp.setStatus(status);
        req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
    }

    // ---------- LOGOUT ----------
    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // Session baru hanya untuk membawa pesan "berhasil keluar".
        WebUtil.flashSuccess(req, "Anda telah keluar.");
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute(WebUtil.SESSION_USER_ID) != null;
    }
}
