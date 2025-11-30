import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { servicioConfiguracionService } from '../../services/configuracionService';
import type { ServicioConfiguracion, ServicioInput } from '../../types';

export default function ServiciosTab() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategoria, setSelectedCategoria] = useState<string>('');
  const [formData, setFormData] = useState<ServicioInput>({
    nombreServicio: '',
    descripcion: '',
    categoria: '',
    precio: 0,
    duracionEstimada: 0,
    activo: true
  });

  // Queries
  const { data: servicios = [], isLoading } = useQuery({
    queryKey: ['servicios-activos'],
    queryFn: async () => {
      const response = await servicioConfiguracionService.listarActivos();
      return response.data;
    }
  });

  const { data: categorias = [] } = useQuery({
    queryKey: ['servicios-categorias'],
    queryFn: async () => {
      const response = await servicioConfiguracionService.obtenerCategorias();
      return response.data;
    }
  });

  // Mutations
  const createMutation = useMutation({
    mutationFn: (data: ServicioInput) => servicioConfiguracionService.crear(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['servicios-activos'] });
      queryClient.invalidateQueries({ queryKey: ['servicios-categorias'] });
      resetForm();
      alert('Servicio creado correctamente');
    }
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<ServicioInput> }) =>
      servicioConfiguracionService.actualizar(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['servicios-activos'] });
      resetForm();
      alert('Servicio actualizado correctamente');
    }
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => servicioConfiguracionService.eliminar(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['servicios-activos'] });
      alert('Servicio eliminado correctamente');
    }
  });

  const resetForm = () => {
    setFormData({
      nombreServicio: '',
      descripcion: '',
      categoria: '',
      precio: 0,
      duracionEstimada: 0,
      activo: true
    });
    setShowForm(false);
    setEditingId(null);
  };

  const handleEdit = (servicio: ServicioConfiguracion) => {
    setFormData({
      nombreServicio: servicio.nombreServicio,
      descripcion: servicio.descripcion || '',
      categoria: servicio.categoria,
      precio: servicio.precio,
      duracionEstimada: servicio.duracionEstimada || 0,
      activo: servicio.activo
    });
    setEditingId(servicio.idServicio);
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

  // Filtrado
  const serviciosFiltrados = servicios.filter(s => {
    const matchSearch = s.nombreServicio.toLowerCase().includes(searchTerm.toLowerCase());
    const matchCategoria = !selectedCategoria || s.categoria === selectedCategoria;
    return matchSearch && matchCategoria;
  });

  if (isLoading) {
    return <div className="flex justify-center p-8"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div></div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Catálogo de Servicios</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          {showForm ? 'Cancelar' : '+ Nuevo Servicio'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
              <input
                type="text"
                required
                value={formData.nombreServicio}
                onChange={(e) => setFormData({ ...formData, nombreServicio: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Categoría *</label>
              <input
                type="text"
                required
                list="categorias-list"
                value={formData.categoria}
                onChange={(e) => setFormData({ ...formData, categoria: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
              <datalist id="categorias-list">
                {categorias.map(c => <option key={c} value={c} />)}
              </datalist>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Precio ($) *</label>
              <input
                type="number"
                required
                min="0"
                step="0.01"
                value={formData.precio}
                onChange={(e) => setFormData({ ...formData, precio: parseFloat(e.target.value) })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Duración (min)</label>
              <input
                type="number"
                min="0"
                value={formData.duracionEstimada}
                onChange={(e) => setFormData({ ...formData, duracionEstimada: parseInt(e.target.value) || 0 })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
              <textarea
                rows={3}
                value={formData.descripcion}
                onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="flex items-center">
              <input
                type="checkbox"
                id="servicio-activo"
                checked={formData.activo}
                onChange={(e) => setFormData({ ...formData, activo: e.target.checked })}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label htmlFor="servicio-activo" className="ml-2 block text-sm text-gray-700">
                Activo
              </label>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              disabled={createMutation.isPending || updateMutation.isPending}
              className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 disabled:bg-gray-400"
            >
              {editingId ? 'Actualizar' : 'Crear'}
            </button>
            <button type="button" onClick={resetForm} className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600">
              Cancelar
            </button>
          </div>
        </form>
      )}

      {/* Filtros */}
      <div className="bg-white rounded-lg shadow-md p-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            type="text"
            placeholder="Buscar por nombre..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
          <select
            value={selectedCategoria}
            onChange={(e) => setSelectedCategoria(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Todas las categorías</option>
            {categorias.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
        </div>
      </div>

      {/* Tabla */}
      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Servicio</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoría</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Precio</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Duración</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {serviciosFiltrados.map((servicio) => (
              <tr key={servicio.idServicio} className="hover:bg-gray-50">
                <td className="px-6 py-4">
                  <div className="font-medium text-gray-900">{servicio.nombreServicio}</div>
                  <div className="text-sm text-gray-500">{servicio.descripcion}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className="px-2 py-1 text-xs font-medium bg-blue-100 text-blue-800 rounded">
                    {servicio.categoria}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap font-semibold text-green-600">
                  ${servicio.precio.toFixed(2)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                  {servicio.duracionEstimada ? `${servicio.duracionEstimada} min` : '-'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full ${
                    servicio.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {servicio.activo ? 'Activo' : 'Inactivo'}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button
                    onClick={() => handleEdit(servicio)}
                    className="text-blue-600 hover:text-blue-900 mr-3"
                  >
                    Editar
                  </button>
                  <button
                    onClick={() => confirm('¿Eliminar servicio?') && deleteMutation.mutate(servicio.idServicio)}
                    className="text-red-600 hover:text-red-900"
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {serviciosFiltrados.length === 0 && (
          <div className="p-8 text-center text-gray-600">
            No se encontraron servicios
          </div>
        )}
      </div>
    </div>
  );
}
