import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiServicioPrestadoResponse } from "../../shared/types/backend";

const BASE_PATH = "/servicios-prestados";

export interface ServicioPrestadoRequest {
  readonly citaId: number;
  readonly servicioId: number;
  readonly fechaEjecucion?: string;
  readonly observaciones?: string;
  readonly costoTotal: number;
  readonly insumosConsumidos?: Array<{
    readonly productoId: number;
    readonly cantidad: number;
    readonly precioUnitario: number;
  }>;
}

export const ConsultasRepository = {
  getByCita: async (citaId: number): Promise<ApiServicioPrestadoResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiServicioPrestadoResponse[]>>(`${BASE_PATH}/cita/${citaId}`);
    return unwrapResponse(data);
  },

  generarResumen: async (servicioPrestadoId: number): Promise<string> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<string>>(`${BASE_PATH}/${servicioPrestadoId}/resumen`);
    return unwrapResponse(data);
  },

  create: async (request: ServicioPrestadoRequest): Promise<ApiServicioPrestadoResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiServicioPrestadoResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },
};


