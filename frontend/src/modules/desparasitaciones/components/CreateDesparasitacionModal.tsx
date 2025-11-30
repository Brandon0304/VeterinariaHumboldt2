import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import dayjs from "dayjs";

import { DesparasitacionesRepository, type DesparasitacionRequest } from "../services/DesparasitacionesRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";

interface CreateDesparasitacionModalProps {
  readonly isOpen: boolean;
  readonly pacienteId?: number;
  readonly onClose: () => void;
}

interface DesparasitacionFormData {
  readonly pacienteId: number;
  readonly productoUsado: string;
  readonly fechaAplicacion: string;
  readonly proximaAplicacion?: string;
}

export const CreateDesparasitacionModal = ({ isOpen, pacienteId, onClose }: CreateDesparasitacionModalProps) => {
  const queryClient = useQueryClient();

  const { data: pacientes } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
    enabled: !pacienteId,
  });

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
    watch,
  } = useForm<DesparasitacionFormData>({
    defaultValues: {
      pacienteId: pacienteId || 0,
      productoUsado: "",
      fechaAplicacion: dayjs().format("YYYY-MM-DD"),
      proximaAplicacion: "",
    },
  });

  const fechaAplicacion = watch("fechaAplicacion");

  const createMutation = useMutation({
    mutationFn: (data: DesparasitacionRequest) => DesparasitacionesRepository.registrar(data),
    onSuccess: () => {
      toast.success("Desparasitación registrada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["desparasitaciones"] });
      queryClient.invalidateQueries({ queryKey: ["desparasitaciones-pendientes"] });
      if (pacienteId) {
        queryClient.invalidateQueries({ queryKey: ["desparasitaciones-paciente", pacienteId] });
      }
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al registrar la desparasitación");
    },
  });

  const onSubmit = (data: DesparasitacionFormData) => {
    createMutation.mutate({
      pacienteId: data.pacienteId,
      productoUsado: data.productoUsado.trim(),
      fechaAplicacion: data.fechaAplicacion,
      proximaAplicacion: data.proximaAplicacion?.trim() || undefined,
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Registrar Desparasitación</h2>
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
            {!pacienteId && (
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Paciente <span className="text-red-500">*</span>
                </label>
                <select
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("pacienteId", { required: "El paciente es obligatorio", valueAsNumber: true })}
                >
                  <option value={0}>Seleccione un paciente</option>
                  {pacientes?.map((paciente) => (
                    <option key={paciente.id} value={paciente.id}>
                      {paciente.nombre} ({paciente.especie})
                    </option>
                  ))}
                </select>
                {errors.pacienteId && <p className="mt-1 text-xs text-danger">{errors.pacienteId.message}</p>}
              </div>
            )}

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Producto Usado <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                placeholder="Nombre del producto desparasitante"
                className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("productoUsado", { required: "El producto usado es obligatorio" })}
              />
              {errors.productoUsado && <p className="mt-1 text-xs text-danger">{errors.productoUsado.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Fecha de Aplicación <span className="text-red-500">*</span>
                </label>
                <input
                  type="date"
                  max={dayjs().format("YYYY-MM-DD")}
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("fechaAplicacion", { required: "La fecha de aplicación es obligatoria" })}
                />
                {errors.fechaAplicacion && <p className="mt-1 text-xs text-danger">{errors.fechaAplicacion.message}</p>}
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Próxima Aplicación</label>
                <input
                  type="date"
                  min={fechaAplicacion || dayjs().format("YYYY-MM-DD")}
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("proximaAplicacion")}
                />
              </div>
            </div>
          </div>

          <div className="mt-6 flex gap-3">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-xl border border-gray-300 bg-white px-4 py-2.5 text-sm font-medium text-gray-700 transition-all hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={isSubmitting || createMutation.isPending}
              className="flex-1 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark disabled:cursor-not-allowed disabled:bg-primary/60"
            >
              {isSubmitting || createMutation.isPending ? "Registrando..." : "Registrar Desparasitación"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

