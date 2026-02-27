import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class Servidor
{
    public static void main(String[] args) throws IOException
    {
        DatabaseManager.initialize();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/clientes", new ClientesHandler());
        server.createContext("/api/auth/login", new AuthHandler());
        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080");
    }

    static class ClientesHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String id = null;

            // Extraer ID si existe: /api/clientes/{id}
            String prefix = "/api/clientes";
            if (path.length() > prefix.length() + 1)
            {
                id = path.substring(prefix.length() + 1);
            }

            try
            {
                // OPTIONS pasa sin validación (preflight CORS)
                if ("OPTIONS".equals(method))
                {
                    sendResponse(exchange, 204, "");
                    return;
                }

                // Validar JWT para todos los demás métodos
                String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer "))
                {
                    sendResponse(exchange, 401, new JSONObject().put("error", "Token de autenticación requerido").toString());
                    return;
                }

                String token = authHeader.substring(7);
                if (JwtUtil.validateToken(token) == null)
                {
                    sendResponse(exchange, 401, new JSONObject().put("error", "Token inválido o expirado").toString());
                    return;
                }

                switch (method)
                {
                    case "GET":
                        if (id == null)
                        {
                            handleGetAll(exchange);
                        }
                        else
                        {
                            handleGetById(exchange, id);
                        }
                        break;

                    case "POST":
                        handlePost(exchange);
                        break;

                    case "PUT":
                        handlePut(exchange, id);
                        break;

                    case "DELETE":
                        handleDelete(exchange, id);
                        break;

                    default:
                        sendResponse(exchange, 405, new JSONObject().put("error", "Método no permitido").toString());
                        break;
                }

            } catch (Exception e)
            {
                sendResponse(exchange, 500, new JSONObject().put("error", e.getMessage()).toString());
            }
        }

        private void handleGetAll(HttpExchange exchange) throws IOException
        {
            JSONArray array = DatabaseManager.getAllClientes();
            sendResponse(exchange, 200, array.toString());
        }

        private void handleGetById(HttpExchange exchange, String id) throws IOException
        {
            JSONObject cliente = DatabaseManager.getClienteById(id);
            if (cliente == null)
            {
                sendResponse(exchange, 404, new JSONObject().put("error", "Cliente no encontrado").toString());
                return;
            }

            sendResponse(exchange, 200, cliente.toString());
        }

        private void handlePost(HttpExchange exchange) throws IOException
        {
            String body = readBody(exchange);
            JSONObject json = new JSONObject(body);

            // Validar campos requeridos
            String error = validarCampos(json);
            if (error != null)
            {
                sendResponse(exchange, 400, new JSONObject().put("error", error).toString());
                return;
            }

            String id = json.optString("id", UUID.randomUUID().toString());

            // Detectar ID duplicado
            if (DatabaseManager.getClienteById(id) != null)
            {
                sendResponse(exchange, 409, new JSONObject().put("error", "Ya existe un cliente con el ID: " + id).toString());
                return;
            }

            JSONObject created = DatabaseManager.createCliente(id, json);
            if (created == null)
            {
                sendResponse(exchange, 500, new JSONObject().put("error", "Error al crear cliente").toString());
                return;
            }

            sendResponse(exchange, 201, created.toString());
        }

        private void handlePut(HttpExchange exchange, String id) throws IOException
        {
            if (id == null || DatabaseManager.getClienteById(id) == null)
            {
                sendResponse(exchange, 404, new JSONObject().put("error", "Cliente no encontrado").toString());
                return;
            }
            String body = readBody(exchange);
            JSONObject json = new JSONObject(body);

            // Validar campos requeridos
            String error = validarCampos(json);
            if (error != null)
            {
                sendResponse(exchange, 400, new JSONObject().put("error", error).toString());
                return;
            }

            JSONObject updated = DatabaseManager.updateCliente(id, json);
            if (updated == null)
            {
                sendResponse(exchange, 500, new JSONObject().put("error", "Error al actualizar cliente").toString());
                return;
            }

            sendResponse(exchange, 200, updated.toString());
        }

        private void handleDelete(HttpExchange exchange, String id) throws IOException
        {
            if (id == null || !DatabaseManager.deleteCliente(id))
            {
                sendResponse(exchange, 404, new JSONObject().put("error", "Cliente no encontrado").toString());
                return;
            }

            sendResponse(exchange, 204, "");
        }

        private String validarCampos(JSONObject json)
        {
            String nombre = json.optString("nombre", "").trim();
            String ubicacion = json.optString("ubicacion", "").trim();
            String nif = json.optString("nif", "").trim();

            if (nombre.isEmpty()) return "El campo 'nombre' es obligatorio";
            if (ubicacion.isEmpty()) return "El campo 'ubicacion' es obligatorio";
            if (nif.isEmpty()) return "El campo 'nif' es obligatorio";

            return null;
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
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
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
}
