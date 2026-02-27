import java.io.BufferedReader;                                                      
import java.io.InputStreamReader;                                                   
import java.io.OutputStream;                                                        
                                                                                    
import java.net.HttpURLConnection;                                                  
import java.net.URL;                                                                
                                                                                    
import org.json.JSONArray;                                                          
import org.json.JSONObject;                                                         
                                                                                    
public class GestorCliente                                                          
{                                                                                   
    private static final String BASE_URL = "http://localhost:8080/api/clientes";                                                                                 
                                                                                    
    // ==================== CREATE ====================                             
    public JSONObject crearCliente(Cliente cliente)                                 
    {                                                                               
        HttpURLConnection connection = null;                                        
                                                                                    
        try                                                                         
        {                                                                           
            URL url = new URL(BASE_URL);                                            
            connection = (HttpURLConnection) url.openConnection();                  
            connection.setRequestMethod("POST");                                    
            connection.setRequestProperty("Content-Type", "application/json");                                                                                   
            connection.setDoOutput(true);                                           
                                                                                    
            JSONObject json = cliente.toJson();                                     
                                                                                    
            try (OutputStream os = connection.getOutputStream())                    
            {                                                                       
                os.write(json.toString().getBytes("UTF-8"));                        
            }                                                                       
                                                                                    
            int codigo = connection.getResponseCode();                              
                                                                                    
            if (codigo == HttpURLConnection.HTTP_OK || codigo == HttpURLConnection.HTTP_CREATED)                                                                 
            {                                                                       
                return new JSONObject(leerRespuesta(connection));                   
                                                                                    
            } else                                                                  
            {                                                                       
                return new JSONObject().put("error", "Error al crear cliente. Código: " + codigo);                                                               
            }                                                                       
                                                                                    
        } catch (Exception e)                                                       
        {                                                                           
            return new JSONObject().put("error", "Error de conexión: " + e.getMessage());                                                                        
                                                                                    
        } finally                                                                   
        {                                                                           
            if (connection != null) connection.disconnect();                        
        }                                                                           
    }                                                                               
                                                                                    
    // ==================== READ (todos) ====================                       
    public JSONArray obtenerTodosClientes()                                         
    {                                                                               
        HttpURLConnection connection = null;                                        
                                                                                    
        try                                                                         
        {                                                                           
            URL url = new URL(BASE_URL);                                            
            connection = (HttpURLConnection) url.openConnection();                  
            connection.setRequestMethod("GET");                                     
            connection.setRequestProperty("Accept", "application/json");            
                                                                                    
            int codigo = connection.getResponseCode();                              
            if (codigo == HttpURLConnection.HTTP_OK)                                
            {                                                                       
                return new JSONArray(leerRespuesta(connection));                    
                                                                                    
            } else                                                                  
            {                                                                       
                return new JSONArray().put(new JSONObject().put("error", "Error al obtener clientes. Código: " + codigo));                                       
            }                                                                       
                                                                                    
        } catch (Exception e)                                                       
        {                                                                           
            return new JSONArray().put(new JSONObject().put("error", "Error de conexión: " + e.getMessage()));                                                   
                                                                                    
        } finally                                                                   
        {                                                                           
            if (connection != null) connection.disconnect();                        
        }                                                                           
    }                                                                               
                                                                                    
