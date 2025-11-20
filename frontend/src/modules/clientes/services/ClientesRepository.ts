import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiClienteResponse } from "../../shared/types/backend";

const BASE_PATH = "/clientes";

export interface ClienteRequest {
  readonly nombre: string;
  readonly apellido: string;
  readonly correo: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly identificacion?: string;
}

export interface ClienteUpdateRequest {
  readonly nombre?: string;
  readonly apellido?: string;
  readonly correo?: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly documentoIdentidad?: string;
}

export const ClientesRepository = {
  getAll: async (): Promise<ApiClienteResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiClienteResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getById: async (id: number): Promise<ApiClienteResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiClienteResponse>>(`${BASE_PATH}/${id}`);
    return unwrapResponse(data);
  },

  create: async (request: ClienteRequest): Promise<ApiClienteResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiClienteResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  update: async (id: number, request: ClienteUpdateRequest): Promise<ApiClienteResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiClienteResponse>>(`${BASE_PATH}/${id}`, request);
    return unwrapResponse(data);
  },
};
