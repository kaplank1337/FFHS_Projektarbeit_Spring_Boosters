import { apiClient } from "@/lib/api-client";

export interface ImmunizationRecordDto {
  id: string;
  vaccineName: string;
  administeredOn: Date;
  doseOrderClaimed: number;
  createdAt: string;
  updatedAt?: string;
}

export interface ImmunizationRecordCreateDto {
  vaccineTypeId: string;
  administeredOn: Date;
  doseOrderClaimed: number;
}

export interface ImmunizationRecordUpdateDto {
  id: string;
  administeredOn: Date;
  doseOrderClaimed: number;
}

export const vaccinationsService = {
  getAll: async (): Promise<ImmunizationRecordDto[]> => {
    return apiClient.get<ImmunizationRecordDto[]>(
      "/api/v1/immunization-records"
    );
  },

  getById: async (id: string): Promise<ImmunizationRecordDto> => {
    return apiClient.get<ImmunizationRecordDto>(
      `/api/v1/immunization-records/${id}`
    );
  },

  create: async (
    data: ImmunizationRecordCreateDto
  ): Promise<ImmunizationRecordDto> => {
    return apiClient.post<ImmunizationRecordDto>(
      "/api/v1/immunization-records",
      data
    );
  },

  update: async (
    id: string,
    data: ImmunizationRecordUpdateDto
  ): Promise<ImmunizationRecordDto> => {
    return apiClient.put<ImmunizationRecordDto>(
      `/api/v1/immunization-records/${id}`,
      data
    );
  },

  delete: async (id: string): Promise<void> => {
    return apiClient.delete<void>(`/api/v1/immunization-records/${id}`);
  },
};
