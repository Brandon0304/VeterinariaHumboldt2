import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import dayjs from "dayjs";

import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { NotificacionesRepository } from "../services/NotificacionesRepository";
import type { ApiNotificacionResponse } from "../../shared/types/backend";

interface StatCardProps {
  readonly title: string;
  readonly value: string;
  readonly description?: string;
  readonly tone?: "primary" | "success" | "warning" | "danger" | "info";
}

const StatCard = ({ title, value, description, tone = "primary" }: StatCardProps) => {
  const toneClasses = {
    primary: "border-primary bg-primary/10 text-primary",
    success: "border-success bg-success/10 text-success",
    warning: "border-warning bg-warning/10 text-warning",
    danger: "border-danger bg-danger/10 text-danger",
    info: "border-info bg-info/10 text-info",
  };

  return (
    <div className={`rounded-3xl border p-6 ${toneClasses[tone]}`}>
      <h3 className="text-sm font-medium opacity-80">{title}</h3>
      <p className="mt-2 text-3xl font-bold">{value}</p>
      {description && <p className="mt-1 text-xs opacity-70">{description}</p>}
    </div>
  );
};

export const NotificacionesPage = () => {
  const [isEnviarModalOpen, setIsEnviarModalOpen] = useState(false);
  const [isProgramarModalOpen, setIsProgramarModalOpen] = useState(false);
  const [filtroEstado, setFiltroEstado] = useState<"TODAS" | "PENDIENTES" | "ENVIADAS">("TODAS");

  const { data: notificaciones, isLoading } = useQuery({
    queryKey: ["notificaciones", filtroEstado],
    queryFn: () =>
      filtroEstado === "PENDIENTES" ? NotificacionesRepository.obtenerPendientes() : NotificacionesRepository.obtenerTodas(),
  });

  const queryClient = useQueryClient();

  const enviarMutation = useMutation({
    mutationFn: NotificacionesRepository.enviar,
    onSuccess: () => {
      toast.success("Notificación enviada exitosamente");
      setIsEnviarModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ["notificaciones"] });
    },
    onError: (error: Error) => {
      toast.error(error.message || "Error al enviar la notificación");
    },
  });

  const programarMutation = useMutation({
    mutationFn: NotificacionesRepository.programar,
    onSuccess: () => {
      toast.success("Notificación programada exitosamente");
      setIsProgramarModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ["notificaciones"] });
    },
    onError: (error: Error) => {
      toast.error(error.message || "Error al programar la notificación");
    },
  });

  const stats = {
    total: notificaciones?.length || 0,
    pendientes: notificaciones?.filter((n) => n.estado === "PENDIENTE").length || 0,
    enviadas: notificaciones?.filter((n) => n.estado === "ENVIADA").length || 0,
  };

  if (isLoading) {
    return <FullscreenLoader />;
  }

  return (
    <div className="w-full space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-semibold text-secondary">Notificaciones</h2>
          <p className="text-sm text-gray-500">Gestiona y programa notificaciones del sistema</p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => setIsProgramarModalOpen(true)}
            className="rounded-2xl border border-primary bg-primary/10 px-4 py-2 text-sm font-semibold text-primary transition-base hover:bg-primary hover:text-white"
          >
            Programar
          </button>
          <button
            onClick={() => setIsEnviarModalOpen(true)}
            className="rounded-2xl bg-primary px-4 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark"
          >
            Enviar Ahora
          </button>
        </div>
      </header>

      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <StatCard title="Total" value={stats.total.toString()} description="Notificaciones" tone="primary" />
        <StatCard
          title="Pendientes"
          value={stats.pendientes.toString()}
          description="Por enviar"
          tone={stats.pendientes > 0 ? "warning" : "success"}
        />
        <StatCard title="Enviadas" value={stats.enviadas.toString()} description="Completadas" tone="success" />
      </section>

      <section className="rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex flex-wrap items-center gap-4">
          <select
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value as typeof filtroEstado)}
            className="rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
          >
            <option value="TODAS">Todas las notificaciones</option>
            <option value="PENDIENTES">Solo pendientes</option>
            <option value="ENVIADAS">Solo enviadas</option>
          </select>
        </div>

        {!notificaciones || notificaciones.length === 0 ? (
          <div className="rounded-2xl border border-dashed border-gray-200 bg-gray-50 p-10 text-center text-sm text-gray-500">
            No hay notificaciones con los filtros seleccionados.
          </div>
        ) : (
          <div className="space-y-3">
            {notificaciones.map((notificacion) => (
              <div
                key={notificacion.id}
                className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-gray-200 bg-gray-50 p-4 transition-base hover:border-primary/40 hover:bg-white"
              >
                <div className="flex-1 min-w-[200px]">
                  <div className="flex items-center gap-2">
                    <h4 className="font-semibold text-secondary">{notificacion.tipo}</h4>
                    <span
                      className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                        notificacion.estado === "ENVIADA"
                          ? "bg-success/10 text-success"
                          : notificacion.estado === "PENDIENTE"
                            ? "bg-warning/10 text-warning"
                            : "bg-gray-100 text-gray-600"
                      }`}
                    >
                      {notificacion.estado}
                    </span>
                  </div>
                  <p className="mt-1 text-sm text-gray-600">{notificacion.mensaje}</p>
                  <div className="mt-2 flex flex-wrap gap-4 text-xs text-gray-500">
                    {notificacion.fechaEnvioProgramada && (
                      <span>Programada: {dayjs(notificacion.fechaEnvioProgramada).format("DD/MM/YYYY HH:mm")}</span>
                    )}
                    {notificacion.fechaEnvioReal && (
                      <span>Enviada: {dayjs(notificacion.fechaEnvioReal).format("DD/MM/YYYY HH:mm")}</span>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {isEnviarModalOpen && (
        <EnviarNotificacionModal
          isOpen={isEnviarModalOpen}
          onClose={() => setIsEnviarModalOpen(false)}
          onEnviar={(request) => enviarMutation.mutate(request)}
          isLoading={enviarMutation.isPending}
        />
      )}

      {isProgramarModalOpen && (
        <ProgramarNotificacionModal
          isOpen={isProgramarModalOpen}
          onClose={() => setIsProgramarModalOpen(false)}
          onProgramar={(request) => programarMutation.mutate(request)}
          isLoading={programarMutation.isPending}
        />
      )}
    </div>
  );
};

interface EnviarNotificacionModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onEnviar: (request: { tipo: string; mensaje: string; canalId: number; datos?: Record<string, unknown> }) => void;
  readonly isLoading: boolean;
}

const EnviarNotificacionModal = ({ isOpen, onClose, onEnviar, isLoading }: EnviarNotificacionModalProps) => {
  const [tipo, setTipo] = useState("alerta");
  const [mensaje, setMensaje] = useState("");
  const [canalId, setCanalId] = useState(1);

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onEnviar({ tipo, mensaje, canalId });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-secondary">Enviar Notificación</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            ✕
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Tipo</label>
            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
              required
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            >
              <option value="alerta">Alerta</option>
              <option value="recordatorio">Recordatorio</option>
              <option value="info">Información</option>
            </select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Mensaje</label>
            <textarea
              value={mensaje}
              onChange={(e) => setMensaje(e.target.value)}
              required
              rows={4}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Escribe el mensaje de la notificación..."
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Canal ID</label>
            <input
              type="number"
              value={canalId}
              onChange={(e) => setCanalId(Number(e.target.value))}
              required
              min={1}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            />
          </div>
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-base hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition-base hover:bg-primary-dark disabled:opacity-50"
            >
              {isLoading ? "Enviando..." : "Enviar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

interface ProgramarNotificacionModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly onProgramar: (request: { tipo: string; mensaje: string; fechaEnvio: string; datos?: Record<string, unknown> }) => void;
  readonly isLoading: boolean;
}

const ProgramarNotificacionModal = ({ isOpen, onClose, onProgramar, isLoading }: ProgramarNotificacionModalProps) => {
  const [tipo, setTipo] = useState("recordatorio");
  const [mensaje, setMensaje] = useState("");
  const [fechaEnvio, setFechaEnvio] = useState(dayjs().add(1, "day").format("YYYY-MM-DDTHH:mm"));

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onProgramar({ tipo, mensaje, fechaEnvio });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-secondary">Programar Notificación</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            ✕
          </button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Tipo</label>
            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value)}
              required
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            >
              <option value="recordatorio">Recordatorio</option>
              <option value="alerta">Alerta</option>
              <option value="info">Información</option>
            </select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Mensaje</label>
            <textarea
              value={mensaje}
              onChange={(e) => setMensaje(e.target.value)}
              required
              rows={4}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Escribe el mensaje de la notificación..."
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-gray-700">Fecha y Hora de Envío</label>
            <input
              type="datetime-local"
              value={fechaEnvio}
              onChange={(e) => setFechaEnvio(e.target.value)}
              required
              min={dayjs().format("YYYY-MM-DDTHH:mm")}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            />
          </div>
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-gray-200 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-base hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 rounded-xl bg-primary px-4 py-2 text-sm font-medium text-white transition-base hover:bg-primary-dark disabled:opacity-50"
            >
              {isLoading ? "Programando..." : "Programar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

