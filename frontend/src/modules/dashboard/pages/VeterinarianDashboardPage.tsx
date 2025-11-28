// Página principal del veterinario que replica la vista de dashboard del prototipo.
// Genera tarjetas de resumen, lista de citas del día y accesos rápidos.
import { Fragment, useState } from "react";
import classNames from "classnames";

import { useVeterinarianDashboard } from "../hooks/useVeterinarianDashboard";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { CreateCitaModal } from "../../citas/components/CreateCitaModal";
import { CitaDetailModal } from "../../citas/components/CitaDetailModal";
import type { ApiCitaResponse } from "../../shared/types/backend";

const statusLabels: Record<string, { label: string; tone: string }> = {
  COMPLETADA: { label: "Completada", tone: "bg-success/20 text-success" },
  PENDIENTE: { label: "Pendiente", tone: "bg-warning/20 text-warning" },
  URGENTE: { label: "Urgente", tone: "bg-danger/20 text-danger" },
  EN_CURSO: { label: "En progreso", tone: "bg-primary/20 text-primary" },
  CANCELADA: { label: "Cancelada", tone: "bg-danger/20 text-danger" },
};

export const VeterinarianDashboardPage = () => {
  const { data, isLoading, isError } = useVeterinarianDashboard();
  const [isCreateCitaModalOpen, setIsCreateCitaModalOpen] = useState(false);
  const [selectedCita, setSelectedCita] = useState<ApiCitaResponse | null>(null);

  if (isLoading) {
    return <FullscreenLoader />;
  }

  if (isError || !data) {
    return (
      <div className="rounded-3xl border border-danger/40 bg-white p-10 text-center shadow-soft">
        <h2 className="text-xl font-semibold text-danger">No se pudo cargar el dashboard</h2>
        <p className="mt-2 text-sm text-gray-500">
          Verifica la conexión con el backend. Se requiere que el endpoint `/prestaciones/veterinarios/[id]/dashboard`
          esté implementado.
        </p>
      </div>
    );
  }

  const {
    resumen: { citasHoy, citasCompletadasHoy, pacientesAsignados, pendientes, proximaCita },
    citasDelDia,
    accesosRapidos,
    citasOriginales,
  } = data;

  return (
    <div className="w-full space-y-6">
      {/* Tarjetas de resumen superior */}
      <section className="grid w-full gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <SummaryCard title="Citas Hoy" value={citasHoy} subtitle={`${citasCompletadasHoy} completadas`} />
        <SummaryCard title="Pacientes" value={pacientesAsignados} subtitle="Bajo tu cuidado" />
        <SummaryCard title="Pendientes" value={pendientes} subtitle="Seguimientos activos" tone="warning" />
        <SummaryCard
          title="Próxima cita"
          value={proximaCita?.hora ?? "--:--"}
          subtitle={proximaCita?.descripcion ?? "Sin citas próximas"}
        />
      </section>

      {/* Sección de citas de hoy */}
      <section className="w-full overflow-hidden rounded-3xl border border-gray-200/80 bg-white shadow-lg">
        <div className="border-b border-gray-100 bg-gradient-to-r from-white to-gray-50/50 px-6 py-5">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
                  <span className="text-xl">📅</span>
                </div>
                <div>
                  <h2 className="text-xl font-bold text-secondary">Citas de hoy</h2>
                  <p className="mt-0.5 text-sm font-medium text-gray-500">Gestiona tus citas programadas para el día</p>
                </div>
              </div>
            </div>
            <div className="flex flex-wrap gap-2.5">
              {/* Oculto para rol VETERINARIO: creación de consultas/citas no permitida */}
              <button className="rounded-xl border-2 border-gray-200 bg-white px-4 py-2.5 text-sm font-semibold text-gray-700 shadow-sm transition-all hover:border-primary hover:bg-primary/5 hover:text-primary hover:shadow-md">
                🔍 Buscar paciente
              </button>
              <button className="rounded-xl border-2 border-gray-200 bg-white px-4 py-2.5 text-sm font-semibold text-gray-700 shadow-sm transition-all hover:border-primary hover:bg-primary/5 hover:text-primary hover:shadow-md">
                📆 Ver mi agenda
              </button>
            </div>
          </div>
        </div>

        <div className="p-6">
          {citasDelDia.length === 0 ? (
            <div className="rounded-2xl border-2 border-dashed border-gray-200 bg-gradient-to-br from-gray-50 to-white p-16 text-center">
              <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-2xl bg-gradient-to-br from-primary/10 to-primary/5 shadow-inner">
                <span className="text-4xl">📅</span>
              </div>
              <p className="text-lg font-bold text-gray-700">No hay citas programadas</p>
              <p className="mt-2 text-sm font-medium text-gray-500">No hay citas programadas para el día de hoy.</p>
            </div>
          ) : (
            <div className="space-y-3">
              {citasDelDia.map((cita) => {
                const statusTone = statusLabels[cita.estado] ?? statusLabels.PENDIENTE;
                return (
                  <article
                    key={cita.idCita}
                    className="group flex items-center gap-5 rounded-2xl border-2 border-gray-200/80 bg-white p-5 shadow-sm transition-all hover:border-primary/40 hover:shadow-lg"
                  >
                    <div className="flex min-w-[90px] flex-col items-center justify-center rounded-xl bg-gradient-to-br from-primary/10 to-primary/5 py-3 px-4 shadow-inner">
                      <span className="text-xl font-bold text-primary">{cita.hora}</span>
                      <span className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Hora</span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-4">
                        <div className="min-w-0 flex-1">
                          <p className="truncate text-base font-bold text-secondary">
                            {cita.paciente} <span className="font-normal text-gray-500">- {cita.especie}</span>
                          </p>
                          <p className="mt-1.5 text-sm font-medium text-gray-600">{cita.motivo}</p>
                          <p className="mt-1.5 text-xs font-medium text-gray-500">
                            Propietario: <span className="font-semibold text-gray-700">{cita.propietario}</span>
                          </p>
                        </div>
                        <span className={classNames("whitespace-nowrap rounded-xl px-3.5 py-2 text-xs font-bold shadow-sm", statusTone.tone)}>
                          {statusTone.label}
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => {
                        const citaOriginal = citasOriginales?.find((c) => c.idCita === cita.idCita);
                        if (citaOriginal) setSelectedCita(citaOriginal);
                      }}
                      className="rounded-xl border-2 border-primary/30 bg-white px-5 py-2.5 text-sm font-bold text-primary shadow-sm transition-all hover:border-primary hover:bg-primary hover:text-white hover:shadow-md"
                    >
                      {cita.estado === "PENDIENTE" ? "▶ Iniciar" : "👁 Ver detalle"}
                    </button>
                  </article>
                );
              })}
            </div>
          )}
        </div>
      </section>

      {/* Tarjetas de accesos rápidos */}
      <section className="grid w-full gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <ShortcutCard
          title="Seguimientos activos"
          description="Tratamientos en progreso"
          value={`${accesosRapidos.seguimientosActivos} activos`}
        />
        <ShortcutCard
          title="Vacunas pendientes"
          description="Próximas aplicaciones"
          value={`${accesosRapidos.vacunasPendientes} pendientes`}
        />
        <ShortcutCard
          title="Historias clínicas"
          description="Buscar registros médicos"
          value={`${accesosRapidos.historiasClinicas} registros`}
        />
        <ShortcutCard
          title="Pacientes nuevos"
          description="Registrados esta semana"
          value={`${accesosRapidos.pacientesNuevosSemana} nuevos`}
        />
      </section>

      <CreateCitaModal isOpen={isCreateCitaModalOpen} onClose={() => setIsCreateCitaModalOpen(false)} />
      <CitaDetailModal isOpen={selectedCita !== null} cita={selectedCita} onClose={() => setSelectedCita(null)} />
    </div>
  );
};

