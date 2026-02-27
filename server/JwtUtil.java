import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import org.json.JSONObject;

public class JwtUtil
{
    private static final String SECRET = "clave-secreta-jwt-clientes-app-2025";
    private static final long EXPIRATION_MS = 3600000; // 1 hora

    public static String generateToken(String username)
    {
        try
        {
            JSONObject header = new JSONObject();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = System.currentTimeMillis() / 1000;

            JSONObject payload = new JSONObject();
            payload.put("sub", username);
            payload.put("iat", now);
            payload.put("exp", now + (EXPIRATION_MS / 1000));

            String headerB64 = base64UrlEncode(header.toString().getBytes("UTF-8"));
            String payloadB64 = base64UrlEncode(payload.toString().getBytes("UTF-8"));

            String signature = sign(headerB64 + "." + payloadB64);

            return headerB64 + "." + payloadB64 + "." + signature;

        } catch (Exception e)
        {
            throw new RuntimeException("Error al generar token JWT", e);
        }
    }

    public static JSONObject validateToken(String token)
    {
        try
        {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String signature = sign(parts[0] + "." + parts[1]);
            if (!signature.equals(parts[2])) return null;

            String payloadJson = new String(base64UrlDecode(parts[1]), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);

            long exp = payload.getLong("exp");
            long now = System.currentTimeMillis() / 1000;
            if (now > exp) return null;

            return payload;

        } catch (Exception e)
        {
            return null;
        }
    }

    private static String sign(String data) throws Exception
    {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes("UTF-8"), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
        return base64UrlEncode(hash);
    }

    private static String base64UrlEncode(byte[] data)
    {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static byte[] base64UrlDecode(String data)
    {
        return Base64.getUrlDecoder().decode(data);
    }
}
