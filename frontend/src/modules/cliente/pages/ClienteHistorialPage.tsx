import { useQuery } from "@tanstack/react-query";
import { authStore } from "../../../shared/state/authStore";
import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

interface Paciente {
  idPaciente: number;
  nombre: string;
}

interface HistoriaClinica {
  idHistoria: number;
  fechaConsulta: string;
  diagnostico: string;
  tratamiento: string;
  observaciones?: string;
  peso?: number;
  temperatura?: number;
  frecuenciaCardiaca?: number;
  paciente: {
    nombre: string;
  };
  veterinario: {
    nombreCompleto: string;
  };
}

export const ClienteHistorialPage = () => {
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

  const { data: historias = [], isLoading } = useQuery({
    queryKey: ["historias-cliente", mascotas.map((m) => m.idPaciente)],
    queryFn: async () => {
      if (mascotas.length === 0) return [];
      const client = getApiClient();
      const promesas = mascotas.map((mascota) =>
        client
          .get<ApiResponse<HistoriaClinica[]>>(`/historias/paciente/${mascota.idPaciente}`)
          .then(({ data }) => unwrapResponse(data))
          .catch(() => [])
      );
      const resultados = await Promise.all(promesas);
      return resultados.flat().sort((a, b) => 
        new Date(b.fechaConsulta).getTime() - new Date(a.fechaConsulta).getTime()
      );
    },
    enabled: mascotas.length > 0,
  });

  return (
    <div className="p-6 space-y-6">
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">Historial Médico</h1>
        <p className="text-gray-600 mt-2">
          Registro completo de consultas veterinarias
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-blue-50 rounded-lg p-6 border border-blue-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-blue-500 rounded-lg flex items-center justify-center text-white text-xl">
              📋
            </div>
            <div>
              <p className="text-sm text-blue-600 font-medium">Total de Consultas</p>
              <p className="text-2xl font-bold text-blue-900">{historias.length}</p>
            </div>
          </div>
        </div>
        <div className="bg-green-50 rounded-lg p-6 border border-green-100">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-green-500 rounded-lg flex items-center justify-center text-white text-xl">
              🐾
            </div>
            <div>
              <p className="text-sm text-green-600 font-medium">Mascotas Atendidas</p>
              <p className="text-2xl font-bold text-green-900">{mascotas.length}</p>
            </div>
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-4">Cargando historial médico...</p>
        </div>
      ) : historias.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="text-6xl mb-4">📋</div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No hay historial médico disponible
          </h3>
          <p className="text-gray-500">
            Las consultas realizadas aparecerán aquí
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {historias.map((historia) => (
            <div
              key={historia.idHistoria}
              className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    {historia.paciente.nombre}
                  </h3>
                  <p className="text-sm text-gray-600">
                    📅 {new Date(historia.fechaConsulta).toLocaleDateString("es-ES", {
                      weekday: "long",
                      year: "numeric",
                      month: "long",
                      day: "numeric",
                    })}
                  </p>
                  <p className="text-sm text-gray-600">
                    👨‍⚕️ Dr. {historia.veterinario.nombreCompleto}
                  </p>
                </div>
              </div>

              <div className="space-y-3">
                {/* Signos Vitales */}
                {(historia.peso || historia.temperatura || historia.frecuenciaCardiaca) && (
                  <div className="bg-gray-50 rounded-lg p-4">
                    <p className="text-sm font-semibold text-gray-700 mb-2">
                      Signos Vitales
                    </p>
                    <div className="grid grid-cols-3 gap-4">
                      {historia.peso && (
                        <div>
                          <p className="text-xs text-gray-500">Peso</p>
                          <p className="text-sm font-medium text-gray-900">
                            {historia.peso} kg
                          </p>
                        </div>
                      )}
                      {historia.temperatura && (
                        <div>
                          <p className="text-xs text-gray-500">Temperatura</p>
                          <p className="text-sm font-medium text-gray-900">
                            {historia.temperatura}°C
                          </p>
                        </div>
                      )}
                      {historia.frecuenciaCardiaca && (
                        <div>
                          <p className="text-xs text-gray-500">Frec. Cardíaca</p>
                          <p className="text-sm font-medium text-gray-900">
                            {historia.frecuenciaCardiaca} bpm
                          </p>
                        </div>
                      )}
                    </div>
                  </div>
                )}

                {/* Diagnóstico */}
                <div>
                  <p className="text-sm font-semibold text-gray-700 mb-1">Diagnóstico</p>
                  <p className="text-sm text-gray-600 bg-gray-50 rounded-lg p-3">
                    {historia.diagnostico}
                  </p>
                </div>

                {/* Tratamiento */}
                <div>
                  <p className="text-sm font-semibold text-gray-700 mb-1">Tratamiento</p>
                  <p className="text-sm text-gray-600 bg-gray-50 rounded-lg p-3">
                    {historia.tratamiento}
                  </p>
                </div>

                {/* Observaciones */}
                {historia.observaciones && (
                  <div>
                    <p className="text-sm font-semibold text-gray-700 mb-1">Observaciones</p>
                    <p className="text-sm text-gray-600 bg-gray-50 rounded-lg p-3">
                      {historia.observaciones}
                    </p>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
