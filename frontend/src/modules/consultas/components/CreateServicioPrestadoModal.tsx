import { useState } from "react";
import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import toast from "react-hot-toast";

import { ConsultasRepository, type ServicioPrestadoRequest } from "../services/ConsultasRepository";
import type { ApiCitaResponse } from "../../shared/types/backend";

interface CreateServicioPrestadoModalProps {
  readonly isOpen: boolean;
  readonly cita: ApiCitaResponse | null;
  readonly onClose: () => void;
}

interface FormData {
  servicioId: string;
  fechaEjecucion: string;
  observaciones: string;
  costoTotal: string;
}

export const CreateServicioPrestadoModal = ({ isOpen, cita, onClose }: CreateServicioPrestadoModalProps) => {
  const queryClient = useQueryClient();
  const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>({
    defaultValues: {
      fechaEjecucion: dayjs().format("YYYY-MM-DDTHH:mm"),
      costoTotal: "0",
    },
  });

  const mutation = useMutation({
    mutationFn: (data: ServicioPrestadoRequest) => ConsultasRepository.create(data),
    onSuccess: () => {
      toast.success("Servicio prestado registrado exitosamente");
      if (cita) {
        queryClient.invalidateQueries({ queryKey: ["servicios-prestados", cita.idCita] });
      }
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al registrar el servicio prestado");
    },
  });

  const onSubmit = (data: FormData) => {
    if (!cita) {
      toast.error("No se pudo obtener la información de la cita");
      return;
    }

    const request: ServicioPrestadoRequest = {
      citaId: cita.idCita,
      servicioId: parseInt(data.servicioId),
      fechaEjecucion: data.fechaEjecucion || undefined,
      observaciones: data.observaciones || undefined,
      costoTotal: parseFloat(data.costoTotal),
    };
    mutation.mutate(request);
  };

  if (!isOpen || !cita) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Registrar Servicio Prestado</h2>
              <p className="mt-1 text-sm text-gray-500">
                Cita: {cita.paciente?.nombre || "Sin nombre"} - {dayjs(cita.fechaHora).format("DD/MM/YYYY HH:mm")}
              </p>
            </div>
            <button
              onClick={onClose}
              className="rounded-lg p-2 text-gray-400 transition-all hover:bg-gray-100 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <div className="space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                ID del Servicio <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                {...register("servicioId", {
                  required: "El ID del servicio es obligatorio",
                  min: { value: 1, message: "El ID debe ser mayor a 0" },
                })}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Ej: 1, 2, 3..."
              />
              <p className="mt-1 text-xs text-gray-500">
                Ingresa el ID del servicio del catálogo (consulta con el administrador)
              </p>
              {errors.servicioId && <p className="mt-1 text-xs text-red-500">{errors.servicioId.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Fecha y Hora de Ejecución</label>
              <input
                type="datetime-local"
                {...register("fechaEjecucion")}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Costo Total <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                {...register("costoTotal", {
                  required: "El costo total es obligatorio",
                  min: { value: 0.01, message: "El costo debe ser mayor a 0" },
                })}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Ej: 85000.00"
              />
              {errors.costoTotal && <p className="mt-1 text-xs text-red-500">{errors.costoTotal.message}</p>}
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Observaciones</label>
              <textarea
                {...register("observaciones")}
                rows={4}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Observaciones sobre el servicio prestado..."
              />
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
              {mutation.isPending ? "Registrando..." : "Registrar Servicio"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

