import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiProveedorResponse } from "../../shared/types/backend";

const BASE_PATH = "/proveedores";

export interface ProveedorRequest {
  readonly nombre: string;
  readonly contacto?: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly correo?: string;
}

export const ProveedoresRepository = {
  getAll: async (): Promise<ApiProveedorResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiProveedorResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getById: async (id: number): Promise<ApiProveedorResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiProveedorResponse>>(`${BASE_PATH}/${id}`);
    return unwrapResponse(data);
  },

  create: async (request: ProveedorRequest): Promise<ApiProveedorResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiProveedorResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  update: async (id: number, request: ProveedorRequest): Promise<ApiProveedorResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiProveedorResponse>>(`${BASE_PATH}/${id}`, request);
    return unwrapResponse(data);
  },

  delete: async (id: number): Promise<void> => {
    const client = getApiClient();
    await client.delete<ApiResponse<void>>(`${BASE_PATH}/${id}`);
  },
};

