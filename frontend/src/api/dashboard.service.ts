import { apiClient } from "./api-client";

export interface DashboardStatsDto {
  overdueCount: number;
  dueSoonCount: number;
  upcomingDueCount: number;
  totalPending: number;
}

export interface PendingVaccinationsResponse {
  vaccinationNames: string[];
}

export type PendingPriority = "overdue" | "due-soon" | "upcoming";

export const dashboardService = {
  getStats: async (): Promise<DashboardStatsDto> => {
    return apiClient.get<DashboardStatsDto>(
      "/api/v1/immunization-schedule/pending/summary"
    );
  },

  getPendingByPriority: async (
    priority: PendingPriority
  ): Promise<PendingVaccinationsResponse> => {
    return apiClient.get<PendingVaccinationsResponse>(
      `/api/v1/immunization-schedule/pending/${priority}`
    );
  },
};
