import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import toast from "react-hot-toast";
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

/**
 * Portal del Cliente - Dashboard principal
 * Permite a los clientes ver sus citas y mascotas (solo lectura)
 */
export const ClienteDashboardPage = () => {
  const user = authStore((state) => state.user);
  const [activeTab, setActiveTab] = useState<"citas" | "mascotas">("citas");

  // Obtener mascotas del cliente
  const { data: mascotas = [], isLoading: loadingMascotas } = useQuery({
    queryKey: ["pacientes-cliente", user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const client = getApiClient();
      const { data } = await client.get<ApiResponse<Paciente[]>>(`/pacientes/cliente/${user.id}`);
      return unwrapResponse(data);
    },
    enabled: !!user?.id,
  });

  // Obtener citas de todas las mascotas del cliente
  const { data: todasCitas = [], isLoading: loadingCitas } = useQuery({
    queryKey: ["citas-cliente", mascotas.map(m => m.idPaciente)],
    queryFn: async () => {
      if (mascotas.length === 0) return [];
      const client = getApiClient();
      const promesas = mascotas.map(mascota =>
        client.get<ApiResponse<Cita[]>>(`/citas/paciente/${mascota.idPaciente}`)
          .then(({ data }) => unwrapResponse(data))
      );
      const resultados = await Promise.all(promesas);
      return resultados.flat();
    },
    enabled: mascotas.length > 0,
  });

  const citasProgramadas = todasCitas.filter(c => c.estado === "PROGRAMADA");
  const proximaCita = citasProgramadas.sort((a, b) => 
    new Date(a.fechaHora).getTime() - new Date(b.fechaHora).getTime()
  )[0];

  const handleSolicitarCita = () => {
    if (mascotas.length === 0) {
      toast.error("Debes tener al menos una mascota registrada. Contacta al veterinario o administrador para registrarla.");
      return;
    }
    toast.info("Funcionalidad en desarrollo. Por favor, contacta a la recepción para agendar tu cita.");
  };

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">
          Portal del Cliente
        </h1>
        <p className="text-gray-600 mt-2">
          Bienvenido/a {user?.nombre} {user?.apellido}
        </p>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center space-x-4">
            <div className="flex-shrink-0">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center text-2xl">
                📅
              </div>
            </div>
            <div className="flex-1">
              <h3 className="text-sm font-medium text-gray-900">Próxima Cita</h3>
              {proximaCita ? (
                <>
                  <p className="text-xs text-blue-600 font-semibold mt-1">
                    {new Date(proximaCita.fechaHora).toLocaleDateString('es-ES', {
                      day: '2-digit',
                      month: 'short',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </p>
                  <p className="text-xs text-gray-500">{proximaCita.paciente.nombre}</p>
                </>
              ) : (
                <p className="text-xs text-gray-500 mt-1">No hay citas programadas</p>
              )}
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center space-x-4">
            <div className="flex-shrink-0">
              <div className="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center text-2xl">
                🐾
              </div>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-900">Mascotas Registradas</h3>
              <p className="text-xs text-gray-500 mt-1">{mascotas.length} {mascotas.length === 1 ? 'mascota' : 'mascotas'}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow-sm p-6">
          <div className="flex items-center space-x-4">
            <div className="flex-shrink-0">
              <div className="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center text-2xl">
                📋
              </div>
            </div>
            <div>
              <h3 className="text-sm font-medium text-gray-900">Citas Totales</h3>
              <p className="text-xs text-gray-500 mt-1">{todasCitas.length} {todasCitas.length === 1 ? 'cita' : 'citas'}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-sm">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8 px-6" aria-label="Tabs">
            <button
              onClick={() => setActiveTab("citas")}
              className={`
                whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                ${
                  activeTab === "citas"
                    ? "border-blue-500 text-blue-600"
                    : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                }
              `}
            >
              📅 Mis Citas
            </button>
            <button
              onClick={() => setActiveTab("mascotas")}
              className={`
                whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm
                ${
                  activeTab === "mascotas"
                    ? "border-blue-500 text-blue-600"
                    : "border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300"
                }
              `}
            >
              🐾 Mis Mascotas
            </button>
          </nav>
        </div>

        {/* Content */}
        <div className="p-6">
          {activeTab === "citas" && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold text-gray-900">Mis Citas</h2>
                <button 
                  onClick={handleSolicitarCita}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  + Solicitar Cita
                </button>
              </div>
              
              {loadingCitas ? (
                <div className="text-center py-8">
                  <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  <p className="text-gray-500 mt-2">Cargando citas...</p>
                </div>
              ) : todasCitas.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-8 text-center">
                  <p className="text-gray-500">No tienes citas programadas</p>
                  <p className="text-sm text-gray-400 mt-2">
                    Contacta a la recepción para solicitar una cita
                  </p>
                </div>
              ) : (
                <div className="space-y-3">
                  {todasCitas.map((cita) => (
                    <div key={cita.idCita} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex justify-between items-start">
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <h3 className="font-semibold text-gray-900">{cita.paciente.nombre}</h3>
                            <span className={`px-2 py-1 text-xs rounded-full ${
                              cita.estado === 'PROGRAMADA' ? 'bg-blue-100 text-blue-700' :
                              cita.estado === 'REALIZADA' ? 'bg-green-100 text-green-700' :
                              'bg-gray-100 text-gray-700'
                            }`}>
                              {cita.estado}
                            </span>
                          </div>
                          <p className="text-sm text-gray-600 mt-1">
                            {new Date(cita.fechaHora).toLocaleString('es-ES', {
                              weekday: 'long',
                              year: 'numeric',
                              month: 'long',
                              day: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit'
                            })}
                          </p>
                          <p className="text-sm text-gray-500 mt-1">
                            Dr. {cita.veterinario.nombreCompleto} • {cita.tipoServicio || 'Consulta General'}
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
              )}
            </div>
          )}

          {activeTab === "mascotas" && (
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <h2 className="text-lg font-semibold text-gray-900">Mis Mascotas</h2>
                <div className="bg-yellow-50 border border-yellow-200 rounded-lg px-3 py-2">
                  <p className="text-xs text-yellow-800">
                    ℹ️ Para registrar mascotas, contacta al veterinario o administrador
                  </p>
                </div>
              </div>
              
              {loadingMascotas ? (
                <div className="text-center py-8">
                  <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  <p className="text-gray-500 mt-2">Cargando mascotas...</p>
                </div>
              ) : mascotas.length === 0 ? (
                <div className="bg-gray-50 rounded-lg p-8 text-center">
                  <p className="text-gray-500">No tienes mascotas registradas</p>
                  <p className="text-sm text-gray-400 mt-2">
                    Contacta al veterinario o administrador para registrar a tus mascotas
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {mascotas.map((mascota) => (
                    <div key={mascota.idPaciente} className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex items-start gap-3">
                        <div className="w-12 h-12 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white text-xl font-bold">
                          {mascota.nombre.charAt(0).toUpperCase()}
                        </div>
                        <div className="flex-1">
                          <h3 className="font-semibold text-gray-900">{mascota.nombre}</h3>
                          <p className="text-sm text-gray-600">{mascota.especie} • {mascota.raza}</p>
                          <div className="flex gap-4 mt-2">
                            <span className="text-xs text-gray-500">
                              🎂 {mascota.edad} {mascota.edad === 1 ? 'año' : 'años'}
                            </span>
                            <span className="text-xs text-gray-500">
                              ⚖️ {mascota.peso} kg
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
