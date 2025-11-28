// Barra superior que muestra la fecha actual y la información del veterinario.
import dayjs from "dayjs";
import "dayjs/locale/es";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import toast from "react-hot-toast";

import { authStore } from "../../../shared/state/authStore";
import { AuthService } from "../../../modules/auth/services/AuthService";
import { LogoCircular } from "../../../shared/components/LogoCircular";

dayjs.locale("es");

export const Topbar = () => {
  const user = authStore((state) => state.user);
  const navigate = useNavigate();
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const formattedDate = dayjs().format("dddd, D [de] MMMM YYYY");
  const formattedTime = dayjs().format("hh:mm A");

  const handleLogout = () => {
    const toastId = toast.loading("Cerrando sesión…");
    setIsLoggingOut(true);
    setTimeout(() => {
      AuthService.logout();
      toast.dismiss(toastId);
      toast.success("Sesión cerrada");
      navigate("/auth/login");
    }, 700);
  };

  const userInitials = user ? user.nombre.charAt(0).toUpperCase() + user.apellido.charAt(0).toUpperCase() : "DM";

  return (
    <header className="relative overflow-hidden border-b border-gray-200 bg-gradient-to-r from-white via-white to-gray-50/50 px-6 py-5 shadow-sm backdrop-blur-sm lg:px-8 lg:py-6">
      <div className="absolute inset-0 bg-gradient-to-br from-primary/2 via-transparent to-primary/1 opacity-30"></div>
      <div className="relative flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-end">
        <div className="flex flex-wrap items-center gap-4 lg:gap-6">
          <div className="hidden rounded-xl border border-gray-200 bg-white/80 px-4 py-2.5 text-right shadow-sm backdrop-blur-sm sm:block">
            <p className="text-sm font-semibold text-secondary">{formattedDate}</p>
            <p className="text-xs font-medium text-gray-500">{formattedTime}</p>
          </div>
          <div className="flex items-center gap-3 rounded-2xl border border-gray-200 bg-white/90 px-4 py-2.5 shadow-sm backdrop-blur-sm">
            <div className="flex h-11 w-11 items-center justify-center rounded-full bg-gradient-to-br from-primary to-primary-dark text-sm font-bold text-white shadow-md">
              {userInitials}
            </div>
            <div className="hidden text-sm sm:block">
              <p className="font-semibold text-secondary">
                {user ? `${user.nombre} ${user.apellido}` : "Usuario"}
              </p>
              <p className="text-xs font-medium text-gray-500 uppercase">{user?.rol ?? "Usuario"}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="rounded-xl border border-red-200 bg-white px-4 py-2.5 text-sm font-semibold text-red-600 shadow-sm transition-all hover:border-red-300 hover:bg-red-50 hover:shadow-md"
            title="Cerrar sesión"
          >
            <span className="hidden sm:inline">Cerrar sesión</span>
            <span className="sm:hidden">Salir</span>
          </button>
        </div>
      </div>

    </header>
  );
};


