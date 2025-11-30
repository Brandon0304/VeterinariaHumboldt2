import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { horarioService } from '../../services/configuracionService';
import { DiaSemana, type HorarioAtencion, type HorarioInput } from '../../types';

const DIAS_SEMANA = [
  DiaSemana.LUNES,
  DiaSemana.MARTES,
  DiaSemana.MIERCOLES,
  DiaSemana.JUEVES,
  DiaSemana.VIERNES,
  DiaSemana.SABADO,
  DiaSemana.DOMINGO
];

export default function HorariosTab() {
  const queryClient = useQueryClient();

  const { data: horarios = [], isLoading } = useQuery({
    queryKey: ['horarios'],
    queryFn: async () => {
      const response = await horarioService.listarActivos();
      return response.data;
    }
  });

  const createMutation = useMutation({
    mutationFn: (data: HorarioInput) => horarioService.crear(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horarios'] });
      alert('Horario creado correctamente');
    }
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<HorarioInput> }) =>
      horarioService.actualizar(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horarios'] });
      alert('Horario actualizado correctamente');
    }
  });

  const cambiarEstadoMutation = useMutation({
    mutationFn: ({ dia, cerrado }: { dia: DiaSemana; cerrado: boolean }) =>
      horarioService.cambiarEstadoDia(dia, cerrado),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['horarios'] });
    }
  });

  const handleToggleCerrado = (horario: HorarioAtencion) => {
    cambiarEstadoMutation.mutate({
      dia: horario.diaSemana,
      cerrado: !horario.cerrado
    });
  };

  const handleSaveHorario = (horario: HorarioAtencion, horaApertura: string, horaCierre: string) => {
    updateMutation.mutate({
      id: horario.idHorario,
      data: { horaApertura, horaCierre, diaSemana: horario.diaSemana, cerrado: horario.cerrado, activo: horario.activo }
    });
  };

  const handleCrearDia = (dia: DiaSemana) => {
    const horaApertura = '08:00';
    const horaCierre = '18:00';
    createMutation.mutate({
      diaSemana: dia,
      horaApertura,
      horaCierre,
      cerrado: false,
      activo: true
    });
  };

  if (isLoading) {
    return <div className="flex justify-center p-8"><div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div></div>;
  }

  const horariosMap = new Map(horarios.map(h => [h.diaSemana, h]));

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold text-gray-800">Horarios de Atención</h2>
      </div>

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase w-1/4">Día</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Apertura</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Hora Cierre</th>
              <th className="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase">Estado</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Acciones</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {DIAS_SEMANA.map((dia) => {
              const horario = horariosMap.get(dia);

              if (!horario) {
                return (
                  <tr key={dia} className="hover:bg-gray-50">
                    <td className="px-6 py-4 font-medium text-gray-900">{dia}</td>
                    <td className="px-6 py-4 text-gray-400" colSpan={3}>No configurado</td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => handleCrearDia(dia)}
                        className="text-blue-600 hover:text-blue-900 text-sm font-medium"
                      >
                        + Configurar
                      </button>
                    </td>
                  </tr>
                );
              }

              return (
                <HorarioRow
                  key={horario.idHorario}
                  horario={horario}
                  onToggleCerrado={handleToggleCerrado}
                  onSave={handleSaveHorario}
                />
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function HorarioRow({
  horario,
  onToggleCerrado,
  onSave
}: {
  horario: HorarioAtencion;
  onToggleCerrado: (horario: HorarioAtencion) => void;
  onSave: (horario: HorarioAtencion, horaApertura: string, horaCierre: string) => void;
}) {
  const [isEditing, setIsEditing] = useState(false);
  const [horaApertura, setHoraApertura] = useState(horario.horaApertura);
  const [horaCierre, setHoraCierre] = useState(horario.horaCierre);

  const handleSave = () => {
    onSave(horario, horaApertura, horaCierre);
    setIsEditing(false);
  };

  const handleCancel = () => {
    setHoraApertura(horario.horaApertura);
    setHoraCierre(horario.horaCierre);
    setIsEditing(false);
  };

  return (
    <tr className="hover:bg-gray-50">
      <td className="px-6 py-4 font-medium text-gray-900">{horario.diaSemana}</td>
      <td className="px-6 py-4">
        {isEditing ? (
          <input
            type="time"
            value={horaApertura}
            onChange={(e) => setHoraApertura(e.target.value)}
            className="px-3 py-1 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
        ) : (
          <span className="text-gray-900">{horario.horaApertura}</span>
        )}
      </td>
      <td className="px-6 py-4">
        {isEditing ? (
          <input
            type="time"
            value={horaCierre}
            onChange={(e) => setHoraCierre(e.target.value)}
            className="px-3 py-1 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          />
        ) : (
          <span className="text-gray-900">{horario.horaCierre}</span>
        )}
      </td>
      <td className="px-6 py-4 text-center">
        <button
          onClick={() => onToggleCerrado(horario)}
          className={`inline-flex px-3 py-1 text-xs font-medium rounded-full transition ${
            horario.cerrado
              ? 'bg-red-100 text-red-800 hover:bg-red-200'
              : 'bg-green-100 text-green-800 hover:bg-green-200'
          }`}
        >
          {horario.cerrado ? 'Cerrado' : 'Abierto'}
        </button>
      </td>
      <td className="px-6 py-4 text-right text-sm font-medium">
        {isEditing ? (
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
        )}
      </td>
    </tr>
  );
}
