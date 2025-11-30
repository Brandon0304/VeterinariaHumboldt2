import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

const BASE_PATH = "/reportes";

export interface ReporteRequest {
  readonly nombre: string;
  readonly tipo?: string;
  readonly generadoPor?: number;
  readonly parametros?: Record<string, unknown>;
}

export interface ReporteCitasResponse {
  readonly totalCitas: number;
  readonly citasRealizadas: number;
  readonly citasCanceladas: number;
  readonly citasPendientes: number;
  readonly porcentajeRealizacion: number;
}

export interface ReporteFacturacionResponse {
  readonly totalFacturas: number;
  readonly ingresoTotal: number;
  readonly ticketPromedio: number;
  readonly clientesAtendidos: number;
}

export interface ReporteActividadesResponse {
  readonly actividades: Array<{
    readonly tipo: string;
    readonly cantidad: number;
    readonly fecha: string;
  }>;
}

export const ReportesRepository = {
  // ============ REPORTES ESPECÍFICOS ============

  obtenerReporteCitas: async (): Promise<ReporteCitasResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteCitasResponse>>(`${BASE_PATH}/citas`);
    return unwrapResponse(data);
  },

  obtenerReporteFacturacion: async (): Promise<ReporteFacturacionResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteFacturacionResponse>>(`${BASE_PATH}/facturacion`);
    return unwrapResponse(data);
  },

  obtenerReporteActividades: async (): Promise<ReporteActividadesResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteActividadesResponse>>(`${BASE_PATH}/actividades`);
    return unwrapResponse(data);
  },

  obtenerResumenUltimos30Dias: async (): Promise<ReporteCitasResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteCitasResponse>>(
      `${BASE_PATH}/citas/resumen/ultimos-30-dias`
    );
    return unwrapResponse(data);
  },

  obtenerResumenMesActual: async (): Promise<ReporteCitasResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteCitasResponse>>(
      `${BASE_PATH}/citas/resumen/mes-actual`
    );
    return unwrapResponse(data);
  },

  obtenerResumenAnoActual: async (): Promise<ReporteCitasResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ReporteCitasResponse>>(
      `${BASE_PATH}/citas/resumen/ano-actual`
    );
    return unwrapResponse(data);
  },

  // ============ EXPORTACIONES ============

  generar: async (request: ReporteRequest): Promise<Record<string, unknown>> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<Record<string, unknown>>>(`${BASE_PATH}/generar`, request);
    return unwrapResponse(data);
  },

  exportarPDF: async (id: number): Promise<Blob> => {
    const client = getApiClient();
    const response = await client.get(`${BASE_PATH}/${id}/exportar-pdf`, {
      responseType: "blob",
    });
    return response.data;
  },

  exportarExcel: async (id: number): Promise<Blob> => {
    const client = getApiClient();
    const response = await client.get(`${BASE_PATH}/${id}/exportar-excel`, {
      responseType: "blob",
    });
    return response.data;
  },
};

