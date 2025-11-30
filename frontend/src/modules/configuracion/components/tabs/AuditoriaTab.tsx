import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { auditoriaService } from '../../services/configuracionService';
import type { AuditoriaFiltros } from '../../types';

export default function AuditoriaTab() {
  const [page, setPage] = useState(0);
  const [filtros, setFiltros] = useState<AuditoriaFiltros>({});

  const { data, isLoading } = useQuery({
    queryKey: ['auditoria', page, filtros],
    queryFn: async () => {
      const response = await auditoriaService.buscar(filtros, page, 20);
      return response.data;
    }
  });

  const { data: recientes = [] } = useQuery({
    queryKey: ['auditoria-recientes'],
    queryFn: async () => {
      const response = await auditoriaService.obtenerRecientes(10);
      return response.data;
    }
  });

  const handleSearch = () => {
    setPage(0);
  };

  if (isLoading) {
    return <div className="flex justify-center p-8"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div></div>;
  }

  const auditorias = data?.content || [];

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-gray-800">Auditoría del Sistema</h2>

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow-md p-6 space-y-4">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Filtros de Búsqueda</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="text"
            placeholder="Entidad (ej: ServicioConfiguracion)"
            value={filtros.entidad || ''}
            onChange={(e) => setFiltros({ ...filtros, entidad: e.target.value || undefined })}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="number"
            placeholder="ID Entidad"
            value={filtros.entidadId || ''}
            onChange={(e) => setFiltros({ ...filtros, entidadId: e.target.value ? parseInt(e.target.value) : undefined })}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
          <select
            value={filtros.tipoAccion || ''}
            onChange={(e) => setFiltros({ ...filtros, tipoAccion: e.target.value || undefined })}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todas las acciones</option>
            <option value="CREATE">CREATE</option>
            <option value="UPDATE">UPDATE</option>
            <option value="DELETE">DELETE</option>
            <option value="LOGIN">LOGIN</option>
            <option value="LOGOUT">LOGOUT</option>
            <option value="EXPORT">EXPORT</option>
          </select>
          <input
            type="date"
            value={filtros.fechaDesde || ''}
            onChange={(e) => setFiltros({ ...filtros, fechaDesde: e.target.value || undefined })}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="date"
            value={filtros.fechaHasta || ''}
            onChange={(e) => setFiltros({ ...filtros, fechaHasta: e.target.value || undefined })}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
          <button
            onClick={handleSearch}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
          >
            Buscar
          </button>
        </div>
      </div>

      {/* Operaciones Recientes */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Últimas 10 Operaciones</h3>
        <div className="space-y-2">
          {recientes.map((audit) => (
            <div key={audit.idAuditoria} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100">
              <div className="flex items-center gap-3">
                <span className={`px-2 py-1 text-xs font-medium rounded ${
                  audit.tipoAccion === 'CREATE' ? 'bg-green-100 text-green-800' :
                  audit.tipoAccion === 'UPDATE' ? 'bg-blue-100 text-blue-800' :
                  audit.tipoAccion === 'DELETE' ? 'bg-red-100 text-red-800' :
                  'bg-gray-100 text-gray-800'
                }`}>
                  {audit.tipoAccion}
                </span>
                <span className="text-sm font-medium text-gray-900">{audit.entidad}</span>
                {audit.entidadId && <span className="text-sm text-gray-500">#{audit.entidadId}</span>}
              </div>
              <div className="text-sm text-gray-600">
                {new Date(audit.fechaOperacion).toLocaleString('es')}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Tabla de resultados */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acción</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Entidad</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Usuario</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">IP</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {auditorias.map((audit) => (
              <tr key={audit.idAuditoria} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {new Date(audit.fechaOperacion).toLocaleString('es')}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs font-medium rounded ${
                    audit.tipoAccion === 'CREATE' ? 'bg-green-100 text-green-800' :
                    audit.tipoAccion === 'UPDATE' ? 'bg-blue-100 text-blue-800' :
                    audit.tipoAccion === 'DELETE' ? 'bg-red-100 text-red-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {audit.tipoAccion}
                  </span>
                </td>
                <td className="px-6 py-4">
                  <div className="font-medium text-gray-900">{audit.entidad}</div>
                  {audit.entidadId && <div className="text-sm text-gray-500">ID: {audit.entidadId}</div>}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {audit.nombreUsuario || `Usuario #${audit.usuarioId}`}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {audit.ipOrigen || '-'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {auditorias.length === 0 && (
          <div className="p-8 text-center text-gray-600">
            No se encontraron registros de auditoría
          </div>
        )}
      </div>

      {/* Paginación */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-center gap-2">
          <button
            onClick={() => setPage(Math.max(0, page - 1))}
            disabled={page === 0}
            className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50"
          >
            Anterior
          </button>
          <span className="px-4 py-2 text-gray-700">
            Página {page + 1} de {data.totalPages}
          </span>
          <button
            onClick={() => setPage(Math.min(data.totalPages - 1, page + 1))}
            disabled={page >= data.totalPages - 1}
            className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50"
          >
            Siguiente
          </button>
        </div>
      )}
    </div>
  );
}
