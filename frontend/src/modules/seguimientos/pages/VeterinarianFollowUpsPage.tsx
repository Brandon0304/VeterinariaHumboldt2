import { useState, useMemo } from "react";
import dayjs from "dayjs";
import "dayjs/locale/es";
import toast from "react-hot-toast";
import { useQuery } from "@tanstack/react-query";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { EditIcon, HistoriaIcon } from "../../../shared/components/icons/Icons";
import { useCitasVeterinario } from "../../citas/hooks/useCitasVeterinario";
import { VacunacionesRepository } from "../../vacunaciones/services/VacunacionesRepository";
import { CreateVacunacionModal } from "../../vacunaciones/components/CreateVacunacionModal";
import { ProgramarProximaDosisModal } from "../../vacunaciones/components/ProgramarProximaDosisModal";
import { DesparasitacionesRepository } from "../../desparasitaciones/services/DesparasitacionesRepository";
import { CreateDesparasitacionModal } from "../../desparasitaciones/components/CreateDesparasitacionModal";
import type { ApiCitaResponse, ApiVacunacionResponse, ApiDesparasitacionResponse } from "../../shared/types/backend";
import { authStore } from "../../../shared/state/authStore";

dayjs.locale("es");

export const VeterinarianFollowUpsPage = () => {
  const [isCreateVacunacionModalOpen, setIsCreateVacunacionModalOpen] = useState(false);
  const [isProgramarDosisModalOpen, setIsProgramarDosisModalOpen] = useState(false);
  const [selectedVacunacionId, setSelectedVacunacionId] = useState<number | null>(null);
  const [isCreateDesparasitacionModalOpen, setIsCreateDesparasitacionModalOpen] = useState(false);
  const { user } = authStore.getState();
  const isVeterinario = user?.rol === "VETERINARIO";

  const { data: citas, isLoading: loadingCitas } = useCitasVeterinario();
  const { data: vacunasPendientes, isLoading: loadingVacunas } = useQuery({
    queryKey: ["vacunaciones-pendientes"],
    queryFn: () => VacunacionesRepository.getPendientes(60),
  });

  const { data: desparasitacionesPendientes, isLoading: loadingDesparasitaciones } = useQuery({
    queryKey: ["desparasitaciones-pendientes"],
    queryFn: () => DesparasitacionesRepository.getPendientes(60),
  });

  const seguimientos = useMemo(() => buildSeguimientos(citas ?? []), [citas]);
  const vacunas = vacunasPendientes ?? [];

  if (loadingCitas || loadingVacunas || loadingDesparasitaciones) {
    return <FullscreenLoader />;
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Seguimientos Activos</h2>
          <p className="text-sm text-gray-500">
            Revisa tratamientos en progreso, próximas revisiones y vacunas por aplicar.
          </p>
        </div>
        {!isVeterinario && (
          <button
            className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
            onClick={() => setIsCreateVacunacionModalOpen(true)}
          >
            Nueva Vacunación
          </button>
        )}
      </header>

      <section className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {seguimientos.length === 0 ? (
          <EmptyState message="No hay tratamientos pendientes. Todas las citas programadas están al día." />
        ) : (
          seguimientos.map((seguimiento) => <SeguimientoCard key={seguimiento.id} seguimiento={seguimiento} />)
        )}
      </section>

      <section className="space-y-4 rounded-3xl bg-white p-6 shadow-soft">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <h3 className="text-lg font-semibold text-secondary">Vacunas pendientes</h3>
            <p className="text-xs text-gray-500">Listado de dosis programadas en los próximos 60 días.</p>
          </div>
          {!isVeterinario && (
            <button
              className="rounded-2xl border border-primary px-4 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
              onClick={() => setIsCreateVacunacionModalOpen(true)}
            >
              Nueva Vacunación
            </button>
          )}
        </header>
        {vacunas.length === 0 ? (
          <p className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-4 text-center text-sm text-gray-500">
            No hay vacunas pendientes en los próximos días.
          </p>
        ) : (
          <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
            {vacunas.map((vacuna) => (
              <VacunaCard
                key={vacuna.id}
                vacuna={vacuna}
                onProgramarDosis={() => {
                  setSelectedVacunacionId(vacuna.id);
                  setIsProgramarDosisModalOpen(true);
                }}
              />
            ))}
          </div>
        )}
      </section>

      <section className="space-y-4 rounded-3xl bg-white p-6 shadow-soft">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <h3 className="text-lg font-semibold text-secondary">Desparasitaciones pendientes</h3>
            <p className="text-xs text-gray-500">Listado de desparasitaciones programadas en los próximos 60 días.</p>
          </div>
          {!isVeterinario && (
            <button
              className="rounded-2xl border border-primary px-4 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
              onClick={() => setIsCreateDesparasitacionModalOpen(true)}
            >
              Nueva Desparasitación
            </button>
          )}
        </header>
        {desparasitacionesPendientes && desparasitacionesPendientes.length === 0 ? (
          <p className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-4 text-center text-sm text-gray-500">
            No hay desparasitaciones pendientes en los próximos días.
          </p>
        ) : (
          <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
            {desparasitacionesPendientes?.map((desparasitacion) => (
              <DesparasitacionCard key={desparasitacion.idDesparasitacion} desparasitacion={desparasitacion} />
            ))}
          </div>
        )}
      </section>

      {!isVeterinario && (
        <CreateVacunacionModal
          isOpen={isCreateVacunacionModalOpen}
          onClose={() => setIsCreateVacunacionModalOpen(false)}
        />
      )}
      <ProgramarProximaDosisModal
        isOpen={isProgramarDosisModalOpen}
        vacunacionId={selectedVacunacionId}
        onClose={() => {
          setIsProgramarDosisModalOpen(false);
          setSelectedVacunacionId(null);
        }}
      />
      {!isVeterinario && (
        <CreateDesparasitacionModal
          isOpen={isCreateDesparasitacionModalOpen}
          onClose={() => setIsCreateDesparasitacionModalOpen(false)}
        />
      )}
    </div>
  );
};

