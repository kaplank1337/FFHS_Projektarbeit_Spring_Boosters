import { useQuery } from "@tanstack/react-query";
import { dashboardService, type PendingPriority } from "@/services/dashboard.service";

export const useDashboardStats = () => {
  return useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => dashboardService.getStats(),
    enabled: !!localStorage.getItem("auth_token"),
  });
};

export const usePendingVaccinations = (priority: PendingPriority | null) => {
  return useQuery({
    queryKey: ["pending-vaccinations", priority],
    queryFn: () => dashboardService.getPendingByPriority(priority!),
    enabled: !!priority && !!localStorage.getItem("auth_token"),
  });
};
