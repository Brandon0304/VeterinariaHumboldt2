/**
 * Tipos del módulo de Configuración del Sistema
 */

// ==================== INFORMACIÓN CLÍNICA ====================
export interface InformacionClinica {
  idClinica: number;
  nombreClinica: string;
  direccion: string;
  telefono: string;
  email: string;
  nit?: string;
  logoUrl?: string;
  horaApertura: string;
  horaCierre: string;
  diasAtencion: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion?: string;
}

// ==================== PERMISOS Y ROLES ====================
export interface PermisoRol {
  idPermiso: number;
  rol: {
    idRol: number;
    nombreRol: string;
  };
  modulo: string;
  accion: string;
  descripcion?: string;
  activo: boolean;
  fechaCreacion: string;
}

export interface PermisoRolInput {
  idRol: number;
  modulo: string;
  accion: string;
  descripcion?: string;
  activo: boolean;
}

// ==================== SERVICIOS ====================
export interface ServicioConfiguracion {
  idServicio: number;
  nombreServicio: string;
  descripcion?: string;
  categoria: string;
  precio: number;
  duracionEstimada?: number;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion?: string;
}

export interface ServicioInput {
  nombreServicio: string;
  descripcion?: string;
  categoria: string;
  precio: number;
  duracionEstimada?: number;
  activo: boolean;
}

// ==================== HORARIOS ====================
export enum DiaSemana {
  LUNES = 'LUNES',
  MARTES = 'MARTES',
  MIERCOLES = 'MIERCOLES',
  JUEVES = 'JUEVES',
  VIERNES = 'VIERNES',
  SABADO = 'SABADO',
  DOMINGO = 'DOMINGO'
}

export interface HorarioAtencion {
  idHorario: number;
  diaSemana: DiaSemana;
  horaApertura: string;
  horaCierre: string;
  cerrado: boolean;
  activo: boolean;
  fechaCreacion: string;
}

export interface HorarioInput {
  diaSemana: DiaSemana;
  horaApertura: string;
  horaCierre: string;
  cerrado: boolean;
  activo: boolean;
}

// ==================== AUDITORÍA ====================
export enum TipoOperacion {
  CREATE = 'CREATE',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
  EXPORT = 'EXPORT',
  APPROVE = 'APPROVE',
  LOGIN = 'LOGIN',
  LOGOUT = 'LOGOUT',
  RESTORE = 'RESTORE'
}

export interface AuditoriaDetallada {
  idAuditoria: number;
  entidad: string;
  entidadId?: number;
  tipoAccion: string;
  datosAntes?: Record<string, any>;
  datosDespues?: Record<string, any>;
  usuarioId: number;
  nombreUsuario?: string;
  ipOrigen?: string;
  userAgent?: string;
  fechaOperacion: string;
}

export interface AuditoriaFiltros {
  entidad?: string;
  entidadId?: number;
  tipoAccion?: string;
  usuarioId?: number;
  fechaDesde?: string;
  fechaHasta?: string;
}

export interface EstadisticasAuditoria {
  totalOperaciones: number;
  operacionesPorTipo: Record<string, number>;
}

// ==================== RESPALDOS ====================
export enum TipoRespaldo {
  COMPLETO = 'COMPLETO',
  INCREMENTAL = 'INCREMENTAL',
  DIFERENCIAL = 'DIFERENCIAL',
  CONFIGURACION = 'CONFIGURACION'
}

export enum EstadoRespaldo {
  COMPLETADO = 'COMPLETADO',
  EN_PROCESO = 'EN_PROCESO',
  FALLIDO = 'FALLIDO',
  CORRUPTO = 'CORRUPTO',
  RESTAURADO = 'RESTAURADO'
}

export interface RespaldoSistema {
  idRespaldo: number;
  tipoRespaldo: TipoRespaldo;
  rutaArchivo: string;
  tamanoBytes: number;
  checksumSha256?: string;
  estado: EstadoRespaldo;
  fechaRespaldo: string;
  usuarioId: number;
  nombreUsuario?: string;
  observaciones?: string;
}

export interface RespaldoInput {
  tipoRespaldo: TipoRespaldo;
  observaciones?: string;
}

export interface RespaldoFiltros {
  tipo?: TipoRespaldo;
  estado?: EstadoRespaldo;
  fechaDesde?: string;
  fechaHasta?: string;
}

// ==================== CONFIGURACIÓN AVANZADA ====================
export enum TipoDato {
  STRING = 'STRING',
  INTEGER = 'INTEGER',
  DECIMAL = 'DECIMAL',
  BOOLEAN = 'BOOLEAN',
  JSON = 'JSON'
}

export interface ConfiguracionAvanzada {
  idConfiguracion: number;
  clave: string;
  valor: string;
  tipoDato: TipoDato;
  descripcion?: string;
  editable: boolean;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion?: string;
}

export interface ConfiguracionInput {
  clave: string;
  valor: string;
  tipoDato: TipoDato;
  descripcion?: string;
  editable: boolean;
  activo: boolean;
}

// ==================== RESPUESTAS API ====================
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  timestamp: string;
}
