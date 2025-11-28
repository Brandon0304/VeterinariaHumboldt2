import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { VacunacionesRepository, type ProgramarProximaDosisRequest } from "../services/VacunacionesRepository";

interface ProgramarProximaDosisModalProps {
  readonly isOpen: boolean;
  readonly vacunacionId: number | null;
  readonly onClose: () => void;
}

interface FormData {
  proximaDosis: string;
}

export const ProgramarProximaDosisModal = ({
  isOpen,
  vacunacionId,
  onClose,
}: ProgramarProximaDosisModalProps) => {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>();

  const mutation = useMutation({
    mutationFn: (data: ProgramarProximaDosisRequest) =>
      vacunacionId ? VacunacionesRepository.programarProximaDosis(vacunacionId, data) : Promise.reject(),
    onSuccess: () => {
      toast.success("Próxima dosis programada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["vacunaciones"] });
      queryClient.invalidateQueries({ queryKey: ["vacunaciones-pendientes"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al programar la próxima dosis");
    },
  });

  const onSubmit = (data: FormData) => {
    const request: ProgramarProximaDosisRequest = {
      proximaDosis: data.proximaDosis,
    };
    mutation.mutate(request);
  };

  if (!isOpen || !vacunacionId) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-md rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-2xl font-semibold text-secondary">Programar Próxima Dosis</h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Fecha de Próxima Dosis *</label>
            <input
              type="date"
              {...register("proximaDosis", { required: "La fecha de próxima dosis es obligatoria" })}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            />
            {errors.proximaDosis && <p className="mt-1 text-xs text-danger">{errors.proximaDosis.message}</p>}
          </div>

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="rounded-2xl border border-gray-200 bg-white px-6 py-2 text-sm font-semibold text-secondary transition-base hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={mutation.isPending}
              className="rounded-2xl bg-primary px-6 py-2 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark disabled:opacity-50"
            >
              {mutation.isPending ? "Programando..." : "Programar Dosis"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

