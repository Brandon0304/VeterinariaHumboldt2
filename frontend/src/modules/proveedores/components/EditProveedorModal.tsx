import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { ProveedoresRepository, type ProveedorRequest } from "../services/ProveedoresRepository";
import { FullscreenLoader } from "../../../app/components/feedback/FullscreenLoader";

interface EditProveedorModalProps {
  readonly isOpen: boolean;
  readonly proveedorId: number;
  readonly onClose: () => void;
}

interface ProveedorFormData {
  readonly nombre?: string;
  readonly contacto?: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly correo?: string;
}

export const EditProveedorModal = ({ isOpen, proveedorId, onClose }: EditProveedorModalProps) => {
  const queryClient = useQueryClient();

  const { data: proveedor, isLoading } = useQuery({
    queryKey: ["proveedor", proveedorId],
    queryFn: () => ProveedoresRepository.getById(proveedorId),
    enabled: isOpen && proveedorId !== null,
  });

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<ProveedorFormData>({
    defaultValues: {
      nombre: "",
      contacto: "",
      telefono: "",
      direccion: "",
      correo: "",
    },
  });

  useEffect(() => {
    if (proveedor) {
      reset({
        nombre: proveedor.nombre,
        contacto: proveedor.contacto || "",
        telefono: proveedor.telefono || "",
        direccion: proveedor.direccion || "",
        correo: proveedor.correo || "",
      });
    }
  }, [proveedor, reset]);

  const updateMutation = useMutation({
    mutationFn: (data: ProveedorRequest) => ProveedoresRepository.update(proveedorId, data),
    onSuccess: () => {
      toast.success("Proveedor actualizado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["proveedores"] });
      queryClient.invalidateQueries({ queryKey: ["proveedor", proveedorId] });
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al actualizar el proveedor");
    },
  });

  const onSubmit = (data: ProveedorFormData) => {
    const updateData: ProveedorRequest = {
      nombre: data.nombre?.trim() || proveedor?.nombre || "",
      contacto: data.contacto?.trim() || undefined,
      telefono: data.telefono?.trim() || undefined,
      direccion: data.direccion?.trim() || undefined,
      correo: data.correo?.trim() || undefined,
    };

    updateMutation.mutate(updateData);
  };

  if (!isOpen || !proveedorId) return null;

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
            <h2 className="text-xl font-semibold text-gray-900">Editar Proveedor</h2>
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
              <label className="mb-1 block text-sm font-medium text-gray-700">Nombre</label>
              <input
                type="text"
                className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("nombre")}
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">Persona de Contacto</label>
              <input
                type="text"
                className="w-full rounded-xl border border-gray-200 bg-gray-50 px-4 py-2.5 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                {...register("contacto")}
              />
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
              {isSubmitting || updateMutation.isPending ? "Actualizando..." : "Actualizar Proveedor"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

