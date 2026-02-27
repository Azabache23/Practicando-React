import { useState, useEffect } from 'react'
import ClienteForm from './components/ClienteForm'
import ClienteTable from './components/ClienteTable'
import LoginForm from './components/LoginForm'

import type { Cliente, ClienteForm as ClienteFormData } from './types/Cliente'
import type { LoginCredentials } from './types/Auth'

import
{
  getClientes,
  crearCliente,
  actualizarCliente,
  eliminarCliente,
} from './services/clienteService'

import
{
  login,
  logout,
  isAuthenticated,
  getUsername,
} from './services/authService'

const App = () =>
{
  const [autenticado, setAutenticado] = useState(isAuthenticated())
  const [loginError, setLoginError] = useState<string | null>(null)
  const [loginCargando, setLoginCargando] = useState(false)

  const [clientes, setClientes] = useState<Cliente[]>([])
  const [clienteEditar, setClienteEditar] = useState<Cliente | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [cargando, setCargando] = useState(false)

  useEffect(() =>
  {
    if (autenticado)
    {
      cargarClientes()
    }
  }, [autenticado])

  const handleLogin = async (credentials: LoginCredentials) =>
  {
    if (loginCargando) return
    setLoginCargando(true)
    try
    {
      await login(credentials)
      setAutenticado(true)
      setLoginError(null)

    } catch (err)
    {
      setLoginError((err as Error).message || 'Error al iniciar sesión')
    } finally
    {
      setLoginCargando(false)
    }
  }

  const handleLogout = () =>
  {
    logout()
    setAutenticado(false)
    setClientes([])
    setClienteEditar(null)
    setError(null)
  }

  const cargarClientes = async () =>
  {
    try
    {
      const data = await getClientes()
      setClientes(data)
      setError(null)

    } catch (_err)
    {
      setError('No se pudieron cargar los clientes. Verifica que la API esté en ejecución.')
    }
  }

  const handleGuardar = async (form: ClienteFormData) =>
  {
    if (cargando) return
    setCargando(true)
    try
    {
      if (clienteEditar)
      {
        await actualizarCliente(clienteEditar.id, { id: clienteEditar.id, ...form })
      } else
      {
        await crearCliente(form)
      }

      setClienteEditar(null)
      await cargarClientes()
      setError(null)

    } catch (err)
    {
      setError((err as Error).message || 'Error al guardar el cliente.')
    } finally
    {
      setCargando(false)
    }
  }

  const handleEliminar = async (id: string) =>
  {
    if (!confirm('¿Estás seguro de que deseas eliminar este cliente?')) return
    if (cargando) return
    setCargando(true)
    try
    {
      await eliminarCliente(id)
      await cargarClientes()
      setError(null)

    } catch (err)
    {
      setError((err as Error).message || 'Error al eliminar el cliente.')
    } finally
    {
      setCargando(false)
    }
  }

  const handleEditar = (cliente: Cliente) =>
  {
    setClienteEditar(cliente)
  }

  const handleLimpiar = () =>
  {
    setClienteEditar(null)
  }

  if (!autenticado)
  {
    return <LoginForm onLogin={handleLogin} error={loginError} cargando={loginCargando} />
  }

  return (
    <div>
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-between items-center mb-4">
          <h1 className="titulo">Gestor de Clientes</h1>
          <div className="flex items-center gap-3">
            <span className="text-white font-medium">{getUsername()}</span>
            <button
              onClick={handleLogout}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition cursor-pointer"
            >
              Cerrar Sesión
            </button>
          </div>
        </div>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        <ClienteForm
          clienteEditar={clienteEditar}
          onGuardar={handleGuardar}
          onLimpiar={handleLimpiar}
          cargando={cargando}
        />

        <ClienteTable
          clientes={clientes}
          onEditar={handleEditar}
          onEliminar={handleEliminar}
        />
      </div>
    </div>
  )
}

export default App
