import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONObject;

public class AuthHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {
        String method = exchange.getRequestMethod();

        try
        {
            if ("OPTIONS".equals(method))
            {
                sendResponse(exchange, 204, "");
                return;
            }

            if (!"POST".equals(method))
            {
                sendResponse(exchange, 405, new JSONObject().put("error", "Método no permitido").toString());
                return;
            }

            String body = readBody(exchange);
            JSONObject json = new JSONObject(body);

            String username = json.optString("username", "").trim();
            String password = json.optString("password", "").trim();

            if (username.isEmpty() || password.isEmpty())
            {
                sendResponse(exchange, 400, new JSONObject().put("error", "Usuario y contraseña son obligatorios").toString());
                return;
            }

            JSONObject user = DatabaseManager.authenticateUser(username, password);

            if (user == null)
            {
                sendResponse(exchange, 401, new JSONObject().put("error", "Credenciales inválidas").toString());
                return;
            }

            String token = JwtUtil.generateToken(username);

            JSONObject response = new JSONObject();
            response.put("token", token);
            response.put("username", username);

            sendResponse(exchange, 200, response.toString());

        } catch (Exception e)
        {
            sendResponse(exchange, 500, new JSONObject().put("error", "Error interno del servidor").toString());
        }
    }

    private String readBody(HttpExchange exchange) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "UTF-8")))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void addCorsHeaders(HttpExchange exchange)
    {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException
    {
        addCorsHeaders(exchange);
        if (statusCode != 204)
        {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        }
        byte[] bytes = body.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, statusCode == 204 ? -1 : bytes.length);
        if (statusCode != 204)
        {
            try (OutputStream os = exchange.getResponseBody())
            {
                os.write(bytes);
            }
        } else
        {
            exchange.getResponseBody().close();
        }
    }
}
