import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiCitaResponse } from "../../shared/types/backend";

const BASE_PATH = "/citas";

export interface CitaRequest {
  readonly pacienteId: number;
  readonly veterinarioId: number;
  readonly fechaHora: string;
  readonly tipoServicio?: string;
  readonly motivo?: string;
  readonly triageNivel?: string;
}

export interface CitaReprogramarRequest {
  readonly nuevaFechaHora: string;
}

export interface CitaCancelarRequest {
  readonly motivo: string;
}

export const CitasRepository = {
  getAll: async (): Promise<ApiCitaResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiCitaResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },

  getByVeterinario: async (veterinarioId: number): Promise<ApiCitaResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiCitaResponse[]>>(`${BASE_PATH}/veterinario/${veterinarioId}`);
    return unwrapResponse(data);
  },

  getByPaciente: async (pacienteId: number): Promise<ApiCitaResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiCitaResponse[]>>(`${BASE_PATH}/paciente/${pacienteId}`);
    return unwrapResponse(data);
  },

  create: async (request: CitaRequest): Promise<ApiCitaResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiCitaResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  reprogramar: async (citaId: number, request: CitaReprogramarRequest): Promise<ApiCitaResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiCitaResponse>>(`${BASE_PATH}/${citaId}/reprogramar`, request);
    return unwrapResponse(data);
  },

  cancelar: async (citaId: number, request: CitaCancelarRequest): Promise<ApiCitaResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiCitaResponse>>(`${BASE_PATH}/${citaId}/cancelar`, request);
    return unwrapResponse(data);
  },

  completar: async (citaId: number): Promise<ApiCitaResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiCitaResponse>>(`${BASE_PATH}/${citaId}/completar`, {});
    return unwrapResponse(data);
  },

  verificarDisponibilidad: async (veterinarioId: number, fechaHora: string): Promise<boolean> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<boolean>>(
      `${BASE_PATH}/disponibilidad?veterinarioId=${veterinarioId}&fechaHora=${encodeURIComponent(fechaHora)}`
    );
    return unwrapResponse(data);
  },
};


