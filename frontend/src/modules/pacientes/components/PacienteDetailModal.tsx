import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import toast from "react-hot-toast";

import { PacientesRepository } from "../services/PacientesRepository";
import { HistoriasRepository } from "../../historias/services/HistoriasRepository";
import { EditPacienteModal } from "./EditPacienteModal";
import { CreateVacunacionModal } from "../../vacunaciones/components/CreateVacunacionModal";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";
import { authStore } from "../../../shared/state/authStore";

interface PacienteDetailModalProps {
  readonly isOpen: boolean;
  readonly pacienteId: number | null;
  readonly onClose: () => void;
}

export const PacienteDetailModal = ({ isOpen, pacienteId, onClose }: PacienteDetailModalProps) => {
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isVacunacionModalOpen, setIsVacunacionModalOpen] = useState(false);
  const [isGenerandoResumen, setIsGenerandoResumen] = useState(false);
  const { user } = authStore.getState();
  const isVeterinario = user?.rol === "VETERINARIO";
  const { data: paciente, isLoading } = useQuery({
    queryKey: ["paciente", pacienteId],
    queryFn: () => PacientesRepository.getById(pacienteId!),
    enabled: isOpen && pacienteId !== null,
  });

  const { data: historia } = useQuery({
    queryKey: ["historia-clinica", pacienteId],
    queryFn: () => HistoriasRepository.getByPaciente(pacienteId!),
    enabled: isOpen && pacienteId !== null,
  });

  const handleExportarPDF = async () => {
    if (!historia) {
      toast.error("No se encontró la historia clínica del paciente");
      return;
    }
    try {
      const blob = await HistoriasRepository.exportarPDF(historia.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `historia_clinica_${paciente?.nombre || pacienteId}_${dayjs().format("YYYY-MM-DD")}.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success("Historia clínica exportada exitosamente");
    } catch (error) {
      toast.error("Error al exportar la historia clínica");
    }
  };

  const handleGenerarResumen = async () => {
    if (!pacienteId) return;
    setIsGenerandoResumen(true);
    try {
      const resumen = await PacientesRepository.generarResumen(pacienteId);
      // Mostrar resumen en un modal o alerta
      const modal = window.open("", "_blank");
      if (modal) {
        modal.document.write(`
          <!DOCTYPE html>
          <html>
            <head>
              <title>Resumen Clínico - ${paciente?.nombre || "Paciente"}</title>
              <style>
                body { font-family: Arial, sans-serif; padding: 20px; line-height: 1.6; }
                h1 { color: #1e40af; }
                pre { white-space: pre-wrap; background: #f5f5f5; padding: 15px; border-radius: 5px; }
              </style>
            </head>
            <body>
              <h1>Resumen Clínico</h1>
              <p><strong>Paciente:</strong> ${paciente?.nombre || "N/A"}</p>
              <p><strong>Fecha:</strong> ${dayjs().format("DD/MM/YYYY HH:mm")}</p>
              <hr>
              <pre>${resumen}</pre>
            </body>
          </html>
        `);
        modal.document.close();
      }
      toast.success("Resumen clínico generado exitosamente");
    } catch (error) {
      toast.error("Error al generar el resumen clínico");
    } finally {
      setIsGenerandoResumen(false);
    }
  };

  if (!isOpen || !pacienteId) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-3xl max-h-[90vh] overflow-y-auto rounded-2xl bg-white shadow-xl">
        <div className="sticky top-0 border-b border-gray-200 bg-white px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Detalle del Paciente</h2>
              <p className="mt-1 text-sm text-gray-500">Información completa del paciente</p>
            </div>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <div className="p-6">
          {isLoading ? (
            <FullscreenLoader />
          ) : paciente ? (
            <div className="space-y-6">
              {/* Información básica */}
              <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-4 text-lg font-semibold text-gray-900">Información Básica</h3>
                <div className="grid gap-4 sm:grid-cols-2">
                  <div>
                    <p className="text-xs font-medium text-gray-500">Nombre</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.nombre}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Especie</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900 capitalize">{paciente.especie}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Raza</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.raza || "No especificada"}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Sexo</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.sexo || "No especificado"}</p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Fecha de Nacimiento</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">
                      {paciente.fechaNacimiento ? dayjs(paciente.fechaNacimiento).format("DD/MM/YYYY") : "No registrada"}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Edad</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">
                      {paciente.fechaNacimiento
                        ? `${dayjs().diff(dayjs(paciente.fechaNacimiento), "year")} años`
                        : "No calculable"}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Peso</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">
                      {paciente.pesoKg ? `${paciente.pesoKg} kg` : "No registrado"}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs font-medium text-gray-500">Estado de Salud</p>
                    <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.estadoSalud || "No especificado"}</p>
                  </div>
                </div>
              </section>

              {/* Información del propietario */}
              {paciente.cliente && (
                <section className="rounded-xl border border-gray-200 bg-gray-50 p-4">
                  <h3 className="mb-4 text-lg font-semibold text-gray-900">Propietario</h3>
                  <div className="grid gap-4 sm:grid-cols-2">
                    <div>
                      <p className="text-xs font-medium text-gray-500">Nombre Completo</p>
                      <p className="mt-1 text-sm font-semibold text-gray-900">
                        {paciente.cliente.nombre} {paciente.cliente.apellido}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs font-medium text-gray-500">Correo Electrónico</p>
                      <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.cliente.correo}</p>
                    </div>
                    {paciente.cliente.telefono && (
                      <div>
                        <p className="text-xs font-medium text-gray-500">Teléfono</p>
                        <p className="mt-1 text-sm font-semibold text-gray-900">{paciente.cliente.telefono}</p>
                      </div>
                    )}
                  </div>
                </section>
              )}

              {/* Acciones */}
              <div className="flex flex-wrap gap-3">
                <button
                  onClick={handleExportarPDF}
                  disabled={!historia}
                  className="flex-1 min-w-[150px] rounded-lg border border-primary bg-white px-4 py-2 text-sm font-medium text-primary transition-all hover:bg-primary hover:text-white disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  📄 Exportar Historia PDF
                </button>
                <button
                  onClick={handleGenerarResumen}
                  disabled={isGenerandoResumen}
                  className="flex-1 min-w-[150px] rounded-lg border border-info bg-info/10 px-4 py-2 text-sm font-medium text-info transition-all hover:bg-info hover:text-white disabled:opacity-50"
                >
                  {isGenerandoResumen ? "Generando..." : "📋 Resumen Clínico"}
                </button>
                {!isVeterinario && (
                  <>
                    <button
                      onClick={() => setIsVacunacionModalOpen(true)}
                      className="flex-1 min-w-[150px] rounded-lg border border-success bg-success/10 px-4 py-2 text-sm font-medium text-success transition-all hover:bg-success hover:text-white"
                    >
                      Registrar Vacunación
                    </button>
                    <button
                      onClick={() => setIsEditModalOpen(true)}
                      className="flex-1 min-w-[150px] rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
                    >
                      Editar Información
                    </button>
                  </>
                )}
              </div>
            </div>
          ) : (
            <div className="rounded-xl border border-dashed border-gray-200 bg-gray-50 p-6 text-center text-sm text-gray-500">
              No se pudo cargar la información del paciente.
            </div>
          )}
        </div>
      </div>

      {!isVeterinario && (
        <EditPacienteModal
          isOpen={isEditModalOpen}
          pacienteId={pacienteId}
          onClose={() => setIsEditModalOpen(false)}
        />
      )}
      {!isVeterinario && (
        <CreateVacunacionModal
          isOpen={isVacunacionModalOpen}
          pacienteId={pacienteId || undefined}
          onClose={() => setIsVacunacionModalOpen(false)}
        />
      )}
    </div>
  );
};

