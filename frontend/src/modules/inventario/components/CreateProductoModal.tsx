import { useState } from "react";
import { useForm } from "react-hook-form";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { ProductosRepository, type ProductoRequest } from "../services/ProductosRepository";

interface CreateProductoModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
}

interface FormData {
  sku: string;
  nombre: string;
  descripcion: string;
  tipo: string;
  precioUnitario: string;
  um: string;
  stock: string;
}

export const CreateProductoModal = ({ isOpen, onClose }: CreateProductoModalProps) => {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>();

  const mutation = useMutation({
    mutationFn: (data: ProductoRequest) => ProductosRepository.create(data),
    onSuccess: () => {
      toast.success("Producto creado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["productos"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al crear el producto");
    },
  });

  const onSubmit = (data: FormData) => {
    const request: ProductoRequest = {
      sku: data.sku,
      nombre: data.nombre,
      descripcion: data.descripcion || undefined,
      tipo: data.tipo || undefined,
      precioUnitario: parseFloat(data.precioUnitario),
      um: data.um || undefined,
      stock: data.stock ? parseInt(data.stock, 10) : undefined,
    };
    mutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-2xl font-semibold text-secondary">Nuevo Producto</h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">SKU *</label>
              <input
                {...register("sku", { required: "El SKU es obligatorio" })}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="Ej: MED-001"
              />
              {errors.sku && <p className="mt-1 text-xs text-danger">{errors.sku.message}</p>}
            </div>

            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Nombre *</label>
              <input
                {...register("nombre", { required: "El nombre es obligatorio" })}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="Ej: Antibiótico X"
              />
              {errors.nombre && <p className="mt-1 text-xs text-danger">{errors.nombre.message}</p>}
            </div>
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Descripción</label>
            <textarea
              {...register("descripcion")}
              rows={3}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Descripción detallada del producto"
            />
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Tipo</label>
              <select
                {...register("tipo")}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              >
                <option value="">Seleccionar tipo</option>
                <option value="MEDICAMENTO">Medicamento</option>
                <option value="INSUMO">Insumo</option>
                <option value="PRODUCTO">Producto</option>
              </select>
            </div>

            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Unidad de Medida</label>
              <input
                {...register("um")}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="Ej: unidad, ml, kg"
              />
            </div>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Precio Unitario *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                {...register("precioUnitario", { required: "El precio es obligatorio", min: 0 })}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="0.00"
              />
              {errors.precioUnitario && <p className="mt-1 text-xs text-danger">{errors.precioUnitario.message}</p>}
            </div>

            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Stock Inicial</label>
              <input
                type="number"
                min="0"
                {...register("stock")}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="0"
              />
            </div>
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
              {mutation.isPending ? "Creando..." : "Crear Producto"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

