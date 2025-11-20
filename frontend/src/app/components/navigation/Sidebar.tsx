// Sidebar principal del layout.
// Renderiza las secciones según el rol del usuario.
import { NavLink } from "react-router-dom";
import classNames from "classnames";

import { authStore } from "../../../shared/state/authStore";
// import { LogoCircular } from "../../../shared/components/LogoCircular";

interface NavigationItem {
  readonly label: string;
  readonly to: string;
  readonly icon?: string;
  readonly roles?: string[]; // Roles que pueden ver este item
}

const navigationItems: NavigationItem[] = [
  // Navegación para veterinario
  { label: "Inicio", to: "/veterinario/inicio", icon: "home", roles: ["VETERINARIO"] },
  { label: "Mi agenda", to: "/veterinario/agenda", icon: "calendar", roles: ["VETERINARIO"] },
  { label: "Pacientes", to: "/veterinario/pacientes", icon: "users", roles: ["VETERINARIO"] },
  { label: "Historias clínicas", to: "/veterinario/historias", icon: "folder", roles: ["VETERINARIO"] },
  { label: "Consultas", to: "/veterinario/consultas", icon: "stethoscope", roles: ["VETERINARIO"] },
  { label: "Seguimientos", to: "/veterinario/seguimientos", icon: "pulse", roles: ["VETERINARIO"] },
  // Navegación para secretario
  { label: "Inicio", to: "/secretario/inicio", icon: "home", roles: ["SECRETARIO"] },
  { label: "Citas", to: "/secretario/citas", icon: "calendar", roles: ["SECRETARIO"] },
  { label: "Pacientes", to: "/secretario/pacientes", icon: "users", roles: ["SECRETARIO"] },
  { label: "Inventario", to: "/secretario/inventario", icon: "box", roles: ["SECRETARIO"] },
  { label: "Facturas", to: "/secretario/facturas", icon: "receipt", roles: ["SECRETARIO"] },
  { label: "Clientes", to: "/clientes", icon: "people", roles: ["SECRETARIO", "ADMIN"] },
  { label: "Proveedores", to: "/proveedores", icon: "truck", roles: ["SECRETARIO", "ADMIN"] },
  // Navegación compartida
  { label: "Reportes", to: "/reportes", icon: "chart", roles: ["VETERINARIO", "SECRETARIO"] },
  { label: "Notificaciones", to: "/notificaciones", icon: "bell", roles: ["VETERINARIO", "SECRETARIO"] },
  // Solo administradores
  { label: "Configuración", to: "/configuracion", icon: "settings", roles: ["ADMIN"] },
  { label: "Usuarios", to: "/usuarios", icon: "user", roles: ["ADMIN"] },
];

const getIcon = (icon?: string) => {
  // Placeholder de íconos basados en Tailwind/emoji hasta integrar librería de íconos.
  switch (icon) {
    case "home":
      return "🏠";
    case "calendar":
      return "🗓️";
    case "users":
      return "👥";
    case "folder":
      return "📁";
    case "stethoscope":
      return "🩺";
    case "pulse":
      return "📊";
    case "box":
      return "📦";
    case "receipt":
      return "🧾";
    case "chart":
      return "📊";
    case "bell":
      return "🔔";
    case "settings":
      return "⚙️";
    case "user":
      return "👤";
    case "people":
      return "👥";
    case "truck":
      return "🚚";
    default:
      return "•";
  }
};

export const Sidebar = () => {
  const user = authStore((state) => state.user);
  const userRole = user?.rol?.toUpperCase() || "";

  // Filtrar navegación según el rol del usuario
  const navigation = navigationItems.filter((item) => {
    if (!item.roles || item.roles.length === 0) return true;
    return item.roles.includes(userRole);
  });

  return (
    <aside className="relative flex h-full w-72 flex-col overflow-hidden bg-gradient-to-br from-secondary via-secondary/95 to-secondary/90 text-white shadow-2xl lg:w-80">
      {/* Patrón de fondo sutil */}
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_50%,rgba(255,255,255,0.05)_0%,transparent_50%)]"></div>
      
      {/* Header del sidebar */}
      <div className="relative z-10 flex flex-col items-center gap-5 border-b border-white/10 p-8 pb-6">
        <div className="relative">
          <div className="absolute inset-0 rounded-full blur-2xl" style={{ background: "rgba(255,255,255,0.08)" }}></div>
          <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(255,255,255,0.35)" }}>
            <div className="relative rounded-full p-2" style={{ border: "2px solid rgba(255,255,255,0.20)" }}>
              <img
                src="/LogoClinicaVeterinaria.png"
                alt="Logo Clínica Veterinaria Universitaria Humboldt"
                className="h-[110px] w-[110px] rounded-full object-cover bg-white/5"
              />
            </div>
          </div>
        </div>
        <div className="text-center">
          <p className="text-xs font-bold uppercase tracking-widest text-white/80">Clínica Veterinaria</p>
          <p className="mt-0.5 text-sm font-bold uppercase tracking-wider text-white/90">Universitaria</p>
          <p className="mt-1 text-xl font-black text-white drop-shadow-sm">Humboldt</p>
        </div>
        <p className="text-center text-xs leading-relaxed text-white/60">
          Sistema integral para gestionar pacientes, historias clínicas, inventario y más.
        </p>
      </div>

      {/* Navegación */}
      <nav className="relative z-10 mt-2 flex flex-1 flex-col gap-1.5 overflow-y-auto px-4 py-4 scrollbar-hide">
        {navigation.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              classNames(
                "group relative flex items-center gap-3 rounded-xl px-4 py-3.5 text-sm font-semibold transition-all duration-200",
                isActive
                  ? "bg-white text-secondary shadow-lg shadow-primary/20"
                  : "text-white/80 hover:bg-white/10 hover:text-white hover:shadow-md",
              )
            }
          >
            {({ isActive }) => (
              <>
                <span className={`text-xl transition-transform ${isActive ? "scale-110" : "group-hover:scale-110"}`}>
                  {getIcon(item.icon)}
                </span>
                <span className="flex-1">{item.label}</span>
                {isActive && (
                  <div className="absolute right-2 h-2 w-2 rounded-full bg-primary"></div>
                )}
              </>
            )}
          </NavLink>
        ))}
      </nav>

      {/* Footer del sidebar */}
      <div className="relative z-10 border-t border-white/10 bg-secondary/50 p-5 text-center text-xs font-medium text-white/50 backdrop-blur-sm">
        © {new Date().getFullYear()} CVUH. Todos los derechos reservados.
      </div>
    </aside>
  );
};