interface SummaryCardProps {
  readonly title: string;
  readonly value: number | string;
  readonly subtitle: string;
  readonly tone?: "default" | "warning";
}

const SummaryCard = ({ title, value, subtitle, tone = "default" }: SummaryCardProps) => {
  const toneClasses =
    tone === "warning"
      ? "bg-gradient-to-br from-amber-50 to-amber-100/50 text-amber-700 border-amber-200/60"
      : "bg-gradient-to-br from-blue-50 to-primary/5 text-blue-700 border-blue-200/60";

  const iconMap: Record<string, string> = {
    "Citas Hoy": "📅",
    "Pacientes": "👥",
    "Pendientes": "⏰",
    "Próxima cita": "🕐",
  };

  return (
    <div className="group relative overflow-hidden rounded-2xl border border-gray-200/80 bg-white p-6 shadow-sm transition-all hover:border-primary/30 hover:shadow-lg">
      <div className="absolute top-0 right-0 h-20 w-20 -translate-y-8 translate-x-8 rounded-full bg-gradient-to-br from-primary/5 to-transparent opacity-50 transition-transform group-hover:scale-150"></div>
      <div className="relative">
        <div className="mb-3 flex items-center justify-between">
          <p className="text-xs font-bold uppercase tracking-wider text-gray-500">{title}</p>
          <span className="text-xl opacity-60">{iconMap[title] || "📊"}</span>
        </div>
        <p className="mb-4 text-4xl font-bold text-secondary">{value}</p>
        <div>
          <span className={classNames("inline-flex rounded-lg border px-3 py-1.5 text-xs font-bold shadow-sm", toneClasses)}>
            {subtitle}
          </span>
        </div>
      </div>
    </div>
  );
};

