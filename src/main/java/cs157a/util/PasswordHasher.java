package cs157a.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Turns a plain password into a value safe to store in users.password_hash,
 * and checks a login attempt against a stored value.
 *
 * Uses PBKDF2, which the JDK provides, so no extra dependency is needed.
 * The stored string packs everything needed to verify it later:
 *
 *     pbkdf2-sha256$210000$BASE64_SALT$BASE64_HASH
 *      algorithm     rounds   salt        hash
 *
 * Because the parameters travel with the hash, we can raise the round count
 * later without forcing existing users to reset their password.
 */
public class PasswordHasher {

    private static final String ALGORITHM  = "PBKDF2WithHmacSHA256";
    private static final String PREFIX     = "pbkdf2-sha256";
    private static final int    ITERATIONS = 210000;
    private static final int    SALT_BYTES = 16;
    private static final int    KEY_BITS   = 256;

    /** Hashes a new password. The result goes straight into password_hash. */
    public static String hash(String plainPassword) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);

        byte[] key = pbkdf2(plainPassword, salt, ITERATIONS);

        return PREFIX + "$" + ITERATIONS
                + "$" + Base64.getEncoder().encodeToString(salt)
                + "$" + Base64.getEncoder().encodeToString(key);
    }

    /**
     * Returns true when plainPassword produced the stored value.
     * Never compares plain passwords - it re-hashes the attempt with the same
     * salt and rounds, then compares the two hashes.
     */
    public static boolean verify(String plainPassword, String stored) {
        if (plainPassword == null || stored == null) {
            return false;
        }

        String[] parts = stored.split("[$]");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;                      // not a format we understand
        }

        int    rounds       = Integer.parseInt(parts[1]);
        byte[] salt         = Base64.getDecoder().decode(parts[2]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

        byte[] actualHash = pbkdf2(plainPassword, salt, rounds);

        // isEqual takes the same time whatever the inputs, which avoids
        // leaking how much of the hash matched.
        return MessageDigest.isEqual(expectedHash, actualHash);
    }

    /** Runs the key derivation itself. */
    private static byte[] pbkdf2(String password, byte[] salt, int rounds) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, rounds, KEY_BITS);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot hash password", e);
        }
    }

    /**
     * Prints a hash so it can be pasted into 02_seed.sql.
     * Usage: java cs157a.util.PasswordHasher myPassword
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java cs157a.util.PasswordHasher <password>");
            return;
        }
        System.out.println(hash(args[0]));
    }
}
