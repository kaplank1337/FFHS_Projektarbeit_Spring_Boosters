import { useQuery } from "@tanstack/react-query";
import {
  dashboardService,
  type PendingPriority,
} from "@/api/dashboard.service";

export const DASHBOARD_STATS_QUERY_KEY = ["dashboard-stats"];
export const PENDING_VACCINATIONS_QUERY_KEY = "pending-vaccinations";

export const useDashboardStats = () => {
  return useQuery({
    queryKey: DASHBOARD_STATS_QUERY_KEY,
    queryFn: () => dashboardService.getStats(),
    enabled: !!sessionStorage.getItem("auth_token"),
  });
};

export const usePendingVaccinations = (priority: PendingPriority | null) => {
  return useQuery({
    queryKey: [PENDING_VACCINATIONS_QUERY_KEY, priority],
    queryFn: () => dashboardService.getPendingByPriority(priority!),
    enabled: !!priority && !!sessionStorage.getItem("auth_token"),
  });
};
