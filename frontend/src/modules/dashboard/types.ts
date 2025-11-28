// Tipos utilizados por el dashboard del veterinario.

export interface DashboardSummaryDTO {
  readonly citasHoy: number;
  readonly citasCompletadasHoy: number;
  readonly pacientesAsignados: number;
  readonly pendientes: number;
  readonly proximaCita?: {
    readonly hora: string;
    readonly descripcion: string;
  };
}

export interface DashboardAppointmentDTO {
  readonly idCita: number;
  readonly hora: string;
  readonly paciente: string;
  readonly especie: string;
  readonly motivo: string;
  readonly propietario: string;
  readonly estado: "PENDIENTE" | "COMPLETADA" | "URGENTE" | "EN_CURSO";
}

export interface DashboardShortcutsDTO {
  readonly seguimientosActivos: number;
  readonly vacunasPendientes: number;
  readonly historiasClinicas: number;
  readonly pacientesNuevosSemana: number;
}

export interface DashboardDataDTO {
  readonly resumen: DashboardSummaryDTO;
  readonly citasDelDia: DashboardAppointmentDTO[];
  readonly accesosRapidos: DashboardShortcutsDTO;
  readonly citasOriginales?: import("../../shared/types/backend").ApiCitaResponse[];
}


