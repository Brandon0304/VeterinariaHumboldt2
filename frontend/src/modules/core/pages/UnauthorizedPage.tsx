import { useNavigate } from "react-router-dom";
import { authStore } from "../../../shared/state/authStore";
import { LogoCircular } from "../../../shared/components/LogoCircular";

export const UnauthorizedPage = () => {
  const navigate = useNavigate();
  const user = authStore((state) => state.user);
  const userRole = user?.rol?.toUpperCase() || "";

  const getRedirectPath = () => {
    if (userRole === "SECRETARIO") return "/secretario/inicio";
    if (userRole === "VETERINARIO") return "/veterinario/inicio";
    if (userRole === "ADMIN") return "/usuarios";
    return "/auth/login";
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 px-4">
      <div className="w-full max-w-md rounded-3xl border border-gray-200 bg-white p-8 text-center shadow-xl">
        <div className="mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-danger/10">
          <span className="text-4xl">🔒</span>
        </div>
        <LogoCircular size={80} />
        <h1 className="mt-6 text-2xl font-bold text-secondary">Acceso No Autorizado</h1>
        <p className="mt-3 text-sm text-gray-600">
          No tienes permisos para acceder a esta página. Esta sección está reservada para usuarios con el rol adecuado.
        </p>
        <div className="mt-6 rounded-xl border border-warning/20 bg-warning/5 p-4">
          <p className="text-xs font-semibold text-warning">Tu rol actual:</p>
          <p className="mt-1 text-sm font-bold text-secondary">{userRole || "No definido"}</p>
        </div>
        <button
          onClick={() => navigate(getRedirectPath())}
          className="mt-6 w-full rounded-xl bg-primary px-6 py-3 text-sm font-bold text-white shadow-md transition-all hover:bg-primary-dark hover:shadow-lg"
        >
          Volver al Dashboard
        </button>
      </div>
    </div>
  );
};

