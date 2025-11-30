import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiHistoriaClinicaResponse, ApiRegistroMedicoResponse } from "../../shared/types/backend";

const BASE_PATH = "/historias-clinicas";

export interface RegistroMedicoRequest {
  readonly fecha: string;
  readonly motivo: string;
  readonly diagnostico: string;
  readonly signosVitales?: Record<string, unknown>;
  readonly tratamiento?: string;
  readonly veterinarioId?: number;
  readonly insumosUsados?: Array<Record<string, unknown>>;
  readonly archivos?: string[];
}

export const HistoriasRepository = {
  getByPaciente: async (pacienteId: number): Promise<ApiHistoriaClinicaResponse> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiHistoriaClinicaResponse>>(`${BASE_PATH}/paciente/${pacienteId}`);
    return unwrapResponse(data);
  },

  getRegistros: async (historiaId: number): Promise<ApiRegistroMedicoResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiRegistroMedicoResponse[]>>(`${BASE_PATH}/${historiaId}/registros`);
    return unwrapResponse(data);
  },

  agregarRegistro: async (historiaId: number, request: RegistroMedicoRequest): Promise<ApiRegistroMedicoResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiRegistroMedicoResponse>>(`${BASE_PATH}/${historiaId}/registros`, request);
    return unwrapResponse(data);
  },

  actualizarRegistro: async (registroId: number, request: Partial<RegistroMedicoRequest>): Promise<ApiRegistroMedicoResponse> => {
    const client = getApiClient();
    const { data } = await client.put<ApiResponse<ApiRegistroMedicoResponse>>(`${BASE_PATH}/registros/${registroId}`, request);
    return unwrapResponse(data);
  },

  exportarPDF: async (historiaId: number): Promise<Blob> => {
    const client = getApiClient();
    const response = await client.get(`${BASE_PATH}/${historiaId}/exportar-pdf`, {
      responseType: "blob",
    });
    return response.data;
  },
};


