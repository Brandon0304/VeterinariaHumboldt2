import { useQuery } from '@tanstack/react-query';
import { adminDashboardService } from '../services/adminDashboardService';

export const useAdminDashboard = () => {
  return useQuery({
    queryKey: ['admin-dashboard'],
    queryFn: adminDashboardService.obtenerDashboard,
    refetchInterval: 30000, // Refetch cada 30 segundos
  });
};
