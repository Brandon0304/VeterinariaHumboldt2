import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiProductoResponse } from "../../shared/types/backend";

const BASE_PATH = "/api/productos";

export interface ProductoRequest {
  readonly sku: string;
  readonly nombre: string;
  readonly descripcion?: string;
  readonly tipo?: string;
  readonly precioUnitario: number;
  readonly um?: string;
  readonly stock?: number;
  readonly metadatos?: Record<string, unknown>;
}

export interface ProductoUpdateRequest {
  readonly nombre?: string;
  readonly descripcion?: string;
  readonly tipo?: string;
  readonly precioUnitario?: number;
  readonly um?: string;
  readonly stock?: number;
  readonly metadatos?: Record<string, unknown>;
}

export const ProductosRepository = {
  getAll: async (): Promise<ApiProductoResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiProductoResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getById: async (id: number): Promise<ApiProductoResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiProductoResponse>>(`${BASE_PATH}/${id}`);
    return unwrapResponse(data);
  },

  create: async (request: ProductoRequest): Promise<ApiProductoResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiProductoResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  verificarDisponibilidad: async (id: number, cantidad: number): Promise<boolean> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<boolean>>(`${BASE_PATH}/${id}/disponibilidad`, {
      params: { cantidad },
    });
    return unwrapResponse(data);
  },

  obtenerProductosConStockBajo: async (nivelStock: number = 10): Promise<ApiProductoResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiProductoResponse[]>>(`${BASE_PATH}/stock-bajo`, {
      params: { nivelStock },
    });
    return unwrapResponse(data);
  },

  update: async (id: number, request: ProductoUpdateRequest): Promise<ApiProductoResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiProductoResponse>>(`${BASE_PATH}/${id}`, request);
    return unwrapResponse(data);
  },
};

