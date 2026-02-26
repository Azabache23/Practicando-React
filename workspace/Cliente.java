import org.json.JSONObject;

public class Cliente
{
    private String id;
    private String nombre;
    private String ubicacion;
    private String nif;

    public Cliente(String id, String nombre, String ubicacion, String nif) 
    {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.nif = nif;
    }

    public String getId() 
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getNombre() 
    {
        return nombre;
    }

    public void setNombre(String nombre) 
    {
        this.nombre = nombre;
    }

    public String getUbicacion() 
    {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) 
    {
        this.ubicacion = ubicacion;
    }

    public String getNif() 
    {
        return nif;
    }

    public void setNif(String nif) 
    {
        this.nif = nif;
    }

    public JSONObject toJson() 
    {
        return new JSONObject().put("id", this.id).put("nombre", this.nombre).put("ubicacion", this.ubicacion).put("nif", this.nif)
;
    }

    @Override
    public String toString() 
    {
        return "Cliente [id=" + id + ", nombre=" + nombre + ", ubicacion=" + ubicacion + ", nif=" + nif + "]";
    }
}
