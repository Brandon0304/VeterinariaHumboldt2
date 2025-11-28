import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { PacientesRepository, type PacienteUpdateRequest } from "../services/PacientesRepository";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";

interface EditPacienteModalProps {
  readonly isOpen: boolean;
  readonly pacienteId: number | null;
  readonly onClose: () => void;
}

interface FormData {
  nombre: string;
  especie: string;
  raza: string;
  fechaNacimiento: string;
  sexo: string;
  pesoKg: string;
  estadoSalud: string;
}

export const EditPacienteModal = ({ isOpen, pacienteId, onClose }: EditPacienteModalProps) => {
  const queryClient = useQueryClient();
  const { register, handleSubmit, formState: { errors }, reset, setValue } = useForm<FormData>();

  const { data: paciente, isLoading } = useQuery({
    queryKey: ["paciente", pacienteId],
    queryFn: () => PacientesRepository.getById(pacienteId!),
    enabled: isOpen && pacienteId !== null,
  });

  // Cargar datos del paciente en el formulario cuando se obtengan
  useEffect(() => {
    if (paciente) {
      setValue("nombre", paciente.nombre);
      setValue("especie", paciente.especie);
      setValue("raza", paciente.raza || "");
      setValue("fechaNacimiento", paciente.fechaNacimiento ? paciente.fechaNacimiento.split("T")[0] : "");
      setValue("sexo", paciente.sexo || "");
      setValue("pesoKg", paciente.pesoKg || "");
      setValue("estadoSalud", paciente.estadoSalud || "");
    }
  }, [paciente, setValue]);

  const mutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: PacienteUpdateRequest }) => PacientesRepository.update(id, data),
    onSuccess: () => {
      toast.success("Paciente actualizado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["pacientes"] });
      queryClient.invalidateQueries({ queryKey: ["paciente", pacienteId] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al actualizar el paciente");
    },
  });

  const onSubmit = (data: FormData) => {
    if (!pacienteId) return;

    const request: PacienteUpdateRequest = {
      nombre: data.nombre || undefined,
      especie: data.especie || undefined,
      raza: data.raza || undefined,
      fechaNacimiento: data.fechaNacimiento || undefined,
      sexo: data.sexo || undefined,
      pesoKg: data.pesoKg ? parseFloat(data.pesoKg) : undefined,
      estadoSalud: data.estadoSalud || undefined,
    };
    mutation.mutate({ id: pacienteId, data: request });
  };

  if (!isOpen || !pacienteId) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold text-gray-900">Editar Paciente</h2>
              <p className="mt-1 text-sm text-gray-500">Actualiza la información del paciente</p>
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
          ) : (
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <div className="grid gap-4 sm:grid-cols-2">
                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Nombre del paciente</label>
                  <input
                    type="text"
                    {...register("nombre")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="Ej: Max"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Especie</label>
                  <select
                    {...register("especie")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  >
                    <option value="">Seleccione...</option>
                    <option value="perro">Perro</option>
                    <option value="gato">Gato</option>
                  </select>
                </div>
              </div>

              <div className="grid gap-4 sm:grid-cols-2">
                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Raza</label>
                  <input
                    type="text"
                    {...register("raza")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="Ej: Beagle"
                  />
                </div>

                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Fecha de nacimiento</label>
                  <input
                    type="date"
                    {...register("fechaNacimiento")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  />
                </div>
              </div>

              <div className="grid gap-4 sm:grid-cols-2">
                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Sexo</label>
                  <select
                    {...register("sexo")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  >
                    <option value="">Seleccione...</option>
                    <option value="Macho">Macho</option>
                    <option value="Hembra">Hembra</option>
                  </select>
                </div>

                <div>
                  <label className="mb-1 block text-sm font-medium text-gray-700">Peso (kg)</label>
                  <input
                    type="number"
                    step="0.1"
                    {...register("pesoKg")}
                    className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                    placeholder="Ej: 12.5"
                  />
                </div>
              </div>

              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Estado de salud</label>
                <input
                  type="text"
                  {...register("estadoSalud")}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="Ej: Estable, En tratamiento, etc."
                />
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
                  {mutation.isPending ? "Actualizando..." : "Actualizar Paciente"}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

