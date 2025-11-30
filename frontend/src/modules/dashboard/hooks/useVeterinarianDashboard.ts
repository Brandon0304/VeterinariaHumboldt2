// Hook que conecta React Query con el servicio de dashboard.
// Mantiene la responsabilidad de obtener el ID del veterinario autenticado.
import { useQuery } from "@tanstack/react-query";

import { authStore } from "../../../shared/state/authStore";

import { DashboardService } from "../services/DashboardService";

export const DASHBOARD_QUERY_KEY = ["veterinarian-dashboard"];

export const useVeterinarianDashboard = () => {
  const user = authStore((state) => state.user);

  return useQuery({
    queryKey: [...DASHBOARD_QUERY_KEY, user?.id],
    queryFn: async () => {
      if (!user) {
        throw new Error("Usuario no autenticado");
      }
      return DashboardService.fetchDashboard(user.id);
    },
    enabled: Boolean(user?.id),
  });
};
