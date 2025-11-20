// Store global de autenticación implementado con Zustand.
// Conserva el token JWT y la información básica del usuario autenticado.
import { create } from "zustand";
import { persist } from "zustand/middleware";

// Tipos de usuarios soportados por el backend.
export type UserRole = "ADMIN" | "VETERINARIO" | "SECRETARIO" | "CLIENTE" | string;

// Representa la información mínima necesaria del usuario logueado.
export interface AuthUser {
  readonly id: number;
  readonly nombre: string;
  readonly apellido: string;
  readonly rol: UserRole;
  readonly correo: string;
}

interface AuthState {
  readonly token: string | null;
  readonly user: AuthUser | null;
  setSession: (token: string, user: AuthUser) => void;
  clearSession: () => void;
}

export const authStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      // Almacena token y datos del usuario tras un login exitoso.
      setSession: (token, user) => {
        set({ token, user });
      },
      // Limpia completamente la sesión (logout).
      clearSession: () => {
        set({ token: null, user: null });
      },
    }),
    {
      name: "auth-storage", // Clave utilizada en localStorage.
    },
  ),
);


