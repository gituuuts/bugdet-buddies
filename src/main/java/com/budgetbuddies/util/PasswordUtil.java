package com.budgetbuddies.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hash password dengan PBKDF2-HMAC-SHA256 (bawaan JDK, tanpa dependency tambahan).
 *
 * Format yang disimpan di kolom U_password (VARCHAR 255):
 *   pbkdf2_sha256$<iterasi>$<salt base64>$<hash base64>
 * Jumlah iterasi ikut tersimpan, jadi bisa dinaikkan nanti tanpa merusak password lama.
 */
public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2_sha256";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000; // rekomendasi OWASP untuk PBKDF2-HMAC-SHA256
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hash palsu untuk "menghabiskan waktu" saat email tidak ditemukan,
     * supaya waktu respon login tidak membocorkan email mana yang terdaftar.
     */
    public static final String DUMMY_HASH = hash("dummy-password-for-timing");

    private PasswordUtil() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] key = derive(password.toCharArray(), salt, ITERATIONS);
        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(key);
    }

    /** true jika password cocok dengan hash. Hash rusak/tidak dikenal -> false (tidak error). */
    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) {
            return false;
        }
        String[] parts = stored.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = derive(password.toCharArray(), salt, iterations);
            return MessageDigest.isEqual(expected, actual); // perbandingan waktu-konstan
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 tidak tersedia di JVM ini", e);
        } finally {
            spec.clearPassword();
        }
    }
}
