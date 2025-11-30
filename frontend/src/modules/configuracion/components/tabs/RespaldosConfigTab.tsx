import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { respaldoService, configuracionAvanzadaService } from '../../services/configuracionService';
import { TipoRespaldo, EstadoRespaldo } from '../../types';

export default function RespaldosConfigTab() {
  return (
    <div className="space-y-8">
      <RespaldosSection />
      <ConfiguracionAvanzadaSection />
    </div>
  );
}

function RespaldosSection() {
  const queryClient = useQueryClient();
  const [tipoRespaldo, setTipoRespaldo] = useState<TipoRespaldo>(TipoRespaldo.COMPLETO);

  const { data: ultimoExitoso } = useQuery({
    queryKey: ['ultimo-respaldo'],
    queryFn: async () => {
      const response = await respaldoService.obtenerUltimoExitoso();
      return response.data;
    }
  });

  const { data: respaldosPage } = useQuery({
    queryKey: ['respaldos'],
    queryFn: async () => {
      const response = await respaldoService.buscar({}, 0, 10);
      return response.data;
    }
  });

  const crearMutation = useMutation({
    mutationFn: () => respaldoService.crear({ tipoRespaldo, observaciones: '' }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['respaldos'] });
      queryClient.invalidateQueries({ queryKey: ['ultimo-respaldo'] });
      alert('Respaldo iniciado correctamente');
    }
  });

  const respaldos = respaldosPage?.content || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Respaldos del Sistema</h2>
      </div>

      {/* Último respaldo exitoso */}
      {ultimoExitoso && (
        <div className="bg-green-50 border border-green-200 rounded-lg p-6">
          <h3 className="text-lg font-semibold text-green-900 mb-2">Último Respaldo Exitoso</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
            <div>
              <p className="text-green-700 font-medium">Tipo</p>
              <p className="text-green-900">{ultimoExitoso.tipoRespaldo}</p>
            </div>
            <div>
              <p className="text-green-700 font-medium">Fecha</p>
              <p className="text-green-900">{new Date(ultimoExitoso.fechaRespaldo).toLocaleString('es')}</p>
            </div>
            <div>
              <p className="text-green-700 font-medium">Tamaño</p>
              <p className="text-green-900">{(ultimoExitoso.tamanoBytes / 1024 / 1024).toFixed(2)} MB</p>
            </div>
            <div>
              <p className="text-green-700 font-medium">Ruta</p>
              <p className="text-green-900 truncate">{ultimoExitoso.rutaArchivo}</p>
            </div>
          </div>
        </div>
      )}

      {/* Crear nuevo respaldo */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Crear Nuevo Respaldo</h3>
        <div className="flex items-center gap-4">
          <select
            value={tipoRespaldo}
            onChange={(e) => setTipoRespaldo(e.target.value as TipoRespaldo)}
            className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value={TipoRespaldo.COMPLETO}>Completo</option>
            <option value={TipoRespaldo.INCREMENTAL}>Incremental</option>
            <option value={TipoRespaldo.DIFERENCIAL}>Diferencial</option>
            <option value={TipoRespaldo.CONFIGURACION}>Solo Configuración</option>
          </select>
          <button
            onClick={() => crearMutation.mutate()}
            disabled={crearMutation.isPending}
            className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
          >
            {crearMutation.isPending ? 'Creando...' : 'Crear Respaldo'}
          </button>
        </div>
      </div>

      {/* Lista de respaldos */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <div className="px-6 py-4 bg-gray-50 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-800">Historial de Respaldos</h3>
        </div>
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tamaño</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ruta</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {respaldos.map((respaldo) => (
              <tr key={respaldo.idRespaldo} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {new Date(respaldo.fechaRespaldo).toLocaleString('es')}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded">
                    {respaldo.tipoRespaldo}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {(respaldo.tamanoBytes / 1024 / 1024).toFixed(2)} MB
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs font-medium rounded ${
                    respaldo.estado === EstadoRespaldo.COMPLETADO ? 'bg-green-100 text-green-800' :
                    respaldo.estado === EstadoRespaldo.FALLIDO ? 'bg-red-100 text-red-800' :
                    respaldo.estado === EstadoRespaldo.EN_PROCESO ? 'bg-yellow-100 text-yellow-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {respaldo.estado}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm text-gray-600 truncate max-w-xs">
                  {respaldo.rutaArchivo}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {respaldos.length === 0 && (
          <div className="p-8 text-center text-gray-600">
            No hay respaldos registrados
          </div>
        )}
      </div>
    </div>
  );
}

function ConfiguracionAvanzadaSection() {
  const queryClient = useQueryClient();
  const [clave, setClave] = useState('');
  const [valor, setValor] = useState('');

  const { data: configs = [] } = useQuery({
    queryKey: ['config-avanzada'],
    queryFn: async () => {
      const response = await configuracionAvanzadaService.listarEditables();
      return response.data;
    }
  });

  const updateMutation = useMutation({
    mutationFn: ({ clave, valor }: { clave: string; valor: string }) =>
      configuracionAvanzadaService.actualizar(clave, valor),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['config-avanzada'] });
      setClave('');
      setValor('');
      alert('Configuración actualizada correctamente');
    }
  });

  const handleUpdate = (configClave: string, configValor: string) => {
    updateMutation.mutate({ clave: configClave, valor: configValor });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Configuración Avanzada</h2>
      </div>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-1/4">Clave</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Valor</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Tipo</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {configs.map((config) => (
              <ConfigRow
                key={config.idConfiguracion}
                config={config}
                onUpdate={handleUpdate}
              />
            ))}
          </tbody>
        </table>

        {configs.length === 0 && (
          <div className="p-8 text-center text-gray-600">
            No hay configuraciones editables
          </div>
        )}
      </div>
    </div>
  );
}

function ConfigRow({
  config,
  onUpdate
}: {
  config: any;
  onUpdate: (clave: string, valor: string) => void;
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [valor, setValor] = useState(config.valor);

  const handleSave = () => {
    onUpdate(config.clave, valor);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setValor(config.valor);
    setIsEditing(false);
  };

  return (
    <tr className="hover:bg-gray-50">
      <td className="px-6 py-4 font-mono text-sm text-gray-900">{config.clave}</td>
      <td className="px-6 py-4">
        {isEditing ? (
          <input
            type="text"
            value={valor}
            onChange={(e) => setValor(e.target.value)}
            className="w-full px-3 py-1 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
        ) : (
          <span className="text-gray-900">{config.valor}</span>
        )}
      </td>
      <td className="px-6 py-4 whitespace-nowrap">
        <span className="px-2 py-1 text-xs font-medium bg-purple-100 text-purple-800 rounded">
          {config.tipoDato}
        </span>
      </td>
      <td className="px-6 py-4 text-sm text-gray-600">
        {config.descripcion || '-'}
      </td>
      <td className="px-6 py-4 text-right text-sm font-medium">
        {config.editable && (
          isEditing ? (
            <>
              <button
                onClick={handleSave}
                className="text-green-600 hover:text-green-900 mr-3"
              >
                Guardar
              </button>
              <button
                onClick={handleCancel}
                className="text-gray-600 hover:text-gray-900"
              >
                Cancelar
              </button>
            </>
          ) : (
            <button
              onClick={() => setIsEditing(true)}
              className="text-blue-600 hover:text-blue-900"
            >
              Editar
            </button>
          )
        )}
      </td>
    </tr>
  );
}
