import { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { useToast } from "@/hooks/use-toast";

interface VaccineTypeActiveSubstance {
  vaccineTypeId: string;
  activeSubstanceId: string;
  qualitativeAmount: string;
}

interface VaccinationType {
  id: string;
  name: string;
  code: string;
  vaccineTypeActiveSubstances: VaccineTypeActiveSubstance[];
}

interface ImmunizationPlan {
  id: string;
  name: string;
  description: string | null;
  vaccineTypeId: string;
  ageCategoryId: string;
  createdAt: string;
  updatedAt: string;
}

interface VaccinationTypesContextType {
  vaccinationTypes: VaccinationType[];
  immunizationPlans: ImmunizationPlan[];
  loading: boolean;
  fetchVaccinationTypes: () => Promise<void>;
  fetchImmunizationPlans: () => Promise<void>;
}

const VaccinationTypesContext = createContext<VaccinationTypesContextType | undefined>(undefined);

export const VaccinationTypesProvider = ({ children }: { children: ReactNode }) => {
  const [vaccinationTypes, setVaccinationTypes] = useState<VaccinationType[]>([]);
  const [immunizationPlans, setImmunizationPlans] = useState<ImmunizationPlan[]>([]);
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  const fetchVaccinationTypes = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("auth_token");
      if (!token) {
        return;
      }

      const response = await fetch("http://localhost:8000/api/v1/vaccine-types", {
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch vaccination types");
      }

      const data = await response.json();
      setVaccinationTypes(data.vaccineTypeDtoList || []);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error loading vaccination types",
        description: error instanceof Error ? error.message : "Unknown error",
      });
    } finally {
      setLoading(false);
    }
  };

  const fetchImmunizationPlans = async () => {
    try {
      const token = localStorage.getItem("auth_token");
      if (!token) {
        return;
      }

      const response = await fetch("http://localhost:8000/api/v1/immunization-plans", {
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch immunization plans");
      }

      const data = await response.json();
      setImmunizationPlans(data || []);
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error loading immunization plans",
        description: error instanceof Error ? error.message : "Unknown error",
      });
    }
  };

  useEffect(() => {
    // Auto-fetch if token exists
    const token = localStorage.getItem("auth_token");
    if (token && vaccinationTypes.length === 0) {
      fetchVaccinationTypes();
      fetchImmunizationPlans();
    }
  }, []);

  return (
    <VaccinationTypesContext.Provider value={{ vaccinationTypes, immunizationPlans, loading, fetchVaccinationTypes, fetchImmunizationPlans }}>
      {children}
    </VaccinationTypesContext.Provider>
  );
};

export const useVaccinationTypes = () => {
  const context = useContext(VaccinationTypesContext);
  if (context === undefined) {
    throw new Error("useVaccinationTypes must be used within a VaccinationTypesProvider");
  }
  return context;
};
