import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil
{
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String hash(String password)
    {
        try
        {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return saltBase64 + ":" + hashBase64;

        } catch (Exception e)
        {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    public static boolean verify(String password, String stored)
    {
        try
        {
            String[] parts = stored.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] computedHash = factory.generateSecret(spec).getEncoded();

            if (computedHash.length != storedHash.length) return false;

            int result = 0;
            for (int i = 0; i < computedHash.length; i++)
            {
                result |= computedHash[i] ^ storedHash[i];
            }
            return result == 0;

        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean isHashed(String password)
    {
        if (password == null || !password.contains(":")) return false;
        String[] parts = password.split(":");
        if (parts.length != 2) return false;

        try
        {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            return salt.length == SALT_LENGTH && hash.length == KEY_LENGTH / 8;
        } catch (Exception e)
        {
            return false;
        }
    }
}
