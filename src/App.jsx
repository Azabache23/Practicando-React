import { useState, useEffect } from 'react'
import ClienteForm from './components/ClienteForm'
import ClienteTable from './components/ClienteTable'


import 
{
  getClientes,
  crearCliente,
  actualizarCliente,
  eliminarCliente,
} from './services/clienteService'

export default function App() 
{
  const [clientes, setClientes] = useState([])
  const [clienteEditar, setClienteEditar] = useState(null)
  const [error, setError] = useState(null)
  const [cargando, setCargando] = useState(false)

  useEffect(() => {cargarClientes()}, [])

  async function cargarClientes() 
  {
    try 
    {
      const data = await getClientes()
      setClientes(data)
      setError(null)

    } catch (err) 
    {
      setError('No se pudieron cargar los clientes. Verifica que la API esté en ejecución.')
    }
  }

  async function handleGuardar(form)
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
      setError(err.message || 'Error al guardar el cliente.')
    } finally
    {
      setCargando(false)
    }
  }

  async function handleEliminar(id)
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
      setError(err.message || 'Error al eliminar el cliente.')
    } finally
    {
      setCargando(false)
    }
  }

  function handleEditar(cliente) 
  {
    setClienteEditar(cliente)
  }

  function handleLimpiar() 
  {
    setClienteEditar(null)
  }

  return (
    <div className="min-h-screen bg-gray-100 py-8 px-4">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-center mb-8">Gestor de Clientes</h1>

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
