import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

const BASE_PATH = "/servicios-prestados";

export interface InsumoConsumidoRequest {
  readonly productoId: number;
  readonly cantidad: number;
  readonly precioUnitario: number;
}

export interface ServicioPrestadoRequest {
  readonly citaId: number;
  readonly servicioId: number;
  readonly fechaEjecucion?: string;
  readonly observaciones?: string;
  readonly costoTotal: number;
  readonly insumosConsumidos?: InsumoConsumidoRequest[];
}

export interface InsumoConsumidoResponse {
  readonly productoId: number;
  readonly cantidad: number;
  readonly precioUnitario: number;
}

export interface ServicioCatalogoResponse {
  readonly id: number;
  readonly nombre: string;
  readonly precioBase: number;
}

export interface ServicioPrestadoResponse {
  readonly idPrestado: number;
  readonly fechaEjecucion: string;
  readonly observaciones?: string;
  readonly costoTotal: number;
  readonly insumos: InsumoConsumidoResponse[];
  readonly cita?: {
    readonly idCita: number;
    readonly fechaHora: string;
    readonly estado: string;
  };
  readonly servicio: ServicioCatalogoResponse;
}

export const ServicioPrestadoRepository = {
  registrar: async (request: ServicioPrestadoRequest): Promise<ServicioPrestadoResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ServicioPrestadoResponse>>(BASE_PATH, request);
    return unwrapResponse(data);
  },

  obtenerPorCita: async (citaId: number): Promise<ServicioPrestadoResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ServicioPrestadoResponse[]>>(
      `${BASE_PATH}/cita/${citaId}`
    );
    return unwrapResponse(data);
  },

  generarResumen: async (servicioPrestadoId: number): Promise<string> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<string>>(
      `${BASE_PATH}/${servicioPrestadoId}/resumen`
    );
    return unwrapResponse(data);
  },
};
