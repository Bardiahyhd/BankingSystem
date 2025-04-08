import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class BankSecurity {
    public static String Hash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    static boolean CharCheck(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (('a' < string.charAt(i) && string.charAt(i) < 'z') || ('A' < string.charAt(i) && string.charAt(i) < 'Z')) {
                return true;
            }
        }
        return false;
    }
}