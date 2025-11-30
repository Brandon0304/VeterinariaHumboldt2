import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

import { PacientesRepository, type PacienteRequest } from "../services/PacientesRepository";
import { ClientesRepository } from "../../clientes/services/ClientesRepository";
import { calculateSimilarity } from "../../../shared/utils/validations";

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
  const { register, handleSubmit, formState: { errors }, reset, watch } = useForm<FormData>();
  const [similarPatients, setSimilarPatients] = useState<any[]>([]);

  const { data: clientes } = useQuery({
    queryKey: ["clientes"],
    queryFn: ClientesRepository.getAll,
    enabled: isOpen,
  });

  const { data: allPacientes } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
    enabled: isOpen,
  });

  // Detectar pacientes similares mientras el usuario escribe
  const nombreValue = watch("nombre");
  const especieValue = watch("especie");
  const clienteIdValue = watch("clienteId");

  useEffect(() => {
    if (nombreValue && nombreValue.length >= 3 && allPacientes) {
      const similar = allPacientes.filter((p: any) => {
        const nameSimilarity = calculateSimilarity(nombreValue, p.nombre);
        const sameClient = clienteIdValue && p.clienteId === Number.parseInt(clienteIdValue);
        const sameSpecies = especieValue && p.especie === especieValue;
        
        return nameSimilarity > 0.7 || (nameSimilarity > 0.5 && sameClient && sameSpecies);
      });
      
      setSimilarPatients(similar.slice(0, 3)); // Máximo 3 sugerencias
    } else {
      setSimilarPatients([]);
    }
  }, [nombreValue, especieValue, clienteIdValue, allPacientes]);

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
                
                {/* Alerta de pacientes similares */}
                {similarPatients.length > 0 && (
                  <div className="mt-2 rounded-md bg-yellow-50 border border-yellow-200 p-3">
                    <div className="flex">
                      <div className="flex-shrink-0">
                        <svg className="h-5 w-5 text-yellow-400" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                        </svg>
                      </div>
                      <div className="ml-3">
                        <h3 className="text-sm font-medium text-yellow-800">
                          Posibles pacientes duplicados
                        </h3>
                        <div className="mt-2 text-sm text-yellow-700">
                          <p>Se encontraron pacientes con nombres similares:</p>
                          <ul className="mt-1 list-disc list-inside">
                            {similarPatients.map((p: any) => (
                              <li key={p.id}>
                                {p.nombre} ({p.especie}) - Propietario: {p.clienteNombre}
                              </li>
                            ))}
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
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

