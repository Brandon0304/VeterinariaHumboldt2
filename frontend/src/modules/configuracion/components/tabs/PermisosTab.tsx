import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { permisoService } from '../../services/configuracionService';
import type { PermisoRol, PermisoRolInput } from '../../types';

export default function PermisosTab() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<PermisoRolInput>({
    idRol: 0,
    modulo: '',
    accion: '',
    descripcion: '',
    activo: true
  });

  // Queries
  const { data: permisosPage, isLoading } = useQuery({
    queryKey: ['permisos'],
    queryFn: async () => {
      const response = await permisoService.listar(0, 100);
      return response.data;
    }
  });

  const { data: modulos = [] } = useQuery({
    queryKey: ['permisos-modulos'],
    queryFn: async () => {
      const response = await permisoService.obtenerModulos();
      return response.data;
    }
  });

  const { data: acciones = [] } = useQuery({
    queryKey: ['permisos-acciones'],
    queryFn: async () => {
      const response = await permisoService.obtenerAcciones();
      return response.data;
    }
  });

  // Mutations
  const createMutation = useMutation({
    mutationFn: (data: PermisoRolInput) => permisoService.crear(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['permisos'] });
      resetForm();
      alert('Permiso creado correctamente');
    },
    onError: (error: any) => {
      alert(`Error: ${error.response?.data?.message || 'No se pudo crear'}`);
    }
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<PermisoRolInput> }) =>
      permisoService.actualizar(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['permisos'] });
      resetForm();
      alert('Permiso actualizado correctamente');
    },
    onError: (error: any) => {
      alert(`Error: ${error.response?.data?.message || 'No se pudo actualizar'}`);
    }
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => permisoService.eliminar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['permisos'] });
      alert('Permiso eliminado correctamente');
    },
    onError: (error: any) => {
      alert(`Error: ${error.response?.data?.message || 'No se pudo eliminar'}`);
    }
  });

  const resetForm = () => {
    setFormData({
      idRol: 0,
      modulo: '',
      accion: '',
      descripcion: '',
      activo: true
    });
    setShowForm(false);
    setEditingId(null);
  };

  const handleEdit = (permiso: PermisoRol) => {
    setFormData({
      idRol: permiso.rol.idRol,
      modulo: permiso.modulo,
      accion: permiso.accion,
      descripcion: permiso.descripcion || '',
      activo: permiso.activo
    });
    setEditingId(permiso.idPermiso);
    setShowForm(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      updateMutation.mutate({ id: editingId, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleDelete = (id: number) => {
    if (confirm('¿Está seguro de eliminar este permiso?')) {
      deleteMutation.mutate(id);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  const permisos = permisosPage?.content || [];

  // Agrupar permisos por módulo
  const permisosPorModulo = permisos.reduce((acc, permiso) => {
    if (!acc[permiso.modulo]) {
      acc[permiso.modulo] = [];
    }
    acc[permiso.modulo].push(permiso);
    return acc;
  }, {} as Record<string, PermisoRol[]>);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Permisos y Roles (RBAC)</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
        >
          {showForm ? 'Cancelar' : '+ Nuevo Permiso'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                ID Rol *
              </label>
              <input
                type="number"
                required
                min="1"
                value={formData.idRol}
                onChange={(e) => setFormData({ ...formData, idRol: parseInt(e.target.value) })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <p className="text-xs text-gray-500 mt-1">1=ADMIN, 2=VETERINARIO, 3=SECRETARIO</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Módulo *
              </label>
              <input
                type="text"
                required
                list="modulos-list"
                value={formData.modulo}
                onChange={(e) => setFormData({ ...formData, modulo: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <datalist id="modulos-list">
                {modulos.map(m => <option key={m} value={m} />)}
              </datalist>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Acción *
              </label>
              <input
                type="text"
                required
                list="acciones-list"
                value={formData.accion}
                onChange={(e) => setFormData({ ...formData, accion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <datalist id="acciones-list">
                {acciones.map(a => <option key={a} value={a} />)}
              </datalist>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Descripción
              </label>
              <input
                type="text"
                value={formData.descripcion}
                onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="flex items-center">
              <input
                type="checkbox"
                id="permiso-activo"
                checked={formData.activo}
                onChange={(e) => setFormData({ ...formData, activo: e.target.checked })}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label htmlFor="permiso-activo" className="ml-2 block text-sm text-gray-700">
                Activo
              </label>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              disabled={createMutation.isPending || updateMutation.isPending}
              className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
            >
              {editingId ? 'Actualizar' : 'Crear'}
            </button>
            <button
              type="button"
              onClick={resetForm}
              className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition"
            >
              Cancelar
            </button>
          </div>
        </form>
      )}

      <div className="space-y-6">
        {Object.entries(permisosPorModulo).map(([modulo, permisosModulo]) => (
          <div key={modulo} className="bg-white rounded-lg shadow-md overflow-hidden">
            <div className="bg-blue-50 px-6 py-3 border-b border-blue-100">
              <h3 className="text-lg font-semibold text-blue-900">
                Módulo: {modulo}
                <span className="ml-3 text-sm font-normal text-blue-700">
                  ({permisosModulo.length} permisos)
                </span>
              </h3>
            </div>

            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rol</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acción</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Descripción</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {permisosModulo.map((permiso) => (
                    <tr key={permiso.idPermiso} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="font-medium text-gray-900">{permiso.rol.nombreRol}</span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="px-2 py-1 text-xs font-medium bg-purple-100 text-purple-800 rounded">
                          {permiso.accion}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">
                        {permiso.descripcion || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                          permiso.activo 
                            ? 'bg-green-100 text-green-800' 
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {permiso.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <button
                          onClick={() => handleEdit(permiso)}
                          className="text-blue-600 hover:text-blue-900 mr-3"
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => handleDelete(permiso.idPermiso)}
                          className="text-red-600 hover:text-red-900"
                        >
                          Eliminar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        ))}

        {Object.keys(permisosPorModulo).length === 0 && (
          <div className="bg-gray-50 border border-gray-200 rounded-lg p-8 text-center">
            <p className="text-gray-600">No hay permisos configurados</p>
          </div>
        )}
      </div>
    </div>
  );
}
