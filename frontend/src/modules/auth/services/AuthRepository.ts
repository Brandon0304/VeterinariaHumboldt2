// Repository pattern para encapsular las llamadas HTTP relacionadas con autenticación.
// Evita que los componentes conozcan los detalles de Axios o las URLs.
import { getApiClient } from "../../../shared/api/ApiClient";
import { unwrapResponse } from "../../../shared/api/ApiResponseAdapter";
import type { ApiResponse } from "../../../shared/api/types";

import type { LoginRequestDTO, LoginResponseDTO } from "../types";

const BASE_PATH = "/auth";

export interface RegisterRequest {
  readonly username: string;
  readonly password: string;
  readonly email: string;
  readonly nombre: string;
  readonly apellido: string;
}

export const AuthRepository = {
  login: async (payload: LoginRequestDTO): Promise<LoginResponseDTO> => {
    const client = getApiClient();
    // Log temporal para debug
    console.log("🔐 Enviando login con payload:", payload);
    console.log("🔐 URL completa:", `${client.defaults.baseURL}${BASE_PATH}/login`);
    
    const response = await client.post<ApiResponse<LoginResponseDTO>>(`${BASE_PATH}/login`, payload, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return unwrapResponse(response.data);
  },

  register: async (payload: RegisterRequest): Promise<void> => {
    const client = getApiClient();
    await client.post<ApiResponse<void>>(`${BASE_PATH}/register`, payload);
  },

  forgotPassword: async (emailOrUsername: string): Promise<string> => {
    const client = getApiClient();
    const { data } = await client.post<ApiResponse<string>>(`${BASE_PATH}/forgot-password`, {
      emailOrUsername,
    });
    return unwrapResponse(data);
  },

  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    const client = getApiClient();
    await client.post<ApiResponse<void>>(`${BASE_PATH}/reset-password`, {
      token,
      newPassword,
    });
  },
};


