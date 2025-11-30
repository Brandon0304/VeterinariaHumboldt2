import { getApiClient } from '../../../shared/api/ApiClient';
import type {
  InformacionClinica,
  PermisoRol,
  PermisoRolInput,
  ServicioConfiguracion,
  ServicioInput,
  HorarioAtencion,
  HorarioInput,
  DiaSemana,
  AuditoriaDetallada,
  AuditoriaFiltros,
  RespaldoSistema,
  RespaldoInput,
  RespaldoFiltros,
  ConfiguracionAvanzada,
  ConfiguracionInput,
  PageResponse,
  TipoRespaldo,
  EstadoRespaldo
} from '../types';

const api = getApiClient();
const BASE_URL = '/v1/configuracion';

// ==================== INFORMACIÓN CLÍNICA ====================
export const informacionClinicaService = {
  obtenerActiva: () => 
    api.get<InformacionClinica>(`${BASE_URL}/clinica`),
  
  actualizar: (data: Partial<InformacionClinica>) => 
    api.put<InformacionClinica>(`${BASE_URL}/clinica`, data),
  
  existeConfiguracion: () => 
    api.get<boolean>(`${BASE_URL}/clinica/existe`)
};

// ==================== PERMISOS Y ROLES ====================
export const permisoService = {
  validarPermiso: (modulo: string, accion: string) => 
    api.get<boolean>(`${BASE_URL}/permisos/validar`, { params: { modulo, accion } }),
  
  obtenerPorRol: (idRol: number) => 
    api.get<PermisoRol[]>(`${BASE_URL}/permisos/rol/${idRol}`),
  
  obtenerPorModulo: (modulo: string) => 
    api.get<PermisoRol[]>(`${BASE_URL}/permisos/modulo/${modulo}`),
  
  listar: (page = 0, size = 20) => 
    api.get<PageResponse<PermisoRol>>(`${BASE_URL}/permisos`, { params: { page, size } }),
  
  crear: (data: PermisoRolInput) => 
    api.post<PermisoRol>(`${BASE_URL}/permisos`, data),
  
  actualizar: (id: number, data: Partial<PermisoRolInput>) => 
    api.put<PermisoRol>(`${BASE_URL}/permisos/${id}`, data),
  
  eliminar: (id: number) => 
    api.delete(`${BASE_URL}/permisos/${id}`),
  
  obtenerModulos: () => 
    api.get<string[]>(`${BASE_URL}/permisos/modulos`),
  
  obtenerAcciones: () => 
    api.get<string[]>(`${BASE_URL}/permisos/acciones`)
};

// ==================== SERVICIOS ====================
export const servicioConfiguracionService = {
  listarActivos: () => 
    api.get<ServicioConfiguracion[]>(`${BASE_URL}/servicios/activos`),
  
  buscar: (nombre: string, page = 0, size = 20) => 
    api.get<PageResponse<ServicioConfiguracion>>(`${BASE_URL}/servicios/buscar`, { params: { nombre, page, size } }),
  
  obtenerPorCategoria: (categoria: string) => 
    api.get<ServicioConfiguracion[]>(`${BASE_URL}/servicios/categoria/${categoria}`),
  
  obtenerPorRangoPrecio: (min: number, max: number) => 
    api.get<ServicioConfiguracion[]>(`${BASE_URL}/servicios/rango-precio`, { params: { min, max } }),
  
  obtenerCategorias: () => 
    api.get<string[]>(`${BASE_URL}/servicios/categorias`),
  
  obtenerPorId: (id: number) => 
    api.get<ServicioConfiguracion>(`${BASE_URL}/servicios/${id}`),
  
  crear: (data: ServicioInput) => 
    api.post<ServicioConfiguracion>(`${BASE_URL}/servicios`, data),
  
  actualizar: (id: number, data: Partial<ServicioInput>) => 
    api.put<ServicioConfiguracion>(`${BASE_URL}/servicios/${id}`, data),
  
  actualizarPrecio: (id: number, precio: number) => 
    api.patch<ServicioConfiguracion>(`${BASE_URL}/servicios/${id}/precio`, null, { params: { precio } }),
  
  eliminar: (id: number) => 
    api.delete(`${BASE_URL}/servicios/${id}`),
  
  contarActivos: () => 
    api.get<number>(`${BASE_URL}/servicios/count`)
};

