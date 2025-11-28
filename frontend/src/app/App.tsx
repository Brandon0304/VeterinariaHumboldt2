// Componente raíz de la aplicación.
// Define las rutas disponibles y aplica los layouts/transiciones comunes.
import { Suspense } from "react";
import { Outlet } from "react-router-dom";

// Importamos la configuración de rutas principal.
import { AppRoutes } from "./routes/AppRoutes";

// Layout base que rodea a todas las rutas (menú lateral, topbar, etc.).
import { DashboardLayout } from "./layouts/DashboardLayout";

// Spinner sintético reutilizable mientras se cargan módulos bajo demanda.
import { FullscreenLoader } from "./components/feedback/FullscreenLoader";

// Guard de autenticación que verifica si el usuario inició sesión.
import { AuthGuard } from "./guards/AuthGuard";

export const App = () => {
  return (
    <AppRoutes
      // Renderizamos el layout principal cuando la ruta lo requiera.
      renderDashboard={() => (
        <AuthGuard>
          <DashboardLayout>
            {/* Suspense permite cargar módulos secundarios de forma diferida */}
            <Suspense fallback={<FullscreenLoader />}>
              <Outlet />
            </Suspense>
          </DashboardLayout>
        </AuthGuard>
      )}
    />
  );
};


