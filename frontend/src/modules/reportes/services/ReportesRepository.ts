import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";
import type { ApiReporteResponse } from "../../shared/types/backend";

const BASE_PATH = "/reportes";

export interface ReporteRequest {
  readonly nombre: string;
  readonly tipo?: string;
  readonly generadoPor?: number;
  readonly parametros?: Record<string, unknown>;
}

export const ReportesRepository = {
  generar: async (request: ReporteRequest): Promise<ApiReporteResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiReporteResponse>>(`${BASE_PATH}/generar`, request);
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

