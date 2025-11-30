import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { informacionClinicaService } from '../../services/configuracionService';
import type { InformacionClinica } from '../../types';

export default function InformacionClinicaTab() {
  const queryClient = useQueryClient();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<Partial<InformacionClinica>>({});

  // Query para obtener información activa
  const { data: clinica, isLoading } = useQuery({
    queryKey: ['informacion-clinica'],
    queryFn: async () => {
      const response = await informacionClinicaService.obtenerActiva();
      return response.data;
    }
  });

  // Mutation para actualizar
  const updateMutation = useMutation({
    mutationFn: (data: Partial<InformacionClinica>) => 
      informacionClinicaService.actualizar(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['informacion-clinica'] });
      setIsEditing(false);
      alert('Información actualizada correctamente');
    },
    onError: (error: any) => {
      alert(`Error: ${error.response?.data?.message || 'No se pudo actualizar'}`);
    }
  });

  const handleEdit = () => {
    setFormData(clinica || {});
    setIsEditing(true);
  };

  const handleCancel = () => {
    setFormData({});
    setIsEditing(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateMutation.mutate(formData);
  };

  const handleChange = (field: keyof InformacionClinica, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!clinica && !isEditing) {
    return (
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6">
        <p className="text-yellow-800 mb-4">No se ha configurado la información de la clínica.</p>
        <button
          onClick={handleEdit}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
        >
          Configurar Ahora
        </button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Información de la Clínica</h2>
        {!isEditing && (
          <button
            onClick={handleEdit}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition"
          >
            Editar
          </button>
        )}
      </div>

      {isEditing ? (
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nombre de la Clínica *
              </label>
              <input
                type="text"
                required
                value={formData.nombreClinica || ''}
                onChange={(e) => handleChange('nombreClinica', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                NIT
              </label>
              <input
                type="text"
                value={formData.nit || ''}
                onChange={(e) => handleChange('nit', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Dirección *
              </label>
              <input
                type="text"
                required
                value={formData.direccion || ''}
                onChange={(e) => handleChange('direccion', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Teléfono *
              </label>
              <input
                type="tel"
                required
                value={formData.telefono || ''}
                onChange={(e) => handleChange('telefono', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email *
              </label>
              <input
                type="email"
                required
                value={formData.email || ''}
                onChange={(e) => handleChange('email', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora Apertura *
              </label>
              <input
                type="time"
                required
                value={formData.horaApertura || ''}
                onChange={(e) => handleChange('horaApertura', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Hora Cierre *
              </label>
              <input
                type="time"
                required
                value={formData.horaCierre || ''}
                onChange={(e) => handleChange('horaCierre', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Días de Atención *
              </label>
              <input
                type="text"
                required
                placeholder="Ej: Lunes a Viernes"
                value={formData.diasAtencion || ''}
                onChange={(e) => handleChange('diasAtencion', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                URL del Logo
              </label>
              <input
                type="url"
                value={formData.logoUrl || ''}
                onChange={(e) => handleChange('logoUrl', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            <div className="flex items-center">
              <input
                type="checkbox"
                id="activo"
                checked={formData.activo ?? true}
                onChange={(e) => handleChange('activo', e.target.checked)}
                className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
              />
              <label htmlFor="activo" className="ml-2 block text-sm text-gray-700">
                Activo
              </label>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              disabled={updateMutation.isPending}
              className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
            >
              {updateMutation.isPending ? 'Guardando...' : 'Guardar'}
            </button>
            <button
              type="button"
              onClick={handleCancel}
              className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition"
            >
              Cancelar
            </button>
          </div>
        </form>
      ) : (
        <div className="bg-white rounded-lg shadow-md p-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <p className="text-sm text-gray-500">Nombre</p>
              <p className="text-lg font-semibold text-gray-800">{clinica?.nombreClinica}</p>
            </div>

            {clinica?.nit && (
              <div>
                <p className="text-sm text-gray-500">NIT</p>
                <p className="text-lg font-semibold text-gray-800">{clinica.nit}</p>
              </div>
            )}

            <div className="md:col-span-2">
              <p className="text-sm text-gray-500">Dirección</p>
              <p className="text-lg font-semibold text-gray-800">{clinica?.direccion}</p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Teléfono</p>
              <p className="text-lg font-semibold text-gray-800">{clinica?.telefono}</p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Email</p>
              <p className="text-lg font-semibold text-gray-800">{clinica?.email}</p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Horario</p>
              <p className="text-lg font-semibold text-gray-800">
                {clinica?.horaApertura} - {clinica?.horaCierre}
              </p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Días de Atención</p>
              <p className="text-lg font-semibold text-gray-800">{clinica?.diasAtencion}</p>
            </div>

            <div>
              <p className="text-sm text-gray-500">Estado</p>
              <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${
                clinica?.activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              }`}>
                {clinica?.activo ? 'Activo' : 'Inactivo'}
              </span>
            </div>

            {clinica?.logoUrl && (
              <div className="md:col-span-2">
                <p className="text-sm text-gray-500 mb-2">Logo</p>
                <img 
                  src={clinica.logoUrl} 
                  alt="Logo clínica" 
                  className="max-h-24 object-contain"
                />
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
