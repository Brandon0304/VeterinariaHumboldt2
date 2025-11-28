// Página de gestión de citas para el secretario
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import "dayjs/locale/es";
import toast from "react-hot-toast";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { CitasRepository } from "../../citas/services/CitasRepository";
import { CreateCitaModal } from "../../citas/components/CreateCitaModal";
import { CitaDetailModal } from "../../citas/components/CitaDetailModal";
import type { ApiCitaResponse } from "../../shared/types/backend";

dayjs.locale("es");

export const SecretaryCitasPage = () => {
  const [isCreateCitaModalOpen, setIsCreateCitaModalOpen] = useState(false);
  const [selectedCita, setSelectedCita] = useState<ApiCitaResponse | null>(null);
  const [filtroEstado, setFiltroEstado] = useState<string>("TODAS");

  // Obtener todas las citas
  const { data: todasLasCitas, isLoading } = useQuery({
    queryKey: ["todas-las-citas"],
    queryFn: CitasRepository.getAll,
  });

  const citasFiltradas = todasLasCitas?.filter((cita) => {
    if (filtroEstado === "TODAS") return true;
    return cita.estado === filtroEstado;
  }) || [];

  const citasOrdenadas = [...citasFiltradas].sort((a, b) => dayjs(b.fechaHora).diff(dayjs(a.fechaHora)));

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Gestión de Citas</h2>
          <p className="text-sm text-gray-500">Administra y programa las citas de la clínica</p>
        </div>
        <div className="flex gap-3">
          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          >
            <option value="TODAS">Todas las citas</option>
            <option value="PROGRAMADA">Programadas</option>
            <option value="REALIZADA">Completadas</option>
            <option value="CANCELADA">Canceladas</option>
          </select>
          <button
            onClick={() => setIsCreateCitaModalOpen(true)}
            className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
          >
            Nueva Cita
          </button>
        </div>
      </header>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        {citasOrdenadas.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            No hay citas con los filtros seleccionados.
          </div>
        ) : (
          <div className="space-y-4">
            {citasOrdenadas.map((cita) => {
              const fecha = dayjs(cita.fechaHora);
              const estadoTone =
                cita.estado === "REALIZADA"
                  ? "bg-success/20 text-success"
                  : cita.estado === "CANCELADA"
                    ? "bg-danger/20 text-danger"
                    : "bg-warning/20 text-warning";

              return (
                <div key={cita.idCita} className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-3">
                        <span className="text-lg font-semibold text-gray-900">{fecha.format("DD/MM/YYYY HH:mm")}</span>
                        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${estadoTone}`}>
                          {cita.estado === "REALIZADA" ? "Completada" : cita.estado === "CANCELADA" ? "Cancelada" : "Programada"}
                        </span>
                      </div>
                      <p className="mt-1 text-sm font-semibold text-gray-900">
                        {cita.paciente?.nombre || "Sin nombre"} ({cita.paciente?.especie || "Sin especie"})
                      </p>
                      <p className="mt-1 text-sm text-gray-600">{cita.motivo || cita.tipoServicio || "Consulta"}</p>
                      <p className="mt-1 text-xs text-gray-500">
                        Propietario: <span className="font-medium">{cita.paciente?.propietario || "No asignado"}</span>
                      </p>
                      {cita.veterinario && (
                        <p className="mt-1 text-xs text-gray-500">
                          Veterinario: <span className="font-medium">{cita.veterinario.nombreCompleto}</span>
                        </p>
                      )}
                    </div>
                    <button
                      onClick={() => setSelectedCita(cita)}
                      className="rounded-lg border border-primary bg-white px-4 py-2 text-sm font-semibold text-primary transition-all hover:bg-primary hover:text-white"
                    >
                      Ver detalle
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>

      <CreateCitaModal isOpen={isCreateCitaModalOpen} onClose={() => setIsCreateCitaModalOpen(false)} />
      <CitaDetailModal isOpen={selectedCita !== null} cita={selectedCita} onClose={() => setSelectedCita(null)} />
    </div>
  );
};

