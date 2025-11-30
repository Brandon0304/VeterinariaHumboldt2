import type { ApiResponse } from "./types";

/**
 * Extrae los datos de una respuesta estándar del backend y valida que sea exitosa.
 * En caso contrario arroja una excepción con el mensaje provisto por la API.
 */
export const unwrapResponse = <T>(response: ApiResponse<T>): T => {
  if (!response.success) {
    throw new Error(response.message ?? "La operación no se pudo completar");
  }
  return response.data;
};


