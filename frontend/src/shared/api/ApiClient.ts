// Singleton responsable de configurar Axios para toda la aplicación.
// Aplica interceptores para adjuntar el token JWT y centraliza el manejo de errores.
import axios from "axios";
import type { AxiosInstance, AxiosResponse } from "axios";

import { authStore } from "../state/authStore";

// URL base del backend. Se puede parametrizar mediante variables de entorno.
const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080/api";

let apiClient: AxiosInstance | null = null;

const buildClient = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: BASE_URL,
    timeout: 15000,
    headers: {
      "Content-Type": "application/json",
    },
  });

  // Interceptor para incluir el token JWT si existe.
  instance.interceptors.request.use((config) => {
    const token = authStore.getState().token;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // Log temporal para debug
    if (config.url?.includes("/auth/login")) {
      console.log("📤 Request config:", {
        url: config.url,
        method: config.method,
        baseURL: config.baseURL,
        headers: config.headers,
        data: config.data,
      });
    }
    return config;
  });

  // Interceptor de respuesta para logging y unificación de errores.
  instance.interceptors.response.use(
    (response: AxiosResponse) => {
      if (response.config.url?.includes("/auth/login")) {
        console.log("✅ Login exitoso:", response.status, response.data);
      }
      return response;
    },
    (error) => {
      // Log detallado del error
      if (error.config?.url?.includes("/auth/login")) {
        const errorData = error.response?.data;
        console.error("❌ Error en login:", {
          status: error.response?.status,
          statusText: error.response?.statusText,
          data: errorData,
          message: error.message,
          validationErrors: errorData?.data, // Errores de validación del backend
        });
      } else {
        console.error("Error en solicitud HTTP:", error);
      }
      return Promise.reject(error);
    },
  );

  return instance;
};

export const getApiClient = (): AxiosInstance => {
  if (!apiClient) {
    apiClient = buildClient();
  }
  return apiClient;
};