interface Seguimiento {
  readonly id: number;
  readonly paciente: string;
  readonly propietario: string;
  readonly fecha: dayjs.Dayjs;
  readonly motivo: string;
  readonly prioridad: "Alta" | "Media" | "Baja";
  readonly progreso: number;
  readonly tipo: string;
}

const buildSeguimientos = (citas: ApiCitaResponse[]): Seguimiento[] => {
  const now = dayjs();
  return citas
    .filter((cita) => cita.estado === "PROGRAMADA" && dayjs(cita.fechaHora).isAfter(now.subtract(1, "day")))
    .map((cita) => {
      const fecha = dayjs(cita.fechaHora);
      const diffDays = fecha.diff(now, "day");
      const prioridad = diffDays <= 1 ? "Alta" : diffDays <= 7 ? "Media" : "Baja";
      const progreso = Math.min(Math.max(100 - diffDays * 10, 10), 90);

      return {
        id: cita.idCita,
        paciente: cita.paciente?.nombre ?? "Paciente",
        propietario: cita.paciente?.propietario ?? "Sin propietario",
        fecha,
        motivo: cita.motivo ?? "Seguimiento",
        prioridad,
        progreso,
        tipo: cita.tipoServicio ?? "Consulta",
      };
    });
};

interface SeguimientoCardProps {
  readonly seguimiento: Seguimiento;
}

const SeguimientoCard = ({ seguimiento }: SeguimientoCardProps) => {
  const prioridadTone =
    seguimiento.prioridad === "Alta"
      ? "bg-danger/10 text-danger"
      : seguimiento.prioridad === "Media"
        ? "bg-warning/20 text-warning"
        : "bg-success/20 text-success";

  return (
    <article className="flex h-full flex-col justify-between rounded-3xl border border-gray-100 bg-white p-6 shadow-soft transition-base hover:border-primary/40">
      <header className="flex items-start justify-between">
        <div>
          <p className="text-lg font-semibold text-secondary">{seguimiento.paciente}</p>
          <p className="text-xs text-gray-500">{seguimiento.tipo}</p>
        </div>
        <span className={`rounded-full px-3 py-1 text-xs font-semibold ${prioridadTone}`}>{seguimiento.prioridad}</span>
      </header>
      <dl className="mt-4 grid grid-cols-2 gap-3 text-xs text-gray-500">
        <div>
          <dt className="font-semibold text-secondary">Próxima revisión</dt>
          <dd>{seguimiento.fecha.format("DD/MM/YYYY HH:mm")}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Propietario</dt>
          <dd>{seguimiento.propietario}</dd>
        </div>
        <div className="col-span-2">
          <dt className="font-semibold text-secondary">Motivo</dt>
          <dd>{seguimiento.motivo}</dd>
        </div>
      </dl>
      <div className="mt-4 space-y-2">
        <div className="h-2 rounded-full bg-gray-200">
          <div className="h-full rounded-full bg-primary" style={{ width: `${seguimiento.progreso}%` }} />
        </div>
        <p className="text-xs font-medium text-gray-500">Preparación {seguimiento.progreso}%</p>
      </div>
      <div className="mt-4 flex gap-2">
        <button
          className="flex-1 rounded-2xl border border-primary px-4 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white flex items-center justify-center gap-1.5"
          onClick={() => toast("Actualización de seguimiento en construcción")}
        >
          <EditIcon size={16} /> Actualizar
        </button>
        <button
          className="flex-1 rounded-2xl border border-gray-200 px-4 py-2 text-xs font-semibold text-secondary transition-base hover:border-primary hover:text-primary flex items-center justify-center gap-1.5"
          onClick={() => toast("Accediendo a la historia clínica...")}
        >
          <HistoriaIcon size={16} /> Ver historia
        </button>
      </div>
    </article>
  );
};

