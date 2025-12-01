import { useQuery } from "@tanstack/react-query";
import { authStore } from "../../../shared/state/authStore";
import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

interface Paciente {
  idPaciente: number;
  nombre: string;
}

interface Cita {
  idCita: number;
  fechaHora: string;
  estado: string;
  tipoServicio: string;
  motivo: string;
  paciente: {
    id: number;
    nombre: string;
    especie: string;
  };
  veterinario: {
    nombreCompleto: string;
    especialidad: string;
  };
}

export const ClienteCitasPage = () => {
  const user = authStore((state) => state.user);

  const { data: mascotas = [] } = useQuery({
    queryKey: ["pacientes-cliente", user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const client = getApiClient();
      const { data } = await client.get<ApiResponse<Paciente[]>>(`/pacientes/cliente/${user.id}`);
      return unwrapResponse(data);
    },
    enabled: !!user?.id,
  });

  const { data: todasCitas = [], isLoading } = useQuery({
    queryKey: ["citas-cliente", mascotas.map((m) => m.idPaciente)],
    queryFn: async () => {
      if (mascotas.length === 0) return [];
      const client = getApiClient();
      const promesas = mascotas.map((mascota) =>
        client
          .get<ApiResponse<Cita[]>>(`/citas/paciente/${mascota.idPaciente}`)
          .then(({ data }) => unwrapResponse(data))
      );
      const resultados = await Promise.all(promesas);
      return resultados.flat().sort((a, b) => 
        new Date(b.fechaHora).getTime() - new Date(a.fechaHora).getTime()
      );
    },
    enabled: mascotas.length > 0,
  });

  const citasProgramadas = todasCitas.filter((c) => c.estado === "PROGRAMADA");
  const citasRealizadas = todasCitas.filter((c) => c.estado === "REALIZADA");

  return (
    <div className="p-6 space-y-6">
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">Mis Citas</h1>
        <p className="text-gray-600 mt-2">
          Historial completo de citas veterinarias
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-blue-50 rounded-lg p-6 border border-blue-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center text-white text-xl">
              📅
            </div>
            <div>
              <p className="text-sm text-blue-600 font-medium">Programadas</p>
              <p className="text-2xl font-bold text-blue-900">{citasProgramadas.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-green-50 rounded-lg p-6 border border-green-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center text-white text-xl">
              ✅
            </div>
            <div>
              <p className="text-sm text-green-600 font-medium">Realizadas</p>
              <p className="text-2xl font-bold text-green-900">{citasRealizadas.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-purple-50 rounded-lg p-6 border border-purple-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-purple-500 rounded-lg flex items-center justify-center text-white text-xl">
              📊
            </div>
            <div>
              <p className="text-sm text-purple-600 font-medium">Total</p>
              <p className="text-2xl font-bold text-purple-900">{todasCitas.length}</p>
            </div>
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-4">Cargando citas...</p>
        </div>
      ) : todasCitas.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="text-6xl mb-4">📅</div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No tienes citas registradas
          </h3>
          <p className="text-gray-500">
            Contacta a la recepción para agendar tu primera cita
          </p>
        </div>
      ) : (
        <div className="space-y-6">
          {/* Citas Programadas */}
          {citasProgramadas.length > 0 && (
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Próximas Citas
              </h2>
              <div className="space-y-3">
                {citasProgramadas.map((cita) => (
                  <div
                    key={cita.idCita}
                    className="border border-blue-200 bg-blue-50 rounded-lg p-4"
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="font-semibold text-gray-900">
                            {cita.paciente.nombre}
                          </h3>
                          <span className="px-2 py-1 text-xs rounded-full bg-blue-500 text-white">
                            {cita.estado}
                          </span>
                        </div>
                        <p className="text-sm text-gray-700 font-medium">
                          📅 {new Date(cita.fechaHora).toLocaleString("es-ES", {
                            weekday: "long",
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          })}
                        </p>
                        <p className="text-sm text-gray-600 mt-1">
                          👨‍⚕️ Dr. {cita.veterinario.nombreCompleto} •{" "}
                          {cita.tipoServicio || "Consulta General"}
                        </p>
                        {cita.motivo && (
                          <p className="text-sm text-gray-600 mt-2">
                            <span className="font-medium">Motivo:</span> {cita.motivo}
                          </p>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Historial */}
          {citasRealizadas.length > 0 && (
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h2 className="text-lg font-semibold text-gray-900 mb-4">
                Historial de Citas
              </h2>
              <div className="space-y-3">
                {citasRealizadas.map((cita) => (
                  <div
                    key={cita.idCita}
                    className="border rounded-lg p-4 hover:shadow-md transition-shadow"
                  >
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <h3 className="font-semibold text-gray-900">
                            {cita.paciente.nombre}
                          </h3>
                          <span className="px-2 py-1 text-xs rounded-full bg-green-100 text-green-700">
                            {cita.estado}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600">
                          📅 {new Date(cita.fechaHora).toLocaleDateString("es-ES", {
                            year: "numeric",
                            month: "long",
                            day: "numeric",
                          })}
                        </p>
                        <p className="text-sm text-gray-600 mt-1">
                          👨‍⚕️ Dr. {cita.veterinario.nombreCompleto} •{" "}
                          {cita.tipoServicio || "Consulta General"}
                        </p>
                        {cita.motivo && (
                          <p className="text-sm text-gray-600 mt-2">
                            <span className="font-medium">Motivo:</span> {cita.motivo}
                          </p>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
