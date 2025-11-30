import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { ClientesRepository, type ClienteUpdateRequest } from "../services/ClientesRepository";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";

interface EditClienteModalProps {
  readonly isOpen: boolean;
  readonly clienteId: number;
  readonly onClose: () => void;
}

interface ClienteFormData {
  readonly nombre?: string;
  readonly apellido?: string;
  readonly correo?: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly documentoIdentidad?: string;
}

export const EditClienteModal = ({ isOpen, clienteId, onClose }: EditClienteModalProps) => {
  const queryClient = useQueryClient();

  const { data: cliente, isLoading } = useQuery({
    queryKey: ["cliente", clienteId],
    queryFn: () => ClientesRepository.getById(clienteId),
    enabled: isOpen && clienteId !== null,
  });

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ClienteFormData>({
    defaultValues: {
      nombre: "",
      apellido: "",
      correo: "",
      telefono: "",
      direccion: "",
      documentoIdentidad: "",
    },
  });

  useEffect(() => {
    if (cliente) {
      reset({
        nombre: cliente.nombre,
        apellido: cliente.apellido,
        correo: cliente.correo,
        telefono: cliente.telefono || "",
        direccion: cliente.direccion || "",
        documentoIdentidad: cliente.identificacion || "",
      });
    }
  }, [cliente, reset]);

  const updateMutation = useMutation({
    mutationFn: (data: ClienteUpdateRequest) => ClientesRepository.update(clienteId, data),
    onSuccess: () => {
      toast.success("Cliente actualizado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["clientes"] });
      queryClient.invalidateQueries({ queryKey: ["cliente", clienteId] });
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al actualizar el cliente");
    },
  });

  const onSubmit = (data: ClienteFormData) => {
    const updateData: ClienteUpdateRequest = {};
    if (data.nombre?.trim()) updateData.nombre = data.nombre.trim();
    if (data.apellido?.trim()) updateData.apellido = data.apellido.trim();
    if (data.correo?.trim()) updateData.correo = data.correo.trim();
    if (data.telefono?.trim()) updateData.telefono = data.telefono.trim();
    if (data.direccion?.trim()) updateData.direccion = data.direccion.trim();
    if (data.documentoIdentidad?.trim()) updateData.documentoIdentidad = data.documentoIdentidad.trim();

    updateMutation.mutate(updateData);
  };

  if (!isOpen || !clienteId) return null;

  if (isLoading) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
        <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl p-6">
          <FullscreenLoader />
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <div className="flex items-center justify-between">
            <h2 className="text-xl font-semibold text-gray-900">Editar Propietario</h2>
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
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Nombre</label>
                <input
                  type="text"
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("nombre")}
                />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Apellido</label>
                <input
                  type="text"
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("apellido")}
                />
              </div>
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Correo Electrónico</label>
              <input
                type="email"
                className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("correo", {
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: "Correo electrónico inválido",
                  },
                })}
              />
              {errors.correo && <p className="mt-1 text-xs text-danger">{errors.correo.message}</p>}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Teléfono</label>
                <input
                  type="tel"
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("telefono")}
                />
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">Identificación</label>
                <input
                  type="text"
                  className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                  {...register("documentoIdentidad")}
                />
              </div>
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Dirección</label>
              <textarea
                rows={3}
                className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("direccion")}
              />
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
              disabled={isSubmitting || updateMutation.isPending}
              className="flex-1 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-white shadow-soft transition-base hover:bg-primary-dark disabled:cursor-not-allowed disabled:bg-primary/60"
            >
              {isSubmitting || updateMutation.isPending ? "Actualizando..." : "Actualizar Cliente"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

