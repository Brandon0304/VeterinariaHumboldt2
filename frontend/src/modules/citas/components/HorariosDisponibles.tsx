import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import { CitasRepository, type HorarioDisponibilidad } from "../services/CitasRepository";

interface HorariosDisponiblesProps {
  readonly veterinarioId: number | null;
  readonly fecha: dayjs.Dayjs;
  readonly onSelectHorario: (fechaHora: string) => void;
  readonly horarioSeleccionado?: string;
}

export const HorariosDisponibles = ({
  veterinarioId,
  fecha,
  onSelectHorario,
  horarioSeleccionado,
}: HorariosDisponiblesProps) => {
  const { data: horarios, isLoading, error } = useQuery({
    queryKey: ["horarios-disponibles", veterinarioId, fecha.format("YYYY-MM-DD")],
    queryFn: async () => {
      console.log("🔍 Obteniendo horarios para:", {
        veterinarioId,
        fecha: fecha.format("YYYY-MM-DD"),
      });
      if (!veterinarioId) {
        return [];
      }
      const resultado = await CitasRepository.obtenerHorariosDelDia(veterinarioId, fecha.format("YYYY-MM-DD"));
      console.log("📅 Horarios recibidos:", resultado);
      return resultado;
    },
    enabled: !!veterinarioId,
  });

  console.log("Estado del componente HorariosDisponibles:", {
    veterinarioId,
    fecha: fecha.format("YYYY-MM-DD"),
    isLoading,
    hasError: !!error,
    error,
    horariosCount: horarios?.length || 0,
  });

  if (!veterinarioId) {
    return (
      <div className="rounded-lg border-2 border-dashed border-gray-300 bg-gray-50 p-8 text-center">
        <svg
          className="mx-auto h-12 w-12 text-gray-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
          />
        </svg>
        <p className="mt-2 text-sm text-gray-500">Seleccione un veterinario para ver los horarios disponibles</p>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="space-y-2">
        <div className="flex items-center justify-center py-8">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
          <span className="ml-3 text-sm text-gray-600">Cargando horarios disponibles...</span>
        </div>
      </div>
    );
  }

  if (!horarios || horarios.length === 0) {
    return (
      <div className="rounded-lg border border-gray-200 bg-gray-50 p-6 text-center">
        <svg
          className="mx-auto h-10 w-10 text-gray-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M6 18L18 6M6 6l12 12"
          />
        </svg>
        <p className="mt-2 text-sm font-medium text-gray-900">No hay horarios disponibles</p>
        <p className="mt-1 text-xs text-gray-500">La clínica está cerrada este día o no hay horarios configurados</p>
      </div>
    );
  }

  const getEstadoColor = (estado: HorarioDisponibilidad["estado"], disponible: boolean) => {
    if (estado === "DISPONIBLE" && disponible) {
      return "bg-green-50 border-green-200 hover:bg-green-100 hover:border-green-300 text-green-900";
    }
    if (estado === "OCUPADO" || !disponible) {
      return "bg-red-50 border-red-200 text-red-600 cursor-not-allowed opacity-60";
    }
    if (estado === "FUERA_HORARIO") {
      return "bg-gray-50 border-gray-200 text-gray-400 cursor-not-allowed opacity-50";
    }
    if (estado === "BLOQUEADO") {
      return "bg-yellow-50 border-yellow-200 text-yellow-700 cursor-not-allowed opacity-60";
    }
    return "bg-gray-50 border-gray-200 text-gray-500";
  };

  const getEstadoIcono = (estado: HorarioDisponibilidad["estado"], disponible: boolean) => {
    if (estado === "DISPONIBLE" && disponible) {
      return (
        <svg className="h-4 w-4 text-green-600" fill="currentColor" viewBox="0 0 20 20">
          <path
            fillRule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
            clipRule="evenodd"
          />
        </svg>
      );
    }
    if (estado === "OCUPADO" || !disponible) {
      return (
        <svg className="h-4 w-4 text-red-600" fill="currentColor" viewBox="0 0 20 20">
          <path
            fillRule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
            clipRule="evenodd"
          />
        </svg>
      );
    }
    return null;
  };

  // Agrupar horarios por turno (mañana/tarde)
  const horariosMañana = horarios.filter((h) => {
    const hora = dayjs(h.fechaHora).hour();
    return hora >= 8 && hora < 12;
  });

  const horariosTarde = horarios.filter((h) => {
    const hora = dayjs(h.fechaHora).hour();
    return hora >= 14 && hora < 18;
  });

  const renderHorarios = (horariosGrupo: HorarioDisponibilidad[], titulo: string) => {
    if (horariosGrupo.length === 0) return null;

    return (
      <div>
        <h4 className="mb-3 text-xs font-semibold text-gray-500 uppercase tracking-wider">{titulo}</h4>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
          {horariosGrupo.map((horario) => {
            const fechaHoraFormato = dayjs(horario.fechaHora).format("YYYY-MM-DDTHH:mm");
            const esSeleccionado = horarioSeleccionado === fechaHoraFormato;

            return (
              <button
                key={horario.fechaHora}
                type="button"
                onClick={() => horario.disponible && onSelectHorario(fechaHoraFormato)}
                disabled={!horario.disponible}
                className={`
                  relative rounded-lg border-2 p-3 text-left transition-all duration-200
                  ${getEstadoColor(horario.estado, horario.disponible)}
                  ${
                    esSeleccionado
                      ? "ring-2 ring-primary ring-offset-2 border-primary bg-primary/10"
                      : ""
                  }
                `}
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-1">
                      {getEstadoIcono(horario.estado, horario.disponible)}
                      <p className="text-sm font-semibold">{dayjs(horario.fechaHora).format("HH:mm")}</p>
                    </div>
                    <p className="mt-1 text-xs opacity-75">
                      {horario.duracionMinutos} min
                    </p>
                  </div>
                  {esSeleccionado && (
                    <div className="absolute -top-1 -right-1 h-5 w-5 rounded-full bg-primary flex items-center justify-center">
                      <svg className="h-3 w-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                        <path
                          fillRule="evenodd"
                          d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                          clipRule="evenodd"
                        />
                      </svg>
                    </div>
                  )}
                </div>
                {!horario.disponible && horario.nombrePaciente && (
                  <p className="mt-1 text-xs truncate opacity-60">
                    {horario.nombrePaciente}
                  </p>
                )}
              </button>
            );
          })}
        </div>
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-medium text-gray-900">
          Horarios para {fecha.format("dddd, D [de] MMMM")}
        </h3>
        <div className="flex gap-3 text-xs">
          <div className="flex items-center gap-1">
            <div className="h-3 w-3 rounded-full bg-green-500" />
            <span>Disponible</span>
          </div>
          <div className="flex items-center gap-1">
            <div className="h-3 w-3 rounded-full bg-red-500" />
            <span>Ocupado</span>
          </div>
        </div>
      </div>

      <div className="space-y-6">
        {renderHorarios(horariosMañana, "🌅 Turno Mañana (8:00 - 12:00)")}
        {renderHorarios(horariosTarde, "🌆 Turno Tarde (14:00 - 18:00)")}
      </div>

      {horarios.length > 0 && (
        <div className="mt-4 rounded-lg bg-blue-50 p-3 border border-blue-200">
          <div className="flex gap-2">
            <svg className="h-5 w-5 text-blue-600 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                clipRule="evenodd"
              />
            </svg>
            <div className="text-xs text-blue-900">
              <p className="font-medium">Información importante:</p>
              <ul className="mt-1 space-y-0.5 list-disc list-inside">
                <li>Cada cita tiene una duración estimada de 30 minutos</li>
                <li>Por favor llegue 10 minutos antes de su cita</li>
                <li>Los horarios en rojo ya están ocupados</li>
              </ul>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
