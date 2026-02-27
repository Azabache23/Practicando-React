import { PencilLine, Trash2 } from "lucide-react"
import type { Cliente } from '../types/Cliente'

interface Props 
{
  clientes: Cliente[]
  onEditar: (cliente: Cliente) => void
  onEliminar: (id: string) => void
}

const ClienteTable = ({ clientes, onEditar, onEliminar }: Props) =>
{
  if (clientes.length === 0)
  {
    return (
      <div className="bg-white shadow rounded-lg p-6 text-center text-gray-500">
        No hay clientes registrados.
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="tabla-clientes">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Ubicación</th>
            <th>NIF</th>
            <th className="text-center w-32">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id}>
              <td>{cliente.nombre}</td>
              <td>{cliente.ubicacion}</td>
              <td>{cliente.nif}</td>
              <td>
                <button
                  onClick={() => onEditar(cliente)}
                  className="text-blue-600 hover:text-blue-800 transition cursor-pointer"
                  title="Editar"
                >
                  <PencilLine size={18} />
                </button>
                <button
                  onClick={() => onEliminar(cliente.id)}
                  className="text-red-600 hover:text-red-800 transition cursor-pointer"
                  title="Eliminar"
                >
                  <Trash2 size={18} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default ClienteTable