import { apiClient } from "@/lib/api-client";

export interface Vaccination {
  id: string;
  vaccineName: string;
  administeredOn: string;
  doseOrderClaimed?: number;
  createdAt: string;
  updatedAt?: string;
}

export interface ImmunizationRecordCreateDto {
  vaccineTypeId: string;
  administeredOn: Date;
  doseOrderClaimed: number;
}

export interface UpdateVaccinationRequest {
  id: string;
  vaccineTypeId: string;
  administeredOn: Date;
  doseOrderClaimed: number;
}

export const vaccinationsService = {
  getAll: async (): Promise<Vaccination[]> => {
    return apiClient.get<Vaccination[]>("/api/v1/immunization-records");
  },

  getById: async (id: string): Promise<Vaccination> => {
    return apiClient.get<Vaccination>(`/api/v1/immunization-records/${id}`);
  },

  create: async (data: ImmunizationRecordCreateDto): Promise<Vaccination> => {
    return apiClient.post<Vaccination>("/api/v1/immunization-records", data);
  },

  update: async (
    id: string,
    data: UpdateVaccinationRequest
  ): Promise<Vaccination> => {
    return apiClient.put<Vaccination>(
      `/api/v1/immunization-records/${id}`,
      data
    );
  },

  delete: async (id: string): Promise<void> => {
    return apiClient.delete<void>(`/api/v1/immunization-records/${id}`);
  },
};
