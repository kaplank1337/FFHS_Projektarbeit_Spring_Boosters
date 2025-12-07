import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  vaccinationsService,
  type CreateVaccinationRequest,
  type UpdateVaccinationRequest,
} from "@/services/vaccinations.service";
import { successToast, apiErrorToast } from "@/lib/toast-extension";

export const VACCINATIONS_QUERY_KEY = ["vaccinations"];

export const useVaccinations = () => {
  return useQuery({
    queryKey: VACCINATIONS_QUERY_KEY,
    queryFn: () => vaccinationsService.getAll(),
    enabled: !!localStorage.getItem("auth_token"),
  });
};

export const useVaccination = (id: string) => {
  return useQuery({
    queryKey: [...VACCINATIONS_QUERY_KEY, id],
    queryFn: () => vaccinationsService.getById(id),
    enabled: !!id && !!localStorage.getItem("auth_token"),
  });
};

export const useCreateVaccination = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateVaccinationRequest) => vaccinationsService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["dashboard-stats"] });
      successToast("Impfung erfolgreich hinzugefügt!");
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};

export const useUpdateVaccination = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateVaccinationRequest }) =>
      vaccinationsService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["dashboard-stats"] });
      successToast("Impfung erfolgreich aktualisiert!");
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};

export const useDeleteVaccination = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => vaccinationsService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: ["dashboard-stats"] });
      successToast("Impfung erfolgreich gelöscht!");
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};
