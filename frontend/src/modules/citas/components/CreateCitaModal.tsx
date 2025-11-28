import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import toast from "react-hot-toast";

import { CitasRepository, type CitaRequest } from "../services/CitasRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";
import { VeterinariosRepository } from "../../usuarios/services/VeterinariosRepository";
import { authStore } from "../../../shared/state/authStore";

interface CreateCitaModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly initialDate?: dayjs.Dayjs;
}

interface FormData {
  pacienteId: string;
  veterinarioId?: string; // Solo para secretarios
  fechaHora: string;
  tipoServicio: string;
  motivo: string;
  triageNivel: string;
}

export const CreateCitaModal = ({ isOpen, onClose, initialDate }: CreateCitaModalProps) => {
  const queryClient = useQueryClient();
  const user = authStore((state) => state.user);
  const isSecretario = user?.rol?.toUpperCase() === "SECRETARIO";
  
  const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>({
    defaultValues: {
      fechaHora: initialDate ? initialDate.format("YYYY-MM-DDTHH:mm") : dayjs().add(1, "day").format("YYYY-MM-DDTHH:mm"),
      triageNivel: "MEDIA",
    },
  });

  const { data: pacientes } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
    enabled: isOpen,
  });

  const { data: veterinarios } = useQuery({
    queryKey: ["veterinarios"],
    queryFn: VeterinariosRepository.getAll,
    enabled: isOpen && isSecretario,
  });

  const mutation = useMutation({
    mutationFn: (data: CitaRequest) => CitasRepository.create(data),
    onSuccess: () => {
      toast.success("Cita agendada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["citas-veterinario"] });
      queryClient.invalidateQueries({ queryKey: ["todas-las-citas"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al agendar la cita");
    },
  });

  const onSubmit = async (data: FormData) => {
    if (!user) {
      toast.error("No se pudo obtener la información del usuario");
      return;
    }

    // Determinar el ID del veterinario según el rol
    let veterinarioId: number;
    if (user.rol?.toUpperCase() === "SECRETARIO") {
      if (!data.veterinarioId) {
        toast.error("Debe seleccionar un veterinario");
        return;
      }
      veterinarioId = parseInt(data.veterinarioId);
    } else {
      // Si es veterinario, usar su propio ID
      veterinarioId = user.id;
    }

    // Validar disponibilidad antes de crear la cita
    try {
      // El input datetime-local devuelve formato YYYY-MM-DDTHH:mm, que es compatible con ISO LocalDateTime
      const disponible = await CitasRepository.verificarDisponibilidad(veterinarioId, data.fechaHora);
      
      if (!disponible) {
        toast.error("El veterinario ya tiene una cita programada en ese horario. Por favor, seleccione otra fecha y hora.");
        return;
      }
    } catch (error: any) {
      // Si hay error al verificar disponibilidad, mostrar mensaje pero permitir intentar crear
      console.warn("Error al verificar disponibilidad:", error);
      toast.error("No se pudo verificar la disponibilidad. Se intentará crear la cita de todas formas.");
    }

    const request: CitaRequest = {
      pacienteId: parseInt(data.pacienteId),
      veterinarioId,
      fechaHora: data.fechaHora,
      tipoServicio: data.tipoServicio || undefined,
      motivo: data.motivo || undefined,
      triageNivel: data.triageNivel || undefined,
    };
    mutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-semibold text-gray-900">Agendar Nueva Cita</h2>
          <p className="mt-1 text-sm text-gray-500">Completa la información de la cita</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <div className="space-y-4">
            {isSecretario && (
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Veterinario <span className="text-red-500">*</span>
                </label>
                <select
                  {...register("veterinarioId", { required: isSecretario ? "Debe seleccionar un veterinario" : false })}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                >
                  <option value="">Seleccione un veterinario...</option>
                  {veterinarios?.map((veterinario) => (
                    <option key={veterinario.id} value={veterinario.id}>
                      {veterinario.nombre} {veterinario.apellido}
                      {veterinario.rol?.nombreRol === "VETERINARIO" ? " - Veterinario" : ""}
                    </option>
                  ))}
                </select>
                {errors.veterinarioId && <p className="mt-1 text-xs text-red-500">{errors.veterinarioId.message}</p>}
              </div>
            )}

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Paciente <span className="text-red-500">*</span>
              </label>
              <select
                {...register("pacienteId", { required: "Debe seleccionar un paciente" })}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione un paciente...</option>
                {pacientes?.map((paciente) => (
                  <option key={paciente.id} value={paciente.id}>
                    {paciente.nombre} ({paciente.especie}) - {paciente.cliente?.nombre} {paciente.cliente?.apellido}
                  </option>
                ))}
              </select>
              {errors.pacienteId && <p className="mt-1 text-xs text-red-500">{errors.pacienteId.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Fecha y Hora <span className="text-red-500">*</span>
              </label>
              <input
                type="datetime-local"
                {...register("fechaHora", { required: "La fecha y hora son obligatorias" })}
                min={dayjs().format("YYYY-MM-DDTHH:mm")}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              />
              {errors.fechaHora && <p className="mt-1 text-xs text-red-500">{errors.fechaHora.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Tipo de Servicio</label>
              <input
                type="text"
                {...register("tipoServicio")}
                maxLength={50}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Ej: Consulta General, Vacunación, etc."
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Motivo de la Consulta</label>
              <textarea
                {...register("motivo")}
                rows={3}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Describe el motivo de la consulta..."
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Nivel de Prioridad (Triage)</label>
              <select
                {...register("triageNivel")}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione...</option>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
              </select>
            </div>
          </div>

          <div className="mt-6 flex gap-3 justify-end">
            <button
              type="button"
              onClick={onClose}
              className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={mutation.isPending}
              className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90 disabled:opacity-50"
            >
              {mutation.isPending ? "Agendando..." : "Agendar Cita"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

