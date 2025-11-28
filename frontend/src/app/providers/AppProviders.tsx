// Este componente agrupa todos los providers globales utilizados en la aplicación.
// Actualmente incluye: React Query (para manejo de solicitudes asíncronas) y React Router.
import { ReactNode, useState } from "react";
import { BrowserRouter } from "react-router-dom";
import { QueryCache, QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "react-hot-toast";

// Configuración por defecto de React Query para manejar cache y reintentos.
const createQueryClient = () =>
  new QueryClient({
    queryCache: new QueryCache({
      // Se podría añadir lógica global para logging de errores de red.
      onError: (error) => {
        console.error("Error en cache de React Query:", error);
      },
    }),
    defaultOptions: {
      queries: {
        // Reintenta automáticamente solicitudes fallidas hasta 2 intentos.
        retry: 2,
        // Usa datos en caché durante 5 minutos.
        staleTime: 5 * 60 * 1000,
        // Evita reintentos cuando la aplicación se encuentra en segundo plano.
        refetchOnWindowFocus: false,
      },
      mutations: {
        // Manejo global de errores en mutaciones.
        onError: (error) => {
          console.error("Error en mutación de React Query:", error);
        },
      },
    },
  });

// Tipado de las props del componente.
interface AppProvidersProps {
  // Elementos hijos que se beneficiarán de los providers.
  readonly children: ReactNode;
}

export const AppProviders = ({ children }: AppProvidersProps) => {
  // Se utiliza useState para crear una sola instancia del QueryClient.
  const [queryClient] = useState(createQueryClient);

  return (
    // BrowserRouter provee el contexto de enrutamiento a toda la app.
    <BrowserRouter>
      {/* QueryClientProvider expone la funcionalidad de React Query. */}
      <QueryClientProvider client={queryClient}>
        {children}
        {/* Toaster muestra notificaciones emergentes globales. */}
        <Toaster position="top-right" />
      </QueryClientProvider>
    </BrowserRouter>
  );
};

