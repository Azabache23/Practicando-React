import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseManager
{
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 5432;
    private static final String DB_NAME = "clientes_app";
    private static final String AUTH_DB_NAME = "usuarios_API";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "postgres";

    public static void initialize()
    {
        crearBaseDeDatos(DB_NAME);
        crearBaseDeDatos(AUTH_DB_NAME);
        crearTablaUsuarios();
        crearTablaClientes();
        migratePasswords();
    }

    private static void crearBaseDeDatos(String dbName)
    {
        String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/postgres";

        try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement())
        {
            ResultSet rs = stmt.executeQuery(
                "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'"
            );

            if (!rs.next())
            {
                stmt.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                System.out.println("Base de datos '" + dbName + "' creada correctamente");
            }

            rs.close();

        } catch (Exception e)
        {
            System.out.println("Nota: " + e.getMessage());
        }
    }

    private static void crearTablaUsuarios()
    {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                     "id SERIAL PRIMARY KEY, " +
                     "username VARCHAR(100) UNIQUE NOT NULL, " +
                     "password VARCHAR(255) NOT NULL, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";

        try (Connection conn = getAuthConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.executeUpdate(sql);
            System.out.println("Tabla 'usuarios' inicializada en '" + AUTH_DB_NAME + "'");

        } catch (Exception e)
        {
            System.err.println("Error al crear tabla usuarios: " + e.getMessage());
        }
    }

    private static void crearTablaClientes()
    {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                     "id VARCHAR(255) PRIMARY KEY, " +
                     "nombre VARCHAR(255) NOT NULL, " +
                     "ubicacion VARCHAR(255) NOT NULL, " +
                     "nif VARCHAR(255) NOT NULL" +
                     ")";

        try (Connection conn = getClientesConnection();
             Statement stmt = conn.createStatement())
        {
            stmt.executeUpdate(sql);
            System.out.println("Tabla 'clientes' inicializada en '" + DB_NAME + "'");

        } catch (Exception e)
        {
            System.err.println("Error al crear tabla clientes: " + e.getMessage());
        }
    }

    public static Connection getClientesConnection() throws Exception
    {
        String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public static Connection getAuthConnection() throws Exception
    {
        String url = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + AUTH_DB_NAME;
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    public static Connection getConnection() throws Exception
    {
        return getClientesConnection();
    }

    // ==================== CRUD Clientes ====================

    public static JSONArray getAllClientes()
    {
        JSONArray array = new JSONArray();
        String sql = "SELECT id, nombre, ubicacion, nif FROM clientes";

        try (Connection conn = getClientesConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                JSONObject cliente = new JSONObject();
                cliente.put("id", rs.getString("id"));
                cliente.put("nombre", rs.getString("nombre"));
                cliente.put("ubicacion", rs.getString("ubicacion"));
                cliente.put("nif", rs.getString("nif"));
                array.put(cliente);
            }

        } catch (Exception e)
        {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }

        return array;
    }

    public static JSONObject getClienteById(String id)
    {
        String sql = "SELECT id, nombre, ubicacion, nif FROM clientes WHERE id = ?";

        try (Connection conn = getClientesConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                JSONObject cliente = new JSONObject();
                cliente.put("id", rs.getString("id"));
                cliente.put("nombre", rs.getString("nombre"));
                cliente.put("ubicacion", rs.getString("ubicacion"));
                cliente.put("nif", rs.getString("nif"));
                return cliente;
            }

        } catch (Exception e)
        {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject createCliente(String id, JSONObject json)
    {
        String sql = "INSERT INTO clientes (id, nombre, ubicacion, nif) VALUES (?, ?, ?, ?)";

        try (Connection conn = getClientesConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, id);
            stmt.setString(2, json.getString("nombre"));
            stmt.setString(3, json.getString("ubicacion"));
            stmt.setString(4, json.getString("nif"));
            stmt.executeUpdate();

            JSONObject cliente = new JSONObject();
            cliente.put("id", id);
            cliente.put("nombre", json.getString("nombre"));
            cliente.put("ubicacion", json.getString("ubicacion"));
            cliente.put("nif", json.getString("nif"));
            return cliente;

        } catch (Exception e)
        {
            System.err.println("Error al crear cliente: " + e.getMessage());
        }

        return null;
    }

    public static JSONObject updateCliente(String id, JSONObject json)
    {
        String sql = "UPDATE clientes SET nombre = ?, ubicacion = ?, nif = ? WHERE id = ?";

        try (Connection conn = getClientesConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, json.getString("nombre"));
            stmt.setString(2, json.getString("ubicacion"));
            stmt.setString(3, json.getString("nif"));
            stmt.setString(4, id);
            int rows = stmt.executeUpdate();

            if (rows > 0)
            {
                JSONObject cliente = new JSONObject();
                cliente.put("id", id);
                cliente.put("nombre", json.getString("nombre"));
                cliente.put("ubicacion", json.getString("ubicacion"));
                cliente.put("nif", json.getString("nif"));
                return cliente;
            }

        } catch (Exception e)
        {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }

        return null;
    }

    public static boolean deleteCliente(String id)
    {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = getClientesConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, id);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e)
        {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }

        return false;
    }

    // ==================== Autenticación ====================

    public static JSONObject authenticateUser(String username, String password)
    {
        String sql = "SELECT username, password FROM usuarios WHERE username = ?";

        try (Connection conn = getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) return null;

            String storedPassword = rs.getString("password");

            if (!PasswordUtil.verify(password, storedPassword)) return null;

            JSONObject user = new JSONObject();
            user.put("username", rs.getString("username"));
            return user;

        } catch (Exception e)
        {
            System.err.println("Error de autenticación: " + e.getMessage());
            return null;
        }
    }

    public static void migratePasswords()
    {
        String selectSql = "SELECT id, password FROM usuarios";
        String updateSql = "UPDATE usuarios SET password = ? WHERE id = ?";

        try (Connection conn = getAuthConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql))
        {
            int migrated = 0;

            while (rs.next())
            {
                int id = rs.getInt("id");
                String password = rs.getString("password");

                if (!PasswordUtil.isHashed(password))
                {
                    String hashed = PasswordUtil.hash(password);

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql))
                    {
                        updateStmt.setString(1, hashed);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                        migrated++;
                    }
                }
            }

            if (migrated > 0)
            {
                System.out.println(migrated + " contraseña(s) migrada(s) a PBKDF2");
            }

        } catch (Exception e)
        {
            System.err.println("Error al migrar contraseñas: " + e.getMessage());
        }
    }
}
