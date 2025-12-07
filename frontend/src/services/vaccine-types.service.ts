import { apiClient } from "@/lib/api-client";

export interface VaccineTypeActiveSubstance {
  vaccineTypeId: string;
  activeSubstanceId: string;
  qualitativeAmount: string;
}

export interface VaccineType {
  id: string;
  name: string;
  code: string;
  vaccineTypeActiveSubstances: VaccineTypeActiveSubstance[];
}

export interface VaccineTypesResponse {
  vaccineTypeDtoList: VaccineType[];
}

export interface ImmunizationPlan {
  id: string;
  name: string;
  description: string | null;
  vaccineTypeId: string;
  ageCategoryId: string;
  createdAt: string;
  updatedAt: string;
}

export const vaccineTypesService = {
  getAll: async (): Promise<VaccineTypesResponse> => {
    return apiClient.get<VaccineTypesResponse>("/api/v1/vaccine-types");
  },

  getImmunizationPlans: async (): Promise<ImmunizationPlan[]> => {
    return apiClient.get<ImmunizationPlan[]>("/api/v1/immunization-plans");
  },
};
