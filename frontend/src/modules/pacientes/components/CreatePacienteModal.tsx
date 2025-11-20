import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { PacientesRepository, type PacienteRequest } from "../services/PacientesRepository";
import { ClientesRepository } from "../../clientes/services/ClientesRepository";

interface CreatePacienteModalProps {
  readonly isOpen: boolean;
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
  clienteId: string;
}

export const CreatePacienteModal = ({ isOpen, onClose }: CreatePacienteModalProps) => {
  const queryClient = useQueryClient();
  const { register, handleSubmit, formState: { errors }, reset } = useForm<FormData>();

  const { data: clientes } = useQuery({
    queryKey: ["clientes"],
    queryFn: ClientesRepository.getAll,
    enabled: isOpen,
  });

  const mutation = useMutation({
    mutationFn: (data: PacienteRequest) => PacientesRepository.create(data),
    onSuccess: () => {
      toast.success("Paciente registrado exitosamente");
      queryClient.invalidateQueries({ queryKey: ["pacientes"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al registrar el paciente");
    },
  });

  const onSubmit = (data: FormData) => {
    const request: PacienteRequest = {
      nombre: data.nombre,
      especie: data.especie,
      raza: data.raza || undefined,
      fechaNacimiento: data.fechaNacimiento || undefined,
      sexo: data.sexo || undefined,
      pesoKg: data.pesoKg ? parseFloat(data.pesoKg) : undefined,
      estadoSalud: data.estadoSalud || undefined,
      clienteId: parseInt(data.clienteId),
    };
    mutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-semibold text-gray-900">Registrar Nuevo Paciente</h2>
          <p className="mt-1 text-sm text-gray-500">Completa la información del paciente</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <div className="space-y-4">
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Nombre del paciente <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  {...register("nombre", { required: "El nombre es obligatorio" })}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                  placeholder="Ej: Max"
                />
                {errors.nombre && <p className="mt-1 text-xs text-red-500">{errors.nombre.message}</p>}
              </div>

              <div>
                <label className="mb-1 block text-sm font-medium text-gray-700">
                  Especie <span className="text-red-500">*</span>
                </label>
                <select
                  {...register("especie", { required: "La especie es obligatoria" })}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                >
                  <option value="">Seleccione...</option>
                  <option value="perro">Perro</option>
                  <option value="gato">Gato</option>
                </select>
                {errors.especie && <p className="mt-1 text-xs text-red-500">{errors.especie.message}</p>}
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

            <div>
              <label className="mb-1 block text-sm font-medium text-gray-700">
                Propietario (Cliente) <span className="text-red-500">*</span>
              </label>
              <select
                {...register("clienteId", { required: "Debe seleccionar un propietario" })}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione un cliente...</option>
                {clientes?.map((cliente) => (
                  <option key={cliente.id} value={cliente.id}>
                    {cliente.nombre} {cliente.apellido} - {cliente.correo}
                  </option>
                ))}
              </select>
              {errors.clienteId && <p className="mt-1 text-xs text-red-500">{errors.clienteId.message}</p>}
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
              {mutation.isPending ? "Registrando..." : "Registrar Paciente"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

