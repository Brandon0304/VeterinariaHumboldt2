import { useQuery } from "@tanstack/react-query";
import { authStore } from "../../../shared/state/authStore";
import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

interface Paciente {
  idPaciente: number;
  nombre: string;
  especie: string;
  raza: string;
  edad: number;
  peso: number;
  sexo?: string;
  colorPelaje?: string;
}

export const ClienteMascotasPage = () => {
  const user = authStore((state) => state.user);

  const { data: mascotas = [], isLoading } = useQuery({
    queryKey: ["pacientes-cliente", user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const client = getApiClient();
      const { data } = await client.get<ApiResponse<Paciente[]>>(`/pacientes/cliente/${user.id}`);
      return unwrapResponse(data);
    },
    enabled: !!user?.id,
  });

  return (
    <div className="p-6 space-y-6">
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">Mis Mascotas</h1>
        <p className="text-gray-600 mt-2">
          Información de tus mascotas registradas
        </p>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-4">Cargando mascotas...</p>
        </div>
      ) : mascotas.length === 0 ? (
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="text-6xl mb-4">🐾</div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No tienes mascotas registradas
          </h3>
          <p className="text-gray-500">
            Contacta al veterinario o administrador para registrar a tus mascotas
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {mascotas.map((mascota) => (
            <div
              key={mascota.idPaciente}
              className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start gap-4">
                <div className="w-16 h-16 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white text-2xl font-bold flex-shrink-0">
                  {mascota.nombre.charAt(0).toUpperCase()}
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-xl font-semibold text-gray-900 mb-1">
                    {mascota.nombre}
                  </h3>
                  <p className="text-sm text-gray-600 mb-3">
                    {mascota.especie} • {mascota.raza}
                  </p>
                  <div className="space-y-2">
                    <div className="flex items-center gap-2 text-sm text-gray-700">
                      <span className="font-medium">🎂 Edad:</span>
                      <span>{mascota.edad} {mascota.edad === 1 ? "año" : "años"}</span>
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-700">
                      <span className="font-medium">⚖️ Peso:</span>
                      <span>{mascota.peso} kg</span>
                    </div>
                    {mascota.sexo && (
                      <div className="flex items-center gap-2 text-sm text-gray-700">
                        <span className="font-medium">Sexo:</span>
                        <span>{mascota.sexo}</span>
                      </div>
                    )}
                    {mascota.colorPelaje && (
                      <div className="flex items-center gap-2 text-sm text-gray-700">
                        <span className="font-medium">🎨 Color:</span>
                        <span>{mascota.colorPelaje}</span>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
