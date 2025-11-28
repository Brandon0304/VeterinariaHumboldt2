import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiNotificacionResponse } from "../../shared/types/backend";

const BASE_PATH = "/notificaciones";

export interface NotificacionEnviarRequest {
  readonly tipo: string;
  readonly mensaje: string;
  readonly canalId: number;
  readonly datos?: Record<string, unknown>;
}

export interface NotificacionProgramarRequest {
  readonly tipo: string;
  readonly mensaje: string;
  readonly fechaEnvio: string;
  readonly datos?: Record<string, unknown>;
}

export const NotificacionesRepository = {
  programar: async (request: NotificacionProgramarRequest): Promise<ApiNotificacionResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiNotificacionResponse>>(`${BASE_PATH}/programar`, request);
    return unwrapResponse(data);
  },

  enviar: async (request: NotificacionEnviarRequest): Promise<ApiNotificacionResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiNotificacionResponse>>(`${BASE_PATH}/enviar`, request);
    return unwrapResponse(data);
  },

  obtenerPendientes: async (): Promise<ApiNotificacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiNotificacionResponse[]>>(`${BASE_PATH}/pendientes`);
    return unwrapResponse(data);
  },

  obtenerTodas: async (): Promise<ApiNotificacionResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiNotificacionResponse[]>>(BASE_PATH);
    return unwrapResponse(data);
  },
};

