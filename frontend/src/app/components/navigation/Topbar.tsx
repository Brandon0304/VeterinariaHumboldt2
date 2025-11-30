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

interface TopbarProps {
  readonly onMenuClick?: () => void;
}

export const Topbar = ({ onMenuClick }: TopbarProps = {}) => {
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
    <header className="relative overflow-hidden border-b border-gray-200 bg-gradient-to-r from-white via-white to-gray-50/50 px-4 py-4 shadow-sm backdrop-blur-sm sm:px-6 sm:py-5 lg:px-8 lg:py-6">
      <div className="absolute inset-0 bg-gradient-to-br from-primary/2 via-transparent to-primary/1 opacity-30"></div>
      <div className="relative flex items-center justify-between gap-4 lg:justify-end">
        {/* Hamburger Menu Button - Only visible on mobile */}
        {onMenuClick && (
          <button
            onClick={onMenuClick}
            className="flex h-10 w-10 flex-col items-center justify-center gap-1.5 rounded-lg border-2 border-gray-200 bg-white shadow-sm transition-all hover:border-primary hover:bg-primary/5 lg:hidden"
            aria-label="Abrir menú"
          >
            <span className="h-0.5 w-5 rounded-full bg-secondary transition-all"></span>
            <span className="h-0.5 w-5 rounded-full bg-secondary transition-all"></span>
            <span className="h-0.5 w-5 rounded-full bg-secondary transition-all"></span>
          </button>
        )}
        <div className="flex flex-wrap items-center gap-2 sm:gap-3 lg:gap-6">
          <div className="hidden rounded-xl border border-gray-200 bg-white/80 px-3 py-2 text-right shadow-sm backdrop-blur-sm md:block lg:px-4 lg:py-2.5">
            <p className="text-sm font-semibold text-secondary">{formattedDate}</p>
            <p className="text-xs font-medium text-gray-500">{formattedTime}</p>
          </div>
          <div className="flex items-center gap-2 rounded-2xl border border-gray-200 bg-white/90 px-3 py-2 shadow-sm backdrop-blur-sm sm:gap-3 sm:px-4 sm:py-2.5">
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-primary to-primary-dark text-xs font-bold text-white shadow-md sm:h-11 sm:w-11 sm:text-sm">
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
            className="rounded-xl border border-red-200 bg-white px-3 py-2 text-xs font-semibold text-red-600 shadow-sm transition-all hover:border-red-300 hover:bg-red-50 hover:shadow-md sm:px-4 sm:py-2.5 sm:text-sm"
            title="Cerrar sesión"
          >
            <span className="hidden md:inline">Cerrar sesión</span>
            <span className="md:hidden">Salir</span>
          </button>
        </div>
      </div>

    </header>
  );
};