interface ShortcutCardProps {
  readonly title: string;
  readonly description: string;
  readonly value: string;
}

const ShortcutCard = ({ title, description, value }: ShortcutCardProps) => {
  const iconMap: Record<string, { icon: string; gradient: string }> = {
    "Seguimientos activos": { icon: "📋", gradient: "from-purple-50 to-purple-100/30" },
    "Vacunas pendientes": { icon: "💉", gradient: "from-green-50 to-green-100/30" },
    "Historias clínicas": { icon: "📁", gradient: "from-blue-50 to-blue-100/30" },
    "Pacientes nuevos": { icon: "✨", gradient: "from-orange-50 to-orange-100/30" },
  };

  const cardConfig = iconMap[title] || { icon: "📊", gradient: "from-gray-50 to-gray-100/30" };

  return (
    <article className="group relative flex flex-col justify-between overflow-hidden rounded-2xl border border-gray-200/80 bg-white p-6 shadow-sm transition-all hover:border-primary/40 hover:shadow-lg">
      <div className={`absolute inset-0 bg-gradient-to-br ${cardConfig.gradient} opacity-0 transition-opacity group-hover:opacity-100`}></div>
      <header className="relative z-10">
        <div className="mb-3 flex items-center justify-between">
          <p className="text-xs font-bold uppercase tracking-wider text-primary">{title}</p>
          <span className="text-2xl">{cardConfig.icon}</span>
        </div>
        <p className="text-sm font-medium text-gray-600">{description}</p>
      </header>
      <div className="relative z-10 mt-4">
        <p className="text-3xl font-bold text-secondary">{value}</p>
      </div>
      <button className="relative z-10 mt-6 w-fit rounded-xl border-2 border-primary/20 bg-white px-4 py-2 text-xs font-bold text-primary transition-all hover:border-primary hover:bg-primary hover:text-white hover:shadow-md">
        Ver detalle →
      </button>
    </article>
  );
};

export default VeterinarianDashboardPage;


