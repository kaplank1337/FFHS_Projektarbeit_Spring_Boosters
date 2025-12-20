import { useQuery } from "@tanstack/react-query";
import { vaccineTypesService } from "@/api/vaccine-types.service";

export const useVaccineTypes = () => {
  return useQuery({
    queryKey: ["vaccine-types"],
    queryFn: () => vaccineTypesService.getAll(),
    enabled: !!sessionStorage.getItem("auth_token"),
    select: (data) => data.vaccineTypeDtoList,
  });
};

export const useImmunizationPlans = () => {
  return useQuery({
    queryKey: ["immunization-plans"],
    queryFn: () => vaccineTypesService.getImmunizationPlans(),
    enabled: !!sessionStorage.getItem("auth_token"),
  });
};
