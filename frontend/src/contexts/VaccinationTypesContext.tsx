import { createContext, useContext, type ReactNode } from "react";
import { useVaccineTypes, useImmunizationPlans } from "@/hooks/useVaccineTypes";
import type {
  VaccineType,
  ImmunizationPlan,
} from "@/api/vaccine-types.service";

interface VaccinationTypesContextType {
  vaccinationTypes: VaccineType[];
  immunizationPlans: ImmunizationPlan[];
  loading: boolean;
  fetchVaccinationTypes: () => Promise<void>;
  fetchImmunizationPlans: () => Promise<void>;
}

const VaccinationTypesContext = createContext<
  VaccinationTypesContextType | undefined
>(undefined);

export const VaccinationTypesProvider = ({
  children,
}: {
  children: ReactNode;
}) => {
  const {
    data: vaccinationTypes = [],
    isLoading: loadingTypes,
    refetch: refetchTypes,
  } = useVaccineTypes();

  const {
    data: immunizationPlans = [],
    isLoading: loadingPlans,
    refetch: refetchPlans,
  } = useImmunizationPlans();

  const loading = loadingTypes || loadingPlans;

  const fetchVaccinationTypes = async () => {
    await refetchTypes();
  };

  const fetchImmunizationPlans = async () => {
    await refetchPlans();
  };

  return (
    <VaccinationTypesContext.Provider
      value={{
        vaccinationTypes,
        immunizationPlans,
        loading,
        fetchVaccinationTypes,
        fetchImmunizationPlans,
      }}
    >
      {children}
    </VaccinationTypesContext.Provider>
  );
};

export const useVaccinationTypes = () => {
  const context = useContext(VaccinationTypesContext);
  if (context === undefined) {
    throw new Error(
      "useVaccinationTypes must be used within a VaccinationTypesProvider"
    );
  }
  return context;
};
