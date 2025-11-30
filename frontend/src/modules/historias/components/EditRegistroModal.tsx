import { useState } from "react";
import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";
import type { ApiRegistroMedicoResponse } from "../../shared/types/backend";
import { HistoriasRepository } from "../services/HistoriasRepository";

interface EditRegistroModalProps {
  readonly isOpen: boolean;
  readonly registro: ApiRegistroMedicoResponse | null;
  readonly onClose: () => void;
}

interface FormData {
  motivo: string;
  diagnostico: string;
  tratamiento: string;
  temperatura?: string;
  frecuenciaCardiaca?: string;
  frecuenciaRespiratoria?: string;
  peso?: string;
}

export const EditRegistroModal = ({ isOpen, registro, onClose }: EditRegistroModalProps) => {
  const queryClient = useQueryClient();
  const [showVitals, setShowVitals] = useState(false);

  const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>({
    defaultValues: registro ? {
      motivo: registro.motivo || "",
      diagnostico: registro.diagnostico || "",
      tratamiento: registro.tratamiento || "",
      temperatura: registro.signosVitales?.temperatura?.toString() || "",
      frecuenciaCardiaca: registro.signosVitales?.frecuenciaCardiaca?.toString() || "",
      frecuenciaRespiratoria: registro.signosVitales?.frecuenciaRespiratoria?.toString() || "",
      peso: registro.signosVitales?.peso?.toString() || "",
    } : undefined,
  });

  const mutation = useMutation({
    mutationFn: (data: FormData) => {
      if (!registro) throw new Error("No hay registro seleccionado");

      const signosVitales: Record<string, number> = {};
      if (data.temperatura) signosVitales.temperatura = parseFloat(data.temperatura);
      if (data.frecuenciaCardiaca) signosVitales.frecuenciaCardiaca = parseInt(data.frecuenciaCardiaca);
      if (data.frecuenciaRespiratoria) signosVitales.frecuenciaRespiratoria = parseInt(data.frecuenciaRespiratoria);
      if (data.peso) signosVitales.peso = parseFloat(data.peso);

      return HistoriasRepository.actualizarRegistro(registro.id, {
        motivo: data.motivo,
        diagnostico: data.diagnostico,
        tratamiento: data.tratamiento,
        signosVitales: Object.keys(signosVitales).length > 0 ? signosVitales : undefined,
      });
    },
    onSuccess: () => {
      toast.success("Registro médico actualizado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["historia-clinica-registros"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al actualizar el registro");
    },
  });

  if (!isOpen || !registro) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="max-h-[90vh] w-full max-w-2xl overflow-y-auto rounded-3xl bg-white p-6 shadow-xl">
        <header className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold text-secondary">Editar Registro Médico</h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 transition-colors hover:bg-gray-100"
          >
            ✕
          </button>
        </header>

        <form onSubmit={handleSubmit((data) => mutation.mutate(data))} className="space-y-4">
          <div>
            <label htmlFor="motivo" className="mb-1 block text-sm font-medium text-gray-700">
              Motivo de la Consulta <span className="text-red-500">*</span>
            </label>
            <input
              id="motivo"
              type="text"
              {...register("motivo", { required: "El motivo es obligatorio" })}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              placeholder="Ej: Control postoperatorio, Vacunación, etc."
            />
            {errors.motivo && <p className="mt-1 text-xs text-red-500">{errors.motivo.message}</p>}
          </div>

          <div>
            <label htmlFor="diagnostico" className="mb-1 block text-sm font-medium text-gray-700">
              Diagnóstico <span className="text-red-500">*</span>
            </label>
            <textarea
              id="diagnostico"
              {...register("diagnostico", { required: "El diagnóstico es obligatorio" })}
              rows={3}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              placeholder="Describe el diagnóstico detallado..."
            />
            {errors.diagnostico && <p className="mt-1 text-xs text-red-500">{errors.diagnostico.message}</p>}
          </div>

          <div>
            <label htmlFor="tratamiento" className="mb-1 block text-sm font-medium text-gray-700">
              Tratamiento <span className="text-red-500">*</span>
            </label>
            <textarea
              id="tratamiento"
              {...register("tratamiento", { required: "El tratamiento es obligatorio" })}
              rows={3}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              placeholder="Describe el tratamiento indicado..."
            />
            {errors.tratamiento && <p className="mt-1 text-xs text-red-500">{errors.tratamiento.message}</p>}
          </div>

          <div className="border-t pt-4">
            <button
              type="button"
              onClick={() => setShowVitals(!showVitals)}
              className="mb-3 flex items-center gap-2 text-sm font-medium text-primary"
            >
              <span>{showVitals ? "▼" : "►"}</span>
              Signos Vitales (opcional)
            </button>

            {showVitals && (
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label htmlFor="temperatura" className="mb-1 block text-xs font-medium text-gray-700">
                    Temperatura (°C)
                  </label>
                  <input
                    id="temperatura"
                    type="number"
                    step="0.1"
                    {...register("temperatura")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="38.5"
                  />
                </div>

                <div>
                  <label htmlFor="frecuenciaCardiaca" className="mb-1 block text-xs font-medium text-gray-700">
                    Frecuencia Cardíaca (lpm)
                  </label>
                  <input
                    id="frecuenciaCardiaca"
                    type="number"
                    {...register("frecuenciaCardiaca")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="90"
                  />
                </div>

                <div>
                  <label htmlFor="frecuenciaRespiratoria" className="mb-1 block text-xs font-medium text-gray-700">
                    Frecuencia Respiratoria (rpm)
                  </label>
                  <input
                    id="frecuenciaRespiratoria"
                    type="number"
                    {...register("frecuenciaRespiratoria")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="25"
                  />
                </div>

                <div>
                  <label htmlFor="peso" className="mb-1 block text-xs font-medium text-gray-700">
                    Peso (kg)
                  </label>
                  <input
                    id="peso"
                    type="number"
                    step="0.1"
                    {...register("peso")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="15.5"
                  />
                </div>
              </div>
            )}
          </div>

          <div className="mt-6 flex gap-3 justify-end border-t pt-4">
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
              className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {mutation.isPending ? "Guardando..." : "Guardar Cambios"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
