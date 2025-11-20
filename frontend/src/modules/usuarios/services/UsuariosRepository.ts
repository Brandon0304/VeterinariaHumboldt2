import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";
import type { ApiUsuarioResponse } from "../../shared/types/backend";

const BASE_PATH = "/usuarios";

export interface UsuarioRequest {
  readonly nombre: string;
  readonly apellido: string;
  readonly correo: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly username: string;
  readonly password: string;
  readonly rolId: number;
  readonly activo?: boolean;
}

export interface UsuarioUpdateRequest {
  readonly nombre?: string;
  readonly apellido?: string;
  readonly correo?: string;
  readonly telefono?: string;
  readonly direccion?: string;
  readonly username?: string;
  readonly password?: string;
  readonly rolId?: number;
  readonly activo?: boolean;
}

export const UsuariosRepository = {
  getAll: async (): Promise<ApiUsuarioResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiUsuarioResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getById: async (id: number): Promise<ApiUsuarioResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiUsuarioResponse>>(`${BASE_PATH}/${id}`);
    return unwrapResponse(data);
  },

  create: async (request: UsuarioRequest): Promise<ApiUsuarioResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiUsuarioResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  update: async (id: number, request: UsuarioUpdateRequest): Promise<ApiUsuarioResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiUsuarioResponse>>(`${BASE_PATH}/${id}`, request);
    return unwrapResponse(data);
  },

  delete: async (id: number): Promise<void> => {
    const client = getApiClient();
    await client.delete<ApiResponse<void>>(`${BASE_PATH}/${id}`);
  },
};

