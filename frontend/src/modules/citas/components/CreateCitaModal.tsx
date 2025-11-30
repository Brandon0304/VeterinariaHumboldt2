import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import "dayjs/locale/es";
import toast from "react-hot-toast";

dayjs.locale("es");

import { CitasRepository, type CitaRequest } from "../services/CitasRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";
import { VeterinariosRepository } from "../../usuarios/services/VeterinariosRepository";
import { authStore } from "../../../shared/state/authStore";
import { HorariosDisponibles } from "./HorariosDisponibles";

interface CreateCitaModalProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
  readonly initialDate?: dayjs.Dayjs;
}

interface FormData {
  pacienteId: string;
  veterinarioId?: string; // Solo para secretarios
  fechaHora: string;
  tipoServicio: string;
  motivo: string;
  triageNivel: string;
}

export const CreateCitaModal = ({ isOpen, onClose, initialDate }: CreateCitaModalProps) => {
  const queryClient = useQueryClient();
  const user = authStore((state) => state.user);
  const isSecretario = user?.rol?.toUpperCase() === "SECRETARIO";
  const [horarioError, setHorarioError] = useState<string>("");
  const [fechaSeleccionada, setFechaSeleccionada] = useState<dayjs.Dayjs>(
    initialDate || dayjs().add(2, "hours")
  );
  
  // Calcular el mínimo permitido (ahora + 2 horas, redondeado al siguiente intervalo de 30 min)
  const calcularMinimoPermitido = () => {
    const ahora = dayjs();
    const minimoBase = ahora.add(2, "hours");
    const minutos = minimoBase.minute();
    const minutosRedondeados = minutos <= 30 ? 30 : 60;
    return minimoBase.minute(minutosRedondeados).second(0).format("YYYY-MM-DDTHH:mm");
  };
  
  const { register, handleSubmit, formState: { errors }, reset, watch, setValue } = useForm<FormData>({
    defaultValues: {
      fechaHora: initialDate ? initialDate.format("YYYY-MM-DDTHH:mm") : calcularMinimoPermitido(),
      triageNivel: "MEDIA",
    },
  });

  const fechaHoraValue = watch("fechaHora");
  const veterinarioIdValue = watch("veterinarioId");

  // Validar horario laboral en tiempo real
  useEffect(() => {
    if (!fechaHoraValue) {
      setHorarioError("");
      return;
    }

    const fecha = dayjs(fechaHoraValue);
    const diaSemana = fecha.day(); // 0=Domingo, 6=Sábado
    const hora = fecha.hour();

    // Validar que sea al menos 2 horas en el futuro
    const ahora = dayjs();
    const minimoAnticipacion = ahora.add(2, "hours");
    if (fecha.isBefore(minimoAnticipacion)) {
      const horaMinima = minimoAnticipacion.format("DD/MM/YYYY h:mm A");
      setHorarioError(`⚠️ La hora mínima permitida es: ${horaMinima}`);
      return;
    }

    // Domingo - Cerrado
    if (diaSemana === 0) {
      setHorarioError("❌ La clínica está cerrada los domingos");
      return;
    }

    // Sábado - Solo mañana (8:00 - 12:00)
    if (diaSemana === 6) {
      if (hora < 8 || hora >= 12) {
        setHorarioError("❌ Los sábados el horario es de 8:00 AM a 12:00 PM");
        return;
      }
    }

    // Lunes a Viernes - Mañana (8:00 - 12:00) y Tarde (14:00 - 18:00)
    if (diaSemana >= 1 && diaSemana <= 5) {
      const enHorarioManana = hora >= 8 && hora < 12;
      const enHorarioTarde = hora >= 14 && hora < 18;
      
      if (!enHorarioManana && !enHorarioTarde) {
        setHorarioError("❌ Horario no válido. Seleccione: 8:00-12:00 o 14:00-18:00");
        return;
      }
    }

    // Si pasó todas las validaciones
    setHorarioError("");
  }, [fechaHoraValue]);

  const { data: pacientes } = useQuery({
    queryKey: ["pacientes"],
    queryFn: PacientesRepository.getAll,
    enabled: isOpen,
  });

  const { data: veterinarios } = useQuery({
    queryKey: ["veterinarios"],
    queryFn: VeterinariosRepository.getAll,
    enabled: isOpen && isSecretario,
  });

  const mutation = useMutation({
    mutationFn: (data: CitaRequest) => CitasRepository.create(data),
    onSuccess: () => {
      toast.success("Cita agendada exitosamente");
      queryClient.invalidateQueries({ queryKey: ["citas-veterinario"] });
      queryClient.invalidateQueries({ queryKey: ["todas-las-citas"] });
      reset();
      onClose();
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Error al agendar la cita");
    },
  });

  const onSubmit = async (data: FormData) => {
    if (!user) {
      toast.error("No se pudo obtener la información del usuario");
      return;
    }

    // Determinar el ID del veterinario según el rol
    let veterinarioId: number;
    if (user.rol?.toUpperCase() === "SECRETARIO") {
      if (!data.veterinarioId) {
        toast.error("Debe seleccionar un veterinario");
        return;
      }
      veterinarioId = Number.parseInt(data.veterinarioId);
    } else {
      // Si es veterinario, usar su propio ID
      veterinarioId = user.id;
    }

    // Validar disponibilidad antes de crear la cita
    try {
      // El input datetime-local devuelve formato YYYY-MM-DDTHH:mm, que es compatible con ISO LocalDateTime
      const disponible = await CitasRepository.verificarDisponibilidad(veterinarioId, data.fechaHora);
      
      if (!disponible) {
        toast.error("El veterinario ya tiene una cita programada en ese horario. Por favor, seleccione otra fecha y hora.");
        return;
      }
    } catch (error: any) {
      // Si hay error al verificar disponibilidad, mostrar mensaje pero permitir intentar crear
      console.warn("Error al verificar disponibilidad:", error);
      toast.error("No se pudo verificar la disponibilidad. Se intentará crear la cita de todas formas.");
    }

    const request: CitaRequest = {
      pacienteId: Number.parseInt(data.pacienteId),
      veterinarioId,
      fechaHora: data.fechaHora,
      tipoServicio: data.tipoServicio || undefined,
      motivo: data.motivo || undefined,
      triageNivel: data.triageNivel || undefined,
    };
    mutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 overflow-y-auto">
      <div className="w-full max-w-2xl rounded-2xl bg-white shadow-xl my-8">
        <div className="border-b border-gray-200 px-6 py-4">
          <h2 className="text-xl font-semibold text-gray-900">Agendar Nueva Cita</h2>
          <p className="mt-1 text-sm text-gray-500">Completa la información de la cita</p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="p-6">
          <div className="space-y-4">
            {isSecretario && (
              <div>
                <label htmlFor="veterinarioId" className="mb-1 block text-sm font-medium text-gray-700">
                  Veterinario <span className="text-red-500">*</span>
                </label>
                <select
                  id="veterinarioId"
                  {...register("veterinarioId", { required: isSecretario ? "Debe seleccionar un veterinario" : false })}
                  className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                >
                  <option value="">Seleccione un veterinario...</option>
                  {veterinarios?.map((veterinario) => (
                    <option key={veterinario.id} value={veterinario.id}>
                      {veterinario.nombre} {veterinario.apellido}
                      {veterinario.rol?.nombreRol === "VETERINARIO" ? " - Veterinario" : ""}
                    </option>
                  ))}
                </select>
                {errors.veterinarioId && <p className="mt-1 text-xs text-red-500">{errors.veterinarioId.message}</p>}
              </div>
            )}

            <div>
              <label htmlFor="pacienteId" className="mb-1 block text-sm font-medium text-gray-700">
                Paciente <span className="text-red-500">*</span>
              </label>
              <select
                id="pacienteId"
                {...register("pacienteId", { required: "Debe seleccionar un paciente" })}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione un paciente...</option>
                {pacientes?.map((paciente) => (
                  <option key={paciente.id} value={paciente.id}>
                    {paciente.nombre} ({paciente.especie}) - {paciente.cliente?.nombre} {paciente.cliente?.apellido}
                  </option>
                ))}
              </select>
              {errors.pacienteId && <p className="mt-1 text-xs text-red-500">{errors.pacienteId.message}</p>}
            </div>

            <div>
              <div className="mb-3 flex items-center justify-between">
                <label htmlFor="horarios-disponibles" className="block text-sm font-medium text-gray-700">
                  Seleccionar Fecha y Hora <span className="text-red-500">*</span>
                </label>
                <div className="flex gap-2">
                  <button
                    type="button"
                    onClick={() => setFechaSeleccionada(fechaSeleccionada.subtract(1, "day"))}
                    className="rounded-lg border border-gray-300 px-3 py-1 text-xs font-medium text-gray-700 hover:bg-gray-50"
                  >
                    ← Día Anterior
                  </button>
                  <button
                    type="button"
                    onClick={() => setFechaSeleccionada(dayjs())}
                    className="rounded-lg border border-primary px-3 py-1 text-xs font-medium text-primary hover:bg-primary/5"
                  >
                    Hoy
                  </button>
                  <button
                    type="button"
                    onClick={() => setFechaSeleccionada(fechaSeleccionada.add(1, "day"))}
                    className="rounded-lg border border-gray-300 px-3 py-1 text-xs font-medium text-gray-700 hover:bg-gray-50"
                  >
                    Día Siguiente →
                  </button>
                </div>
              </div>

              <div id="horarios-disponibles" className="rounded-lg border border-gray-200 bg-gray-50 p-4">
                <HorariosDisponibles
                  veterinarioId={(() => {
                    console.log("🔧 Calculando veterinarioId:", {
                      isSecretario,
                      veterinarioIdValue,
                      userId: user?.id,
                      userRol: user?.rol,
                    });
                    
                    if (isSecretario && veterinarioIdValue) {
                      const id = Number.parseInt(veterinarioIdValue);
                      console.log("✅ Modo Secretario - veterinarioId:", id);
                      return id;
                    }
                    if (!isSecretario && user?.id) {
                      console.log("✅ Modo Veterinario - veterinarioId:", user.id);
                      return user.id;
                    }
                    console.log("⚠️ No se pudo determinar veterinarioId");
                    return null;
                  })()}
                  fecha={fechaSeleccionada}
                  onSelectHorario={(fechaHora) => {
                    setValue("fechaHora", fechaHora);
                    setHorarioError("");
                  }}
                  horarioSeleccionado={fechaHoraValue}
                />
              </div>

              <input type="hidden" {...register("fechaHora", { required: "Debe seleccionar un horario disponible" })} />
              
              {errors.fechaHora && (
                <div className="mt-2 rounded-md bg-red-50 border border-red-200 px-3 py-2">
                  <p className="text-xs font-medium text-red-700">{errors.fechaHora.message}</p>
                </div>
              )}
              
              {horarioError && (
                <div className="mt-2 rounded-md bg-red-50 border border-red-200 px-3 py-2">
                  <p className="text-xs font-medium text-red-700">{horarioError}</p>
                </div>
              )}

              {fechaHoraValue && (
                <div className="mt-2 rounded-md bg-green-50 border border-green-200 px-3 py-2">
                  <p className="text-xs font-medium text-green-700">
                    ✓ {dayjs(fechaHoraValue).format("dddd, D [de] MMMM [de] YYYY [a las] HH:mm")}
                  </p>
                </div>
              )}
            </div>

            <div>
              <label htmlFor="tipoServicio" className="mb-1 block text-sm font-medium text-gray-700">
                Tipo de Servicio
              </label>
              <select
                id="tipoServicio"
                {...register("tipoServicio")}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione un servicio...</option>
                <option value="Consulta General">Consulta General</option>
                <option value="Examen Físico">Examen Físico</option>
                <option value="Vacunación">Vacunación</option>
                <option value="Análisis de Laboratorio">Análisis de Laboratorio</option>
                <option value="Radiología">Radiología</option>
                <option value="Ecografía">Ecografía</option>
                <option value="Cirugía General">Cirugía General</option>
                <option value="Cuidado Intensivo">Cuidado Intensivo</option>
                <option value="Esterilización">Esterilización</option>
                <option value="Tratamiento Oncológico">Tratamiento Oncológico</option>
                <option value="Manejo de Heridas">Manejo de Heridas</option>
                <option value="Otro">Otro</option>
              </select>
            </div>

            <div>
              <label htmlFor="motivo" className="mb-1 block text-sm font-medium text-gray-700">Motivo de la Consulta</label>
              <textarea
                id="motivo"
                {...register("motivo")}
                rows={3}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
                placeholder="Describe el motivo de la consulta..."
              />
            </div>

            <div>
              <label htmlFor="triageNivel" className="mb-1 block text-sm font-medium text-gray-700">Nivel de Prioridad (Triage)</label>
              <select
                id="triageNivel"
                {...register("triageNivel")}
                className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:border-primary focus:outline-none focus:ring-2 focus:ring-primary/20"
              >
                <option value="">Seleccione...</option>
                <option value="BAJA">Baja</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
                <option value="URGENTE">Urgente</option>
              </select>
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
              disabled={mutation.isPending || !!horarioError}
              className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-all hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {mutation.isPending ? "Agendando..." : "Agendar Cita"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

