import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

const BASE_PATH = "/api/configuracion";

export const ConfiguracionRepository = {
  obtenerParametro: async (clave: string): Promise<string> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<string>>(`${BASE_PATH}/parametros/${clave}`);
    return unwrapResponse(data);
  },

  obtenerTodos: async (): Promise<Record<string, string>> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<Record<string, string>>>(`${BASE_PATH}/parametros`);
    return unwrapResponse(data);
  },

  actualizarParametro: async (clave: string, valor: string): Promise<void> => {
    const client = getApiClient();
    await client.put<ApiResponse<void>>(`${BASE_PATH}/parametros/${clave}`, { valor });
  },

  recargarParametros: async (): Promise<void> => {
    const client = getApiClient();
    await client.post<ApiResponse<void>>(`${BASE_PATH}/parametros/recargar`);
  },
};

