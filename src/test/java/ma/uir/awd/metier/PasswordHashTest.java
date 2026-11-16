package ma.uir.awd.metier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying BCrypt password hashing logic (R1).
 */
public class PasswordHashTest {

    @Test
    @DisplayName("BCrypt generates unique salt and verifies matching password")
    void testPasswordHashAndVerify() {
        String rawPassword = "MySecretPassword123!";
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$12$") || hash.startsWith("$2b$12$") || hash.startsWith("$2y$12$"),
                "Hash must use BCrypt prefix with work factor 12");
        assertTrue(BCrypt.checkpw(rawPassword, hash), "Password check must succeed for matching raw password");
    }

    @Test
    @DisplayName("BCrypt rejects incorrect password")
    void testWrongPasswordRejected() {
        String rawPassword = "CorrectPassword";
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        assertFalse(BCrypt.checkpw("WrongPassword", hash), "Password check must return false for mismatch");
        assertFalse(BCrypt.checkpw("", hash), "Password check must return false for empty password");
    }

    @Test
    @DisplayName("Different salts produce different hashes for identical passwords")
    void testSaltsDiffer() {
        String rawPassword = "IdenticalPassword";
        String hash1 = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
        String hash2 = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        assertNotEquals(hash1, hash2, "BCrypt must generate a new per-user salt on every hash");
        assertTrue(BCrypt.checkpw(rawPassword, hash1));
        assertTrue(BCrypt.checkpw(rawPassword, hash2));
    }
}
