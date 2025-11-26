import { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { authStore } from "../../shared/state/authStore";

interface RoleGuardProps {
  readonly children: ReactNode;
  readonly allowedRoles: string[];
  readonly redirectTo?: string;
}

/**
 * Componente que protege rutas basándose en el rol del usuario.
 * Si el usuario no tiene el rol permitido, redirige a su dashboard correspondiente.
 */
export const RoleGuard = ({ children, allowedRoles, redirectTo }: RoleGuardProps) => {
  const user = authStore((state) => state.user);
  const userRole = user?.rol?.toUpperCase() || "";

  // Verificar si el rol del usuario está en la lista de roles permitidos
  const hasAccess = user ? allowedRoles.some((role) => role.toUpperCase() === userRole) : false;

  // Si no hay usuario autenticado, redirigir al login
  if (!user) {
    return <Navigate to="/auth/login" replace />;
  }

  if (!hasAccess) {
    // Redirigir según el rol del usuario o a la ruta especificada
    if (redirectTo) {
      return <Navigate to={redirectTo} replace />;
    }

    // Redirigir al dashboard correspondiente según el rol
    if (userRole === "SECRETARIO") {
      return <Navigate to="/secretario/inicio" replace />;
    }
    if (userRole === "VETERINARIO") {
      return <Navigate to="/veterinario/inicio" replace />;
    }
    if (userRole === "ADMIN") {
      return <Navigate to="/admin/dashboard" replace />;
    }
    if (userRole === "CLIENTE") {
      return <Navigate to="/cliente/inicio" replace />;
    }

    // Por defecto, redirigir al login
    return <Navigate to="/auth/login" replace />;
  }

  return <>{children}</>;
};

