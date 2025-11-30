import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { FacturasRepository, type FacturaRequest } from "../services/FacturasRepository";
import { ClientesRepository } from "../../clientes/services/ClientesRepository";

interface CreateFacturaModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly clienteId?: number;
  readonly totalInicial?: number;
  readonly contenidoInicial?: Record<string, unknown>;
}

interface FormData {
  clienteId: string;
  total: string;
  formaPago: string;
}

export const CreateFacturaModal = ({
  isOpen,
  onClose,
  clienteId: initialClienteId,
  totalInicial,
  contenidoInicial,
}: CreateFacturaModalProps) => {
  const queryClient = useQueryClient();
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>({
    defaultValues: {
      clienteId: initialClienteId?.toString() || "",
      total: totalInicial?.toString() || "",
      formaPago: "",
    },
  });

  const { data: clientes } = useQuery({
    queryKey: ["clientes"],
    queryFn: ClientesRepository.getAll,
    enabled: isOpen,
  });

  const mutation = useMutation({
    mutationFn: (data: FacturaRequest) => FacturasRepository.create(data),
    onSuccess: () => {
      toast.success("Factura creada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["facturas"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al crear la factura");
    },
  });

  const onSubmit = (data: FormData) => {
    const request: FacturaRequest = {
      clienteId: parseInt(data.clienteId, 10),
      total: parseFloat(data.total),
      formaPago: data.formaPago || undefined,
      contenido: contenidoInicial,
    };
    mutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-lg rounded-3xl bg-white p-6 shadow-soft">
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-2xl font-semibold text-secondary">Nueva Factura</h2>
          <button
            onClick={onClose}
            className="rounded-full p-2 text-gray-400 transition-colors hover:bg-gray-100 hover:text-gray-600"
          >
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Cliente *</label>
            <select
              {...register("clienteId", { required: "Debe seleccionar un cliente" })}
              disabled={!!initialClienteId}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30 disabled:bg-gray-50"
            >
              <option value="">Seleccionar cliente</option>
              {clientes?.map((cliente) => (
                <option key={cliente.id} value={cliente.id}>
                  {cliente.nombre} {cliente.apellido} - {cliente.correo}
                </option>
              ))}
            </select>
            {errors.clienteId && <p className="mt-1 text-xs text-danger">{errors.clienteId.message}</p>}
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Total *</label>
            <input
              type="number"
              step="0.01"
              min="0"
              {...register("total", { required: "El total es obligatorio", min: 0 })}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
              placeholder="0.00"
            />
            {errors.total && <p className="mt-1 text-xs text-danger">{errors.total.message}</p>}
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-secondary">Forma de Pago</label>
            <select
              {...register("formaPago")}
              className="w-full rounded-2xl border border-gray-200 bg-white px-4 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/30"
            >
              <option value="">Seleccionar forma de pago</option>
              <option value="EFECTIVO">Efectivo</option>
              <option value="TARJETA">Tarjeta</option>
              <option value="TRANSFERENCIA">Transferencia</option>
              <option value="PENDIENTE">Pendiente</option>
            </select>
          </div>

          {contenidoInicial && (
            <div className="rounded-2xl border border-gray-200 bg-gray-50 p-4">
              <p className="mb-2 text-sm font-semibold text-secondary">Contenido de la factura:</p>
              <pre className="text-xs text-gray-600">{JSON.stringify(contenidoInicial, null, 2)}</pre>
            </div>
          )}

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
              {mutation.isPending ? "Creando..." : "Crear Factura"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

