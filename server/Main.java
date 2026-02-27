import org.json.JSONArray;
import org.json.JSONObject;

public class Main
{
    public static void main(String[] args)
    {
        GestorCliente gestor = new GestorCliente();

        // CREATE
        System.out.println("=== CREAR CLIENTE ===");
        Cliente nuevo = new Cliente("1", "Juan Pérez", "Madrid", "12345678A");
        JSONObject respuestaCrear = gestor.crearCliente(nuevo);
        System.out.println(respuestaCrear.toString(2));

        // READ (todos)
        System.out.println("\n=== OBTENER TODOS LOS CLIENTES ===");
        JSONArray respuestaTodos = gestor.obtenerTodosClientes();
        System.out.println(respuestaTodos.toString(2));

        // READ (por ID)
        System.out.println("\n=== OBTENER CLIENTE POR ID ===");
        JSONObject respuestaUno = gestor.obtenerClientePorId("1");
        System.out.println(respuestaUno.toString(2));

        // UPDATE
        System.out.println("\n=== ACTUALIZAR CLIENTE ===");
        Cliente actualizado = new Cliente("1", "Juan Pérez García", "Barcelona", "12345678A");
        JSONObject respuestaActualizar = gestor.actualizarCliente("1", actualizado);
        System.out.println(respuestaActualizar.toString(2));

        // DELETE
        System.out.println("\n=== ELIMINAR CLIENTE ===");
        JSONObject respuestaEliminar = gestor.eliminarCliente("1");
        System.out.println(respuestaEliminar.toString(2));
    }
}