interface VacunaCardProps {
  readonly vacuna: ApiVacunacionResponse;
  readonly onProgramarDosis: () => void;
}

const VacunaCard = ({ vacuna, onProgramarDosis }: VacunaCardProps) => {
  const diasRestantes = vacuna.proximaDosis ? dayjs(vacuna.proximaDosis).diff(dayjs(), "day") : null;

  return (
    <article className="rounded-3xl border border-gray-100 bg-gray-50 p-5 shadow-sm transition-base hover:border-primary/40 hover:bg-white">
      <header className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-secondary">{vacuna.paciente.nombre}</p>
          <p className="text-xs text-gray-500">Vacuna: {vacuna.tipoVacuna}</p>
        </div>
        {diasRestantes != null ? (
          <span
            className={`rounded-full px-3 py-1 text-[11px] font-semibold ${
              diasRestantes <= 3 ? "bg-danger/10 text-danger" : diasRestantes <= 7 ? "bg-warning/20 text-warning" : "bg-primary/10 text-primary"
            }`}
          >
            En {diasRestantes} días
          </span>
        ) : (
          <span className="rounded-full bg-gray-200 px-3 py-1 text-[11px] font-semibold text-secondary">Sin fecha</span>
        )}
      </header>
      <dl className="mt-3 grid grid-cols-2 gap-3 text-xs text-gray-500">
        <div>
          <dt className="font-semibold text-secondary">Aplicada</dt>
          <dd>{dayjs(vacuna.fechaAplicacion).format("DD/MM/YYYY")}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Próxima dosis</dt>
          <dd>{vacuna.proximaDosis ? dayjs(vacuna.proximaDosis).format("DD/MM/YYYY") : "Por definir"}</dd>
        </div>
      </dl>
      {vacuna.veterinario ? (
        <p className="mt-3 text-xs text-gray-500">
          Responsable: {vacuna.veterinario.nombre} {vacuna.veterinario.apellido}
        </p>
      ) : null}
      {!vacuna.proximaDosis && (
        <button
          onClick={onProgramarDosis}
          className="mt-3 w-full rounded-xl border border-primary bg-primary/10 px-3 py-2 text-xs font-semibold text-primary transition-base hover:bg-primary hover:text-white"
        >
          Programar Próxima Dosis
        </button>
      )}
    </article>
  );
};

interface DesparasitacionCardProps {
  readonly desparasitacion: ApiDesparasitacionResponse;
}

const DesparasitacionCard = ({ desparasitacion }: DesparasitacionCardProps) => {
  const diasRestantes = desparasitacion.proximaAplicacion ? dayjs(desparasitacion.proximaAplicacion).diff(dayjs(), "day") : null;

  return (
    <article className="rounded-3xl border border-gray-100 bg-gray-50 p-5 shadow-sm transition-base hover:border-primary/40 hover:bg-white">
      <header className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-secondary">{desparasitacion.paciente.nombre}</p>
          <p className="text-xs text-gray-500">Producto: {desparasitacion.productoUsado}</p>
        </div>
        {diasRestantes != null ? (
          <span
            className={`rounded-full px-3 py-1 text-[11px] font-semibold ${
              diasRestantes <= 3 ? "bg-danger/10 text-danger" : diasRestantes <= 7 ? "bg-warning/20 text-warning" : "bg-primary/10 text-primary"
            }`}
          >
            En {diasRestantes} días
          </span>
        ) : (
          <span className="rounded-full bg-gray-200 px-3 py-1 text-[11px] font-semibold text-secondary">Sin fecha</span>
        )}
      </header>
      <dl className="mt-3 grid grid-cols-2 gap-3 text-xs text-gray-500">
        <div>
          <dt className="font-semibold text-secondary">Aplicada</dt>
          <dd>{dayjs(desparasitacion.fechaAplicacion).format("DD/MM/YYYY")}</dd>
        </div>
        <div>
          <dt className="font-semibold text-secondary">Próxima aplicación</dt>
          <dd>{desparasitacion.proximaAplicacion ? dayjs(desparasitacion.proximaAplicacion).format("DD/MM/YYYY") : "Por definir"}</dd>
        </div>
      </dl>
    </article>
  );
};

interface EmptyStateProps {
  readonly message: string;
}

const EmptyState = ({ message }: EmptyStateProps) => (
  <div className="col-span-full rounded-3xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
    {message}
  </div>
);

export default VeterinarianFollowUpsPage;

