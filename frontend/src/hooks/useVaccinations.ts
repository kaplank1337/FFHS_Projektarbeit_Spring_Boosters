import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  vaccinationsService,
  type ImmunizationRecordCreateDto,
  type ImmunizationRecordUpdateDto,
} from "@/services/vaccinations.service";
import { successToast, apiErrorToast } from "@/lib/toast-extension";
import { useLanguage } from "@/contexts/LanguageContext";
import {
  DASHBOARD_STATS_QUERY_KEY,
  PENDING_VACCINATIONS_QUERY_KEY,
} from "./useDashboard";

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
  const { t } = useLanguage();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: ImmunizationRecordCreateDto) =>
      vaccinationsService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: DASHBOARD_STATS_QUERY_KEY });
      successToast(t("addVaccination.success"));
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};

export const useUpdateVaccination = () => {
  const { t } = useLanguage();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: string;
      data: ImmunizationRecordUpdateDto;
    }) => vaccinationsService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({
        queryKey: [PENDING_VACCINATIONS_QUERY_KEY, "overdue"],
      });
      queryClient.invalidateQueries({
        queryKey: [PENDING_VACCINATIONS_QUERY_KEY, "due-soon"],
      });
      queryClient.invalidateQueries({
        queryKey: [PENDING_VACCINATIONS_QUERY_KEY, "upcoming"],
      });
      successToast(t("editVaccination.success"));
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};

export const useDeleteVaccination = () => {
  const { t } = useLanguage();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => vaccinationsService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: VACCINATIONS_QUERY_KEY });
      queryClient.invalidateQueries({ queryKey: DASHBOARD_STATS_QUERY_KEY });
      successToast(t("deleteVaccination.success"));
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};
