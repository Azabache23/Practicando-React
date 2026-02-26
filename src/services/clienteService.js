/*  eslint-disable no-console
    global fetch
*/

const API_URL = '/api/clientes'

export async function getClientes() 
{
  try 
  {
    const res = await fetch(API_URL)
    if (!res.ok) throw new Error(`ERROR ${res.status}: Error al obtener clientes`)
    return await res.json()

  } catch (error) 
  {
    console.error("Fallo al comunicar con el servidor (GET):", error.message);
    throw error;
  }
}

export async function getClienteById(id) 
{
  try 
  {
    const res = await fetch(`${API_URL}/${id}`)
    if (!res.ok) throw new Error(`Error ${res.status}: No se pudo obtener el cliente ${id}`)
    return await res.json()

  } catch (error) 
  {
    console.error("Fallo al comunicar con el servidor:", error.message);
    throw error;
  } 
}

export async function crearCliente(cliente) 
{
  try 
  {
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente),
    })

    if (!res.ok)
    {
      const errorData = await res.json().catch(() => ({}));
      throw new Error(errorData.error || `ERROR ${res.status}: No se pudo crear el cliente`)
    }

    return await res.json();

  } catch (error) 
  {
    console.error("Fallo al comunicar con el servidor (POST):", error.message);
    throw error;
  }
}

export async function actualizarCliente(id, cliente) 
{
  try 
  {
    const res = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(cliente),
    })

    if (!res.ok)
    {
      const errorData = await res.json().catch(() => ({}));
      throw new Error(errorData.error || `ERROR ${res.status}: No se pudo actualizar el cliente`)
    }

    return await res.json();

  } catch (error) 
  {
    console.error("Fallo al comunicar con el servidor (PUT):", error.message);
    throw error;
  }
}

export async function eliminarCliente(id) 
{
  try 
  {
    const res = await fetch(`${API_URL}/${id}`, {method: 'DELETE',})

    if (!res.ok) throw new Error(`Error al eliminar cliente`)
    
  } catch (error) 
  {
    console.error("Fallo al comunicar con el servidor:", error.message);
    throw error;
  }
}