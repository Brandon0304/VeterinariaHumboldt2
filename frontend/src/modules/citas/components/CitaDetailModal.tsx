import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import toast from "react-hot-toast";

import { CitasRepository, type CitaReprogramarRequest, type CitaCancelarRequest } from "../services/CitasRepository";
import { CreateServicioPrestadoModal } from "../../consultas/components/CreateServicioPrestadoModal";
import { CreateFacturaModal } from "../../facturas/components/CreateFacturaModal";
import { ConsultasRepository } from "../../consultas/services/ConsultasRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";
import type { ApiCitaResponse } from "../../shared/types/backend";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { authStore } from "../../../shared/state/authStore";

interface CitaDetailModalProps {
  readonly isOpen: boolean;
  readonly cita: ApiCitaResponse | null;
  readonly onClose: () => void;
}

interface ReprogramarFormData {
  nuevaFechaHora: string;
}

interface CancelarFormData {
  motivoCancelacion: string;
}

export const CitaDetailModal = ({ isOpen, cita, onClose }: CitaDetailModalProps) => {
  const queryClient = useQueryClient();
  const [action, setAction] = useState<"view" | "reprogramar" | "cancelar">("view");
  const [isCreateServicioModalOpen, setIsCreateServicioModalOpen] = useState(false);
  const [isCreateFacturaModalOpen, setIsCreateFacturaModalOpen] = useState(false);
  const [isGenerandoResumen, setIsGenerandoResumen] = useState(false);
  const { user } = authStore.getState();
  const isVeterinario = user?.rol === "VETERINARIO";

  // Obtener servicios prestados para calcular el total de la factura
  const { data: serviciosPrestados } = useQuery({
    queryKey: ["servicios-prestados", cita?.idCita],
    queryFn: () => (cita ? ConsultasRepository.getByCita(cita.idCita) : []),
    enabled: isOpen && cita !== null && cita.estado === "REALIZADA",
  });

  // Obtener información del paciente para obtener el clienteId
  const { data: paciente } = useQuery({
    queryKey: ["paciente", cita?.paciente?.id],
    queryFn: () => (cita?.paciente?.id ? PacientesRepository.getById(cita.paciente.id) : null),
    enabled: isOpen && cita !== null && cita.paciente?.id !== null && cita.estado === "REALIZADA",
  });

  // Calcular el total de los servicios prestados
  const totalServicios = serviciosPrestados?.reduce(
    (sum, servicio) => sum + parseFloat(servicio.costoTotal),
    0
  ) || 0;

  const handleGenerarResumen = async (servicioId: number) => {
    setIsGenerandoResumen(true);
    try {
      const resumen = await ConsultasRepository.generarResumen(servicioId);
      const modal = window.open("", "_blank");
      if (modal) {
        modal.document.write(`
          <!DOCTYPE html>
          <html>
            <head>
              <title>Resumen de Servicio</title>
              <style>
                body { font-family: Arial, sans-serif; padding: 20px; line-height: 1.6; }
                h1 { color: #1e40af; }
                pre { white-space: pre-wrap; background: #f5f5f5; padding: 15px; border-radius: 5px; }
              </style>
            </head>
            <body>
              <h1>Resumen de Servicio Prestado</h1>
              <p><strong>Fecha:</strong> ${dayjs().format("DD/MM/YYYY HH:mm")}</p>
              <hr>
              <pre>${resumen}</pre>
            </body>
          </html>
        `);
        modal.document.close();
      }
      toast.success("Resumen generado exitosamente");
    } catch (error) {
      toast.error("Error al generar el resumen");
    } finally {
      setIsGenerandoResumen(false);
    }
  };

  const { register: registerReprogramar, handleSubmit: handleSubmitReprogramar, reset: resetReprogramar } =
    useForm<ReprogramarFormData>({
      defaultValues: {
        nuevaFechaHora: cita ? dayjs(cita.fechaHora).format("YYYY-MM-DDTHH:mm") : "",
      },
    });

  const { register: registerCancelar, handleSubmit: handleSubmitCancelar, reset: resetCancelar } =
    useForm<CancelarFormData>();

  const completarMutation = useMutation({
    mutationFn: (citaId: number) => CitasRepository.completar(citaId),
    onSuccess: () => {
      toast.success("Cita completada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["citas-veterinario"] });
      queryClient.invalidateQueries({ queryKey: ["veterinarian-dashboard"] });
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al completar la cita");
    },
  });

  const reprogramarMutation = useMutation({
    mutationFn: ({ citaId, request }: { citaId: number; request: CitaReprogramarRequest }) =>
      CitasRepository.reprogramar(citaId, request),
    onSuccess: () => {
      toast.success("Cita reprogramada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["citas-veterinario"] });
      queryClient.invalidateQueries({ queryKey: ["veterinarian-dashboard"] });
      resetReprogramar();
      setAction("view");
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al reprogramar la cita");
    },
  });

  const cancelarMutation = useMutation({
    mutationFn: ({ citaId, request }: { citaId: number; request: CitaCancelarRequest }) =>
      CitasRepository.cancelar(citaId, request),
    onSuccess: () => {
      toast.success("Cita cancelada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["citas-veterinario"] });
      queryClient.invalidateQueries({ queryKey: ["veterinarian-dashboard"] });
      resetCancelar();
      setAction("view");
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al cancelar la cita");
    },
  });

  const onReprogramar = (data: ReprogramarFormData) => {
    if (!cita) return;
    reprogramarMutation.mutate({
      citaId: cita.idCita,
      request: { nuevaFechaHora: data.nuevaFechaHora },
    });
  };

  const onCancelar = (data: CancelarFormData) => {
    if (!cita) return;
    cancelarMutation.mutate({
      citaId: cita.idCita,
      request: { motivo: data.motivoCancelacion },
    });
  };

  const handleCompletar = () => {
    if (!cita) return;
    if (confirm("¿Está seguro de que desea marcar esta cita como completada?")) {
      completarMutation.mutate(cita.idCita);
    }
  };

  if (!isOpen || !cita) return null;

  const fecha = dayjs(cita.fechaHora);
  const estadoTone =
    cita.estado === "REALIZADA"
      ? "bg-success/20 text-success"
      : cita.estado === "CANCELADA"
        ? "bg-danger/20 text-danger"
        : "bg-warning/20 text-warning";

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Detalle de la Cita</h2>
              <p className="mt-1 text-sm text-gray-500">Información y acciones disponibles</p>
            </div>
            <button
              onClick={() => {
                setAction("view");
                onClose();
              }}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          {action === "view" ? (
            <div className="space-y-6">
              {/* Información de la cita */}
              <div className="space-y-4">
                <div className="flex items-center justify-between rounded-xl border border-gray-200 bg-gray-50 p-4">
                  <div>
                    <p className="text-xs font-medium text-gray-500">Estado</p>
                    <span className={`mt-1 inline-block rounded-full px-3 py-1 text-xs font-semibold ${estadoTone}`}>
                      {cita.estado === "REALIZADA" ? "Completada" : cita.estado === "CANCELADA" ? "Cancelada" : "Programada"}
                    </span>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-medium text-gray-500">Fecha y Hora</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{fecha.format("DD/MM/YYYY HH:mm")}</p>
                  </div>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <p className="text-xs font-medium text-gray-500">Paciente</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">
                      {cita.paciente?.nombre || "Sin nombre"} ({cita.paciente?.especie || "Sin especie"})
                    </p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Propietario</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{cita.paciente?.propietario || "No asignado"}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Tipo de Servicio</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{cita.tipoServicio || "No especificado"}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Nivel de Prioridad</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{cita.triageNivel || "No especificado"}</p>
                  </div>
                </div>

                {cita.motivo && (
                  <div>
                    <p className="text-xs font-medium text-gray-500">Motivo</p>
                    <p className="mt-1 text-sm text-gray-900">{cita.motivo}</p>
                  </div>
                )}

                {cita.veterinario && (
                  <div>
                    <p className="text-xs font-medium text-gray-500">Veterinario Asignado</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{cita.veterinario.nombreCompleto}</p>
                    {cita.veterinario.especialidad && (
                      <p className="mt-0.5 text-xs text-gray-500">{cita.veterinario.especialidad}</p>
                    )}
                  </div>
                )}

                {/* Servicios Prestados */}
                {cita.estado === "REALIZADA" && serviciosPrestados && serviciosPrestados.length > 0 && (
                  <div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                    <h3 className="mb-3 text-sm font-semibold text-gray-900">Servicios Prestados</h3>
                    <div className="space-y-2">
                      {serviciosPrestados.map((servicio) => (
                        <div
                          key={servicio.idPrestado}
                          className="flex items-center justify-between rounded-lg border border-gray-200 bg-white p-3"
                        >
                          <div className="flex-1">
                            <p className="text-sm font-semibold text-gray-900">
                              {servicio.servicio?.nombre || "Servicio"}
                            </p>
                            <p className="text-xs text-gray-500">
                              {dayjs(servicio.fechaEjecucion).format("DD/MM/YYYY HH:mm")} •{" "}
                              {parseFloat(servicio.costoTotal).toLocaleString("es-CO", {
                                style: "currency",
                                currency: "COP",
                              })}
                            </p>
                            {servicio.observaciones && (
                              <p className="mt-1 text-xs text-gray-600">{servicio.observaciones}</p>
                            )}
                          </div>
                          <button
                            onClick={() => handleGenerarResumen(servicio.idPrestado)}
                            disabled={isGenerandoResumen}
                            className="ml-3 rounded-lg border border-info bg-info/10 px-3 py-1.5 text-xs font-semibold text-info transition-all hover:bg-info hover:text-white disabled:opacity-50"
                          >
                            {isGenerandoResumen ? "..." : "📋 Resumen"}
                          </button>
                        </div>
                      ))}
                      <div className="mt-3 rounded-lg border border-primary bg-primary/5 p-2 text-center">
                        <p className="text-xs font-medium text-gray-600">Total</p>
                        <p className="text-sm font-semibold text-primary">
                          {totalServicios.toLocaleString("es-CO", { style: "currency", currency: "COP" })}
                        </p>
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* Acciones */}
              <div className="flex flex-wrap gap-3 border-t border-gray-200 pt-4">
                {isVeterinario ? (
                  <>
                    {cita.estado === "PROGRAMADA" ? (
                      <button
                        onClick={handleCompletar}
                        disabled={completarMutation.isPending}
                        className="w-full rounded-lg bg-success px-4 py-2 text-sm font-medium text-white transition-all hover:bg-success/90 disabled:opacity-50"
                      >
                        {completarMutation.isPending ? "Marcando..." : "Marcar como atendida"}
                      </button>
                    ) : cita.estado === "REALIZADA" ? (
                      <p className="w-full text-center text-sm text-gray-500">Esta cita ya fue marcada como atendida.</p>
                    ) : (
                      <p className="w-full text-center text-sm text-gray-500">Esta cita fue cancelada.</p>
                    )}
                  </>
                ) : (
                  <>
                    {cita.estado === "PROGRAMADA" && (
                      <>
                        <button
                          onClick={() => setIsCreateServicioModalOpen(true)}
                          className="flex-1 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90"
                        >
                          Registrar Consulta
                        </button>
                        <button
                          onClick={handleCompletar}
                          disabled={completarMutation.isPending}
                          className="flex-1 rounded-lg bg-success px-4 py-2 text-sm font-medium text-white transition-all hover:bg-success/90 disabled:opacity-50"
                        >
                          {completarMutation.isPending ? "Completando..." : "Completar Cita"}
                        </button>
                        <button
                          onClick={() => setAction("reprogramar")}
                          className="flex-1 rounded-lg border border-primary bg-white px-4 py-2 text-sm font-medium text-primary transition-all hover:bg-primary hover:text-white"
                        >
                          Reprogramar
                        </button>
                        <button
                          onClick={() => setAction("cancelar")}
                          className="flex-1 rounded-lg border border-red-300 bg-white px-4 py-2 text-sm font-medium text-red-600 transition-all hover:bg-red-50"
                        >
                          Cancelar
                        </button>
                      </>
                    )}
                    {cita.estado === "REALIZADA" && (
                      <>
                        <button
                          onClick={() => setIsCreateServicioModalOpen(true)}
                          className="flex-1 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90"
                        >
                          Agregar Servicio
                        </button>
                        {cita.paciente?.id && (
                          <button
                            onClick={() => setIsCreateFacturaModalOpen(true)}
                            className="flex-1 rounded-lg bg-success px-4 py-2 text-sm font-medium text-white transition-all hover:bg-success/90"
                          >
                            Crear Factura
                          </button>
                        )}
                        <p className="w-full text-center text-sm text-gray-500">Esta cita ya fue completada</p>
                      </>
                    )}
                    {cita.estado === "CANCELADA" && (
                      <p className="w-full text-center text-sm text-gray-500">Esta cita fue cancelada</p>
                    )}
                  </>
                )}
              </div>
            </div>
          ) : action === "reprogramar" ? (
            <form onSubmit={handleSubmitReprogramar(onReprogramar)} className="space-y-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Nueva Fecha y Hora <span className="text-red-500">*</span>
                </label>
                <input
                  type="datetime-local"
                  {...registerReprogramar("nuevaFechaHora", { required: "La nueva fecha es obligatoria" })}
                  min={dayjs().format("YYYY-MM-DDTHH:mm")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                />
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setAction("view")}
                  className="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={reprogramarMutation.isPending}
                  className="flex-1 rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90 disabled:opacity-50"
                >
                  {reprogramarMutation.isPending ? "Reprogramando..." : "Confirmar Reprogramación"}
                </button>
              </div>
            </form>
          ) : (
            <form onSubmit={handleSubmitCancelar(onCancelar)} className="space-y-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Motivo de Cancelación <span className="text-red-500">*</span>
                </label>
                <textarea
                  {...registerCancelar("motivoCancelacion", { required: "El motivo es obligatorio" })}
                  rows={4}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="Describe el motivo de la cancelación..."
                />
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => setAction("view")}
                  className="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={cancelarMutation.isPending}
                  className="flex-1 rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-all hover:bg-red-700 disabled:opacity-50"
                >
                  {cancelarMutation.isPending ? "Cancelando..." : "Confirmar Cancelación"}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>

      <CreateServicioPrestadoModal
        isOpen={isCreateServicioModalOpen}
        cita={cita}
        onClose={() => setIsCreateServicioModalOpen(false)}
      />
      <CreateFacturaModal
        isOpen={isCreateFacturaModalOpen}
        clienteId={paciente?.cliente?.id}
        totalInicial={totalServicios > 0 ? totalServicios : undefined}
        contenidoInicial={
          serviciosPrestados && serviciosPrestados.length > 0
            ? {
                citaId: cita?.idCita,
                servicios: serviciosPrestados.map((s) => ({
                  nombre: s.servicio?.nombre || "Servicio",
                  costo: s.costoTotal,
                })),
              }
            : undefined
        }
        onClose={() => setIsCreateFacturaModalOpen(false)}
      />
    </div>
  );
};