// ==================== HORARIOS ====================
export const horarioService = {
  listarActivos: () => 
    api.get<HorarioAtencion[]>(`${BASE_URL}/horarios`),
  
  obtenerPorDia: (dia: DiaSemana) => 
    api.get<HorarioAtencion>(`${BASE_URL}/horarios/dia/${dia}`),
  
  obtenerDiasAbiertos: () => 
    api.get<DiaSemana[]>(`${BASE_URL}/horarios/dias-abiertos`),
  
  verificarAbierta: (dia: DiaSemana) => 
    api.get<boolean>(`${BASE_URL}/horarios/abierta`, { params: { dia } }),
  
  verificarAbiertaAhora: () => 
    api.get<boolean>(`${BASE_URL}/horarios/abierta-ahora`),
  
  crear: (data: HorarioInput) => 
    api.post<HorarioAtencion>(`${BASE_URL}/horarios`, data),
  
  actualizar: (id: number, data: Partial<HorarioInput>) => 
    api.put<HorarioAtencion>(`${BASE_URL}/horarios/${id}`, data),
  
  cambiarEstadoDia: (dia: DiaSemana, cerrado: boolean) => 
    api.patch<HorarioAtencion>(`${BASE_URL}/horarios/dia/${dia}/estado`, null, { params: { cerrado } }),
  
  eliminar: (id: number) => 
    api.delete(`${BASE_URL}/horarios/${id}`)
};

// ==================== AUDITORÍA ====================
export const auditoriaService = {
  buscar: (filtros: AuditoriaFiltros, page = 0, size = 20) => 
    api.get<PageResponse<AuditoriaDetallada>>(`${BASE_URL}/auditoria`, { params: { ...filtros, page, size } }),
  
  obtenerHistorial: (entidad: string, entidadId: number) => 
    api.get<AuditoriaDetallada[]>(`${BASE_URL}/auditoria/historial`, { params: { entidad, entidadId } }),
  
  obtenerPorUsuario: (usuarioId: number, page = 0, size = 20) => 
    api.get<PageResponse<AuditoriaDetallada>>(`${BASE_URL}/auditoria/usuario/${usuarioId}`, { params: { page, size } }),
  
  obtenerRecientes: (limite = 50) => 
    api.get<AuditoriaDetallada[]>(`${BASE_URL}/auditoria/recientes`, { params: { limite } }),
  
  contarPorPeriodo: (fechaDesde: string, fechaHasta: string) => 
    api.get<number>(`${BASE_URL}/auditoria/estadisticas/count`, { params: { fechaDesde, fechaHasta } })
};

// ==================== RESPALDOS ====================
export const respaldoService = {
  obtenerUltimoExitoso: () => 
    api.get<RespaldoSistema>(`${BASE_URL}/respaldos/ultimo-exitoso`),
  
  buscar: (filtros: RespaldoFiltros, page = 0, size = 20) => 
    api.get<PageResponse<RespaldoSistema>>(`${BASE_URL}/respaldos`, { params: { ...filtros, page, size } }),
  
  obtenerPorTipo: (tipo: TipoRespaldo) => 
    api.get<RespaldoSistema[]>(`${BASE_URL}/respaldos/tipo/${tipo}`),
  
  contarPorEstado: (estado: EstadoRespaldo) => 
    api.get<number>(`${BASE_URL}/respaldos/estadisticas/count-por-estado`, { params: { estado } }),
  
  crear: (data: RespaldoInput) => 
    api.post<RespaldoSistema>(`${BASE_URL}/respaldos`, data),
  
  marcarCompletado: (id: number, checksum: string, tamanoBytes: number) => 
    api.patch<RespaldoSistema>(`${BASE_URL}/respaldos/${id}/completar`, null, { params: { checksum, tamanoBytes } }),
  
  marcarFallido: (id: number, observaciones: string) => 
    api.patch<RespaldoSistema>(`${BASE_URL}/respaldos/${id}/fallar`, null, { params: { observaciones } }),
  
  verificarIntegridad: (id: number) => 
    api.post<boolean>(`${BASE_URL}/respaldos/${id}/verificar`),
  
  eliminarAntiguos: (diasRetencion: number) => 
    api.delete(`${BASE_URL}/respaldos/antiguos`, { params: { diasRetencion } })
};

// ==================== CONFIGURACIÓN AVANZADA ====================
export const configuracionAvanzadaService = {
  obtenerValor: (clave: string) => 
    api.get<string>(`${BASE_URL}/avanzada/valor/${clave}`),
  
  obtenerPorClave: (clave: string) => 
    api.get<ConfiguracionAvanzada>(`${BASE_URL}/avanzada/${clave}`),
  
  listarActivas: () => 
    api.get<ConfiguracionAvanzada[]>(`${BASE_URL}/avanzada/activas`),
  
  listarEditables: () => 
    api.get<ConfiguracionAvanzada[]>(`${BASE_URL}/avanzada/editables`),
  
  buscar: (page = 0, size = 20) => 
    api.get<PageResponse<ConfiguracionAvanzada>>(`${BASE_URL}/avanzada`, { params: { page, size } }),
  
  actualizar: (clave: string, valor: string) => 
    api.patch<ConfiguracionAvanzada>(`${BASE_URL}/avanzada/${clave}`, null, { params: { valor } }),
  
  crear: (data: ConfiguracionInput) => 
    api.post<ConfiguracionAvanzada>(`${BASE_URL}/avanzada`, data),
  
  eliminar: (clave: string) => 
    api.delete(`${BASE_URL}/avanzada/${clave}`),
  
  restaurarDefaults: () => 
    api.post(`${BASE_URL}/avanzada/restaurar-defaults`)
};
