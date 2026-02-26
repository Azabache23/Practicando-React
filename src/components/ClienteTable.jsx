export default function ClienteTable({ clientes, onEditar, onEliminar }) 
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
    <div className="bg-white shadow rounded-lg overflow-hidden">
      <table className="w-full text-left">
        <thead className="bg-gray-100">
          <tr>
            <th className="px-4 py-3 text-sm font-semibold text-gray-600">ID</th>
            <th className="px-4 py-3 text-sm font-semibold text-gray-600">Nombre</th>
            <th className="px-4 py-3 text-sm font-semibold text-gray-600">Ubicación</th>
            <th className="px-4 py-3 text-sm font-semibold text-gray-600">NIF</th>
            <th className="px-4 py-3 text-sm font-semibold text-gray-600">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((c) => (
            <tr key={c.id} className="border-t border-gray-200 hover:bg-gray-50">
              <td className="px-4 py-3">{c.id}</td>
              <td className="px-4 py-3">{c.nombre}</td>
              <td className="px-4 py-3">{c.ubicacion}</td>
              <td className="px-4 py-3">{c.nif}</td>
              <td className="px-4 py-3 flex gap-2">
                <button
                  onClick={() => onEditar(c)}
                  className="bg-amber-500 text-white px-3 py-1 rounded text-sm hover:bg-amber-600 transition cursor-pointer"
                >
                  Editar
                </button>
                <button
                  onClick={() => onEliminar(c.id)}
                  className="bg-red-500 text-white px-3 py-1 rounded text-sm hover:bg-red-600 transition cursor-pointer"
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
