import type { Cliente, ClienteForm } from '../types/Cliente'
import { getToken, logout } from './authService'

const API_URL = '/api/clientes'

const getAuthHeaders = (): Record<string, string> =>
{
  const token = getToken()
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (token)
  {
    headers['Authorization'] = `Bearer ${token}`
  }
  return headers
}

const handleUnauthorized = (res: Response) =>
{
  if (res.status === 401)
  {
    logout()
    window.location.reload()
  }
}

export const getClientes = async (): Promise<Cliente[]> =>
{
  try
  {
    const res = await fetch(API_URL, { headers: getAuthHeaders() })
    handleUnauthorized(res)
    if (!res.ok) throw new Error(`ERROR ${res.status}: Error al obtener clientes`)
    return await res.json()

  } catch (error)
  {
    console.error("Fallo al comunicar con el servidor (GET):", (error as Error).message);
    throw error;
  }
}

export const getClienteById = async (id: string): Promise<Cliente> =>
{
  try
  {
    const res = await fetch(`${API_URL}/${id}`, { headers: getAuthHeaders() })
    handleUnauthorized(res)
    if (!res.ok) throw new Error(`Error ${res.status}: No se pudo obtener el cliente ${id}`)
    return await res.json()

  } catch (error)
  {
    console.error("Fallo al comunicar con el servidor:", (error as Error).message);
    throw error;
  }
}

export const crearCliente = async (cliente: ClienteForm): Promise<Cliente> =>
{
  try
  {
    const res = await fetch(API_URL, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(cliente),
    })

    handleUnauthorized(res)

    if (!res.ok)
    {
      const errorData = await res.json().catch(() => ({}));
      throw new Error(errorData.error || `ERROR ${res.status}: No se pudo crear el cliente`)
    }

    return await res.json();

  } catch (error)
  {
    console.error("Fallo al comunicar con el servidor (POST):", (error as Error).message);
    throw error;
  }
}

export const actualizarCliente = async (id: string, cliente: Cliente): Promise<Cliente> =>
{
  try
  {
    const res = await fetch(`${API_URL}/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(cliente),
    })

    handleUnauthorized(res)

    if (!res.ok)
    {
      const errorData = await res.json().catch(() => ({}));
      throw new Error(errorData.error || `ERROR ${res.status}: No se pudo actualizar el cliente`)
    }

    return await res.json();

  } catch (error)
  {
    console.error("Fallo al comunicar con el servidor (PUT):", (error as Error).message);
    throw error;
  }
}

export const eliminarCliente = async (id: string): Promise<void> =>
{
  try
  {
    const res = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    })

    handleUnauthorized(res)

    if (!res.ok) throw new Error(`ERROR ${res.status}: no se pudo eliminar el cliente `)

  } catch (error)
  {
    console.error("Fallo al comunicar con el servidor:", (error as Error).message);
    throw error;
  }
}