    // ==================== READ (por ID) ====================                      
    public JSONObject obtenerClientePorId(String id)                                
    {                                                                               
        HttpURLConnection connection = null;                                        
                                                                                    
        try                                                                         
        {                                                                           
            URL url = new URL(BASE_URL + "/" + id);                                 
            connection = (HttpURLConnection) url.openConnection();                  
            connection.setRequestMethod("GET");                                     
            connection.setRequestProperty("Accept", "application/json");            
                                                                                    
            int codigo = connection.getResponseCode();                              
            if (codigo == HttpURLConnection.HTTP_OK)                                
            {                                                                       
                return new JSONObject(leerRespuesta(connection));                   
                                                                                    
            } else                                                                  
            {                                                                       
                return new JSONObject().put("error", "Error al obtener cliente. Código: " + codigo);                                                             
            }                                                                       
                                                                                    
        } catch (Exception e)                                                       
        {                                                                           
            return new JSONObject().put("error", "Error de conexión: " + e.getMessage());                                                                        
                                                                                    
        } finally                                                                   
        {                                                                           
            if (connection != null) connection.disconnect();                        
        }                                                                           
    }                                                                               
                                                                                    
    // ==================== UPDATE ====================                             
    public JSONObject actualizarCliente(String id, Cliente cliente)                 
    {                                                                               
        HttpURLConnection connection = null;                                        
                                                                                    
        try                                                                         
        {                                                                           
            URL url = new URL(BASE_URL + "/" + id);                                 
            connection = (HttpURLConnection) url.openConnection();                  
            connection.setRequestMethod("PUT");                                     
            connection.setRequestProperty("Content-Type", "application/json");                                                                                   
            connection.setDoOutput(true);                                           
                                                                                    
            JSONObject json = cliente.toJson();                                     
                                                                                    
            try (OutputStream os = connection.getOutputStream())                    
            {                                                                       
                os.write(json.toString().getBytes("UTF-8"));                        
            }                                                                       
                                                                                    
            int codigo = connection.getResponseCode();                              
                                                                                    
            if (codigo == HttpURLConnection.HTTP_OK)                                
            {                                                                       
                return new JSONObject(leerRespuesta(connection));                   
                                                                                    
            } else                                                                  
            {                                                                       
                return new JSONObject().put("error", "Error al actualizar cliente. Código: " + codigo);                                                          
            }                                                                       
                                                                                    
        } catch (Exception e)                                                       
        {                                                                           
            return new JSONObject().put("error", "Error de conexión: " + e.getMessage());                                                                        
                                                                                    
        } finally                                                                   
        {                                                                           
            if (connection != null) connection.disconnect();                        
        }                                                                           
    }                                                                               
                                                                                    
    // ==================== DELETE ====================                             
    public JSONObject eliminarCliente(String id)                                    
    {                                                                               
        HttpURLConnection connection = null;                                        
                                                                                    
        try                                                                         
        {                                                                           
            URL url = new URL(BASE_URL + "/" + id);                                 
            connection = (HttpURLConnection) url.openConnection();                  
            connection.setRequestMethod("DELETE");                                  
                                                                                    
            int codigo = connection.getResponseCode();                              
            if (codigo == HttpURLConnection.HTTP_OK || codigo == HttpURLConnection.HTTP_NO_CONTENT)                                                              
            {                                                                       
                return new JSONObject().put("mensaje", "Cliente eliminado correctamente.");                                                                      
                                                                                    
            } else                                                                  
            {                                                                       
                return new JSONObject().put("error", "Error al eliminar cliente. Código: " + codigo);                                                            
            }                                                                       
                                                                                    
        } catch (Exception e)                                                       
        {                                                                           
            return new JSONObject().put("error", "Error de conexión: " + e.getMessage());                                                                        
                                                                                    
        } finally                                                                   
        {                                                                           
            if (connection != null) connection.disconnect();                        
        }                                                                           
    }                                                                               
                                                                                    
    // ==================== Métodos auxiliares ====================                 
    private String leerRespuesta(HttpURLConnection connection) throws Exception                                                                                  
    {                                                                               
        StringBuilder respuesta = new StringBuilder();                              
                                                                                    
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")))                                                
        {                                                                           
            String linea;                                                           
            while ((linea = br.readLine()) != null)                                 
            {                                                                       
                respuesta.append(linea);                                            
            }                                                                       
        }                                                                           
                                                                                    
        return respuesta.toString();                                                
    }                                                                               
} 