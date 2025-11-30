import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiVacunacionResponse } from "../../shared/types/backend";

const BASE_PATH = "/vacunaciones";

export interface VacunacionRequest {
  readonly pacienteId: number;
  readonly tipoVacuna: string;
  readonly fechaAplicacion: string;
  readonly proximaDosis?: string;
  readonly veterinarioId?: number;
  readonly lote?: string;
  readonly observaciones?: string;
}

export interface ProgramarProximaDosisRequest {
  readonly proximaDosis: string;
}

export const VacunacionesRepository = {
  getPendientes: async (dias = 30): Promise<ApiVacunacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiVacunacionResponse[]>>(`${BASE_PATH}/pendientes`, {
      params: { dias },
    });
    return unwrapResponse(data);
  },

  getByPaciente: async (pacienteId: number): Promise<ApiVacunacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiVacunacionResponse[]>>(`${BASE_PATH}/paciente/${pacienteId}`);
    return unwrapResponse(data);
  },

  registrar: async (request: VacunacionRequest): Promise<ApiVacunacionResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiVacunacionResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  programarProximaDosis: async (
    vacunacionId: number,
    request: ProgramarProximaDosisRequest
  ): Promise<ApiVacunacionResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiVacunacionResponse>>(
      `${BASE_PATH}/${vacunacionId}/proxima-dosis`,
      request
    );
    return unwrapResponse(data);
  },
};


