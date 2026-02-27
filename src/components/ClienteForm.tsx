import { useState, useEffect } from 'react'
import type { Cliente, ClienteForm as ClienteFormData } from '../types/Cliente'

interface Props {
  clienteEditar: Cliente | null
  onGuardar: (form: ClienteFormData) => void
  onLimpiar: () => void
  cargando: boolean
}

const ClienteForm = ({ clienteEditar, onGuardar, onLimpiar, cargando }: Props) =>
{
  const [form, setForm] = useState<ClienteFormData>({ nombre: '', ubicacion: '', nif: '' })
  const editando = clienteEditar !== null

  useEffect(() => {
    if (clienteEditar)
    {
      setForm({
        nombre: clienteEditar.nombre || '',
        ubicacion: clienteEditar.ubicacion || '',
        nif: clienteEditar.nif || '',
      })
    } else
    {
      setForm({ nombre: '', ubicacion: '', nif: '' })
    }
  }, [clienteEditar])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) =>
  {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) =>
  {
    e.preventDefault()
    onGuardar(form)
  }

  const handleLimpiar = () =>
  {
    setForm({ nombre: '', ubicacion: '', nif: '' })
    onLimpiar()
  }

  return (
    <form onSubmit={handleSubmit} className="bg-white shadow rounded-lg p-6 mb-6">
      <h2 className="text-xl font-semibold mb-4">
        {editando ? `Editando cliente #${clienteEditar.id}` : 'Nuevo cliente'}
      </h2>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
          <input
            type="text"
            name="nombre"
            value={form.nombre}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">Ubicación</label>
          <input
            type="text"
            name="ubicacion"
            value={form.ubicacion}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">NIF</label>
          <input
            type="text"
            name="nif"
            value={form.nif}
            onChange={handleChange}
            required
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      <div className="flex gap-3">
        <button
          type="submit"
          disabled={cargando}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {cargando ? 'Guardando...' : editando ? 'Actualizar' : 'Guardar'}
        </button>
        <button
          type="button"
          onClick={handleLimpiar}
          className="bg-gray-300 text-gray-700 px-4 py-2 rounded hover:bg-gray-400 transition cursor-pointer"
        >
          Limpiar
        </button>
      </div>
    </form>
  )
}

export default ClienteForm
