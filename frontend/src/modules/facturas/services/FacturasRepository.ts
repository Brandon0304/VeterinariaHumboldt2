import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiFacturaResponse } from "../../shared/types/backend";

const BASE_PATH = "/facturas";

export interface FacturaRequest {
  readonly clienteId: number;
  readonly total: number;
  readonly formaPago?: string;
  readonly contenido?: Record<string, unknown>;
}

export interface FacturaPagoRequest {
  readonly formaPago: string;
}

export const FacturasRepository = {
  getAll: async (): Promise<ApiFacturaResponse[]> => {
    // TODO: Implementar endpoint GET /facturas en el backend
    // Por ahora retornamos array vacío hasta que el backend lo implemente
    const client = getApiClient();
    try {
      const { data } = await client.get<ApiResponse<ApiFacturaResponse[]>>(BASE_PATH);
      return unwrapResponse(data);
    } catch (error: any) {
      // Si el endpoint no existe aún, retornar array vacío
      if (error.response?.status === 404) {
        return [];
      }
      throw error;
    }
  },

  create: async (request: FacturaRequest): Promise<ApiFacturaResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiFacturaResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  getById: async (id: number): Promise<ApiFacturaResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiFacturaResponse>>(`${BASE_PATH}/${id}`);
    return unwrapResponse(data);
  },

  getByCliente: async (clienteId: number): Promise<ApiFacturaResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiFacturaResponse[]>>(`${BASE_PATH}/cliente/${clienteId}`);
    return unwrapResponse(data);
  },

  generarPDF: async (id: number): Promise<Blob> => {
    const client = getApiClient();
    const response = await client.get(`${BASE_PATH}/${id}/pdf`, {
      responseType: "blob",
    });
    return response.data;
  },

  anular: async (id: number): Promise<ApiFacturaResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiFacturaResponse>>(`${BASE_PATH}/${id}/anular`);
    return unwrapResponse(data);
  },

  registrarPago: async (id: number, request: FacturaPagoRequest): Promise<ApiFacturaResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiFacturaResponse>>(`${BASE_PATH}/${id}/pagar`, request);
    return unwrapResponse(data);
  },
};

