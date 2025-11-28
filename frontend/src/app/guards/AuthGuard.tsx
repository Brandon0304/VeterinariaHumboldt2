// Componente de orden superior que protege rutas privadas.
// Si no existe un token válido en el store de autenticación, redirige al login.
import { ReactNode } from "react";
import { Navigate, useLocation } from "react-router-dom";

import { authStore } from "../../shared/state/authStore";

interface AuthGuardProps {
  readonly children: ReactNode;
}

export const AuthGuard = ({ children }: AuthGuardProps) => {
  const location = useLocation();
  const token = authStore((state) => state.token);

  if (!token) {
    return <Navigate to="/auth/login" replace state={{ from: location }} />;
  }

  return <>{children}</>;
};


