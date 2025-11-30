import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { ProductosRepository } from "../services/ProductosRepository";
import {
  MovimientosInventarioRepository,
  type MovimientoEntradaRequest,
  type MovimientoSalidaRequest,
} from "../services/MovimientosInventarioRepository";
import { authStore } from "../../../shared/state/authStore";

interface MovimientoInventarioModalProps {
  readonly isOpen: boolean;
  readonly tipo: "ENTRADA" | "SALIDA";
  readonly productoId?: number;
  readonly onClose: () => void;
}

interface FormData {
  productoId: string;
  cantidad: string;
  referencia: string;
  proveedorId?: string;
}

export const MovimientoInventarioModal = ({
  isOpen,
  tipo,
  productoId: initialProductoId,
  onClose,
}: MovimientoInventarioModalProps) => {
  const queryClient = useQueryClient();
  const user = authStore((state) => state.user);
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>({
    defaultValues: {
      productoId: initialProductoId?.toString() || "",
    },
  });

  const { data: productos } = useQuery({
    queryKey: ["productos"],
    queryFn: ProductosRepository.getAll,
    enabled: isOpen,
  });

  const mutation = useMutation({
    mutationFn: async (data: FormData) => {
      const baseRequest = {
        productoId: parseInt(data.productoId, 10),
        cantidad: parseInt(data.cantidad, 10),
        referencia: data.referencia || undefined,
        usuarioId: user?.id,
      };

      if (tipo === "ENTRADA") {
        const request: MovimientoEntradaRequest = {
          ...baseRequest,
          proveedorId: data.proveedorId ? parseInt(data.proveedorId, 10) : undefined,
        };
        return MovimientosInventarioRepository.registrarEntrada(request);
      } else {
        const request: MovimientoSalidaRequest = baseRequest;
        return MovimientosInventarioRepository.registrarSalida(request);
      }
    },
    onSuccess: () => {
      toast.success(`${tipo === "ENTRADA" ? "Entrada" : "Salida"} registrada exitosamente`);
      queryClient.invalidateQueries({ queryKey: ["productos"] });
      queryClient.invalidateQueries({ queryKey: ["movimientos-inventario"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || `Error al registrar la ${tipo === "ENTRADA" ? "entrada" : "salida"}`);
    },
  });

  const onSubmit = (data: FormData) => {
    mutation.mutate(data);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-2xl font-semibold text-secondary">
            Registrar {tipo === "ENTRADA" ? "Entrada" : "Salida"} de Inventario
          </h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Producto *</label>
            <select
              {...register("productoId", { required: "Debe seleccionar un producto" })}
              disabled={!!initialProductoId}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:bg-gray-50"
            >
              <option value="">Seleccionar producto</option>
              {productos?.map((producto) => (
                <option key={producto.id} value={producto.id}>
                  {producto.nombre} ({producto.sku}) - Stock: {producto.stock}
                </option>
              ))}
            </select>
            {errors.productoId && <p className="mt-1 text-xs text-danger">{errors.productoId.message}</p>}
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Cantidad *</label>
            <input
              type="number"
              min="1"
              {...register("cantidad", { required: "La cantidad es obligatoria", min: 1 })}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Ej: 10"
            />
            {errors.cantidad && <p className="mt-1 text-xs text-danger">{errors.cantidad.message}</p>}
          </div>

          {tipo === "ENTRADA" && (
            <div>
              <label className="mb-2 block text-sm font-semibold text-secondary">Proveedor (opcional)</label>
              <input
                type="number"
                {...register("proveedorId")}
                className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
                placeholder="ID del proveedor"
              />
            </div>
          )}

          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Referencia (opcional)</label>
            <input
              {...register("referencia")}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="Ej: OC-2025-0012"
            />
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
              className={`rounded-2xl px-6 py-2 text-sm font-semibold text-white shadow-soft transition-base disabled:opacity-50 ${
                tipo === "ENTRADA"
                  ? "bg-success hover:bg-success/90"
                  : "bg-warning hover:bg-warning/90"
              }`}
            >
              {mutation.isPending ? "Registrando..." : `Registrar ${tipo === "ENTRADA" ? "Entrada" : "Salida"}`}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

