import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { ApiMovimientoInventarioResponse } from "../../shared/types/backend";

const BASE_PATH = "/api/movimientos-inventario";

export interface MovimientoEntradaRequest {
  readonly productoId: number;
  readonly cantidad: number;
  readonly proveedorId?: number;
  readonly referencia?: string;
  readonly usuarioId?: number;
}

export interface MovimientoSalidaRequest {
  readonly productoId: number;
  readonly cantidad: number;
  readonly referencia?: string;
  readonly usuarioId?: number;
}

export const MovimientosInventarioRepository = {
  registrarEntrada: async (request: MovimientoEntradaRequest): Promise<ApiMovimientoInventarioResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiMovimientoInventarioResponse>>(`${BASE_PATH}/entrada`, request);
    return unwrapResponse(data);
  },

  registrarSalida: async (request: MovimientoSalidaRequest): Promise<ApiMovimientoInventarioResponse> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<ApiMovimientoInventarioResponse>>(`${BASE_PATH}/salida`, request);
    return unwrapResponse(data);
  },

  obtenerPorProducto: async (productoId: number): Promise<ApiMovimientoInventarioResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiMovimientoInventarioResponse[]>>(
      `${BASE_PATH}/producto/${productoId}`
    );
    return unwrapResponse(data);
  },

  obtenerPorRangoFechas: async (fechaInicio: string, fechaFin: string): Promise<ApiMovimientoInventarioResponse[]> => {
    const client = getApiClient();
    const { data } = await client.get<ApiResponse<ApiMovimientoInventarioResponse[]>>(`${BASE_PATH}/rango-fechas`, {
      params: { fechaInicio, fechaFin },
    });
    return unwrapResponse(data);
  },
};

