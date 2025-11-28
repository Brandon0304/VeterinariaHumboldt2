// Servicio que ensambla la información necesaria para la vista de dashboard del veterinario.
// Reúne datos desde múltiples endpoints (citas, pacientes, vacunaciones) y los transforma
// en view-models alineados con el prototipo de Figma.
import dayjs from "dayjs";
import isToday from "dayjs/plugin/isToday";
import localizedFormat from "dayjs/plugin/localizedFormat";
import "dayjs/locale/es";

import { CitasRepository } from "../../citas/services/CitasRepository";
import { PacientesRepository } from "../../pacientes/services/PacientesRepository";
import { VacunacionesRepository } from "../../vacunaciones/services/VacunacionesRepository";
import type { ApiCitaResponse } from "../../shared/types/backend";
import type { DashboardDataDTO, DashboardAppointmentDTO } from "../types";

dayjs.extend(isToday);
dayjs.extend(localizedFormat);
dayjs.locale("es");

const CITAS_ESTADO_COMPLETADA = "REALIZADA";

const mapCitaToAppointment = (cita: ApiCitaResponse): DashboardAppointmentDTO => {
  const fecha = dayjs(cita.fechaHora);
  return {
    idCita: cita.idCita,
    hora: fecha.format("HH:mm"),
    paciente: cita.paciente?.nombre ?? "Paciente sin nombre",
    especie: cita.paciente?.especie ?? "Sin especie",
    motivo: cita.motivo ?? cita.tipoServicio ?? "Consulta",
    propietario: cita.paciente?.propietario ?? "Propietario no asignado",
    estado: cita.estado === CITAS_ESTADO_COMPLETADA ? "COMPLETADA" : "PENDIENTE",
  };
};

const buildSummary = (citas: ApiCitaResponse[]): DashboardDataDTO["resumen"] => {
  const today = dayjs();
  const citasDeHoy = citas.filter((cita) => dayjs(cita.fechaHora).isToday());
  const completadasHoy = citasDeHoy.filter((cita) => cita.estado === CITAS_ESTADO_COMPLETADA).length;

  const proximas = citas
    .filter((cita) => dayjs(cita.fechaHora).isAfter(today))
    .sort((a, b) => dayjs(a.fechaHora).diff(dayjs(b.fechaHora)));
  const proxima = proximas.at(0);

  const pacientesAsignados = new Set(
    citas
      .map((cita) => cita.paciente?.id)
      .filter((id): id is number => typeof id === "number"),
  );

  const pendientes = citas.filter((cita) => cita.estado !== CITAS_ESTADO_COMPLETADA).length;

  return {
    citasHoy: citasDeHoy.length,
    citasCompletadasHoy: completadasHoy,
    pacientesAsignados: pacientesAsignados.size,
    pendientes,
    proximaCita: proxima
      ? {
          hora: dayjs(proxima.fechaHora).format("HH:mm"),
          descripcion: `${proxima.paciente?.nombre ?? "Paciente"} • ${proxima.tipoServicio ?? "Consulta"}`,
        }
      : undefined,
  };
};

export const DashboardService = {
  fetchDashboard: async (veterinarioId: number): Promise<DashboardDataDTO> => {
    const [citas, pacientes, vacunasPendientes] = await Promise.all([
      CitasRepository.getByVeterinario(veterinarioId),
      PacientesRepository.getAll(),
      VacunacionesRepository.getPendientes(30),
    ]);

    const citasOrdenadas = [...citas].sort((a, b) => dayjs(a.fechaHora).diff(dayjs(b.fechaHora)));
    const citasDelDia = citasOrdenadas
      .filter((cita) => dayjs(cita.fechaHora).isToday())
      .map(mapCitaToAppointment);

    const resumen = buildSummary(citasOrdenadas);

    // Métricas de accesos rápidos. Algunas son aproximaciones porque el backend aún no
    // expone endpoints específicos para "seguimientos" o "pacientes nuevos".
    const accesosRapidos: DashboardDataDTO["accesosRapidos"] = {
      seguimientosActivos: Math.max(resumen.pendientes - citasDelDia.length, 0),
      vacunasPendientes: vacunasPendientes.length,
      historiasClinicas: pacientes.length,
      pacientesNuevosSemana: pacientes.slice(-3).length, // aproximación: últimos 3 registros
    };

    // Guardar citas originales para poder acceder a ellas desde el dashboard
    const citasOriginalesDelDia = citasOrdenadas.filter((cita) => dayjs(cita.fechaHora).isToday());

    return {
      resumen,
      citasDelDia,
      accesosRapidos,
      citasOriginales: citasOriginalesDelDia, // Citas originales para el modal de detalle
    };
  },
};
