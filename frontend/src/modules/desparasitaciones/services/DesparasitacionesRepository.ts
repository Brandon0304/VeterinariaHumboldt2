import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiDesparasitacionResponse } from "../../shared/types/backend";

const BASE_PATH = "/desparasitaciones";

export interface DesparasitacionRequest {
  readonly pacienteId: number;
  readonly productoUsado: string;
  readonly fechaAplicacion: string;
  readonly proximaAplicacion?: string;
}

export const DesparasitacionesRepository = {
  getAll: async (): Promise<ApiDesparasitacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiDesparasitacionResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getPendientes: async (dias = 30): Promise<ApiDesparasitacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiDesparasitacionResponse[]>>(`${BASE_PATH}/pendientes`, {
      params: { dias },
    });
    return unwrapResponse(data);
  },

  getByPaciente: async (pacienteId: number): Promise<ApiDesparasitacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiDesparasitacionResponse[]>>(`${BASE_PATH}/paciente/${pacienteId}`);
    return unwrapResponse(data);
  },

  registrar: async (request: DesparasitacionRequest): Promise<ApiDesparasitacionResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiDesparasitacionResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },
};

