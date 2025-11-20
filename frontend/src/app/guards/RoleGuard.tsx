import { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import toast from "react-hot-toast";
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

  // Si no hay usuario autenticado, redirigir al login
  if (!user) {
    return <Navigate to="/auth/login" replace />;
  }

  // Verificar si el rol del usuario está en la lista de roles permitidos
  const hasAccess = allowedRoles.some((role) => role.toUpperCase() === userRole);

  if (!hasAccess) {
    // Mostrar mensaje de error
    toast.error(`No tienes permisos para acceder a esta página. Se requiere uno de los siguientes roles: ${allowedRoles.join(", ")}`);
    
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
      return <Navigate to="/usuarios" replace />;
    }

    // Por defecto, redirigir al login
    return <Navigate to="/auth/login" replace />;
  }

  return <>{children}</>;
};

