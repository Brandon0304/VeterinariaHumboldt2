import { getApiClient } from '../../../shared/api/ApiClient';

export interface AdminDashboardData {
  resumenGeneral: {
    totalPacientes: number;
    totalClientes: number;
    totalCitas: number;
    totalFacturas: number;
    ingresosTotales: number;
    citasPendientes: number;
  };
  finanzas: {
    ingresosMesActual: number;
    ingresosMesAnterior: number;
    porcentajeCrecimiento: number;
    facturasPendientes: number;
    montoFacturasPendientes: number;
    facturasRealizadas: number;
    promedioIngresoPorCita: number;
  };
  pacientes: {
    pacientesActivos: number;
    pacientesNuevosEsteMes: number;
    citasRealizadasEsteMes: number;
    citasPendientes: number;
    citasCanceladasEsteMes: number;
    vacunacionesPendientes: number;
    desparasitacionesPendientes: number;
  };
  inventario: {
    totalProductos: number;
    productosStockBajo: number;
    productosAgotados: number;
    valorTotalInventario: number;
  };
  personal: {
    totalVeterinarios: number;
    totalSecretarios: number;
    citasPorVeterinario: Record<string, number>;
  };
  graficos: {
    ingresosPorMes: Array<{
      mes: string;
      anio: number;
      valor: number;
      cantidad: number;
    }>;
    citasPorMes: Array<{
      mes: string;
      anio: number;
      cantidad: number;
      valor: number;
    }>;
    distribucionServicios: Array<{
      tipo: string;
      cantidad: number;
      porcentaje: number;
    }>;
    tendenciaClientes: Array<{
      periodo: string;
      nuevos: number;
      activos: number;
    }>;
  };
}

export const adminDashboardService = {
  obtenerDashboard: async (): Promise<AdminDashboardData> => {
    const client = getApiClient();
    const { data } = await client.get<AdminDashboardData>('/dashboard/admin/estadisticas');
    return data;
  },
};
