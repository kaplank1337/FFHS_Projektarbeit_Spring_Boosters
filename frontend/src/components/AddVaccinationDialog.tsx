import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { CalendarIcon, Plus } from "lucide-react";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

import { useToast } from "@/hooks/use-toast";
import { useVaccinationTypes } from "@/contexts/VaccinationTypesContext";
import { useLanguage } from "@/contexts/LanguageContext";

interface AddVaccinationDialogProps {
  onSuccess?: () => void;
}

const AddVaccinationDialog = ({ onSuccess }: AddVaccinationDialogProps) => {
  const [open, setOpen] = useState(false);
  const { vaccinationTypes, immunizationPlans } = useVaccinationTypes();
  const { t } = useLanguage();
  const [selectedType, setSelectedType] = useState<string>("");
  const [selectedImmunizationPlan, setSelectedImmunizationPlan] = useState<string>("");
  const [vaccinationDate, setVaccinationDate] = useState<Date>();
  const [doseOrderClaimed, setDoseOrderClaimed] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  // Filter immunization plans based on selected vaccine type
  const filteredImmunizationPlans = selectedType
    ? immunizationPlans.filter((plan) => plan.vaccineTypeId === selectedType)
    : [];

  const handleSubmit = async () => {
    if (!selectedType || !selectedImmunizationPlan || !vaccinationDate) {
      toast({
        variant: "destructive",
        title: "Missing information",
        description: "Please fill in all required fields.",
      });
      return;
    }

    // Get the age category ID from the selected immunization plan
    const selectedPlan = immunizationPlans.find(plan => plan.id === selectedImmunizationPlan);
    if (!selectedPlan) {
      toast({
        variant: "destructive",
        title: "Invalid selection",
        description: "Please select a valid immunization plan.",
      });
      return;
    }

    // Validate dose is a valid integer
    const doseNumber = doseOrderClaimed ? parseInt(doseOrderClaimed, 10) : null;
    if (doseOrderClaimed && (isNaN(doseNumber!) || doseNumber! < 1)) {
      toast({
        variant: "destructive",
        title: "Invalid dose number",
        description: "Vaccination dose must be a positive integer.",
      });
      return;
    }

    setLoading(true);
    
    const token = localStorage.getItem("auth_token");
    if (!token) {
      toast({
        variant: "destructive",
        title: "Not authenticated",
        description: "Please sign in to add vaccinations.",
      });
      setLoading(false);
      return;
    }

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-records`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          vaccineTypeId: selectedType,
          ageCategoryId: selectedPlan.ageCategoryId,
          administeredOn: format(vaccinationDate, "yyyy-MM-dd"),
          doseOrderClaimed: doseNumber,
        }),
      });

      setLoading(false);

      if (response.ok) {
        toast({
          title: "Vaccination added",
          description: "Your vaccination record has been saved.",
        });
        setOpen(false);
        setSelectedType("");
        setSelectedImmunizationPlan("");
        setVaccinationDate(undefined);
        setDoseOrderClaimed("");
        onSuccess?.();
      } else {
        const errorData = await response.json().catch(() => ({}));
        toast({
          variant: "destructive",
          title: "Error adding vaccination",
          description: errorData.message || "Failed to add vaccination record",
        });
      }
    } catch (error) {
      setLoading(false);
      toast({
        variant: "destructive",
        title: "Error adding vaccination",
        description: error instanceof Error ? error.message : "An error occurred",
      });
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          {t("addVaccination.button")}
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>{t("addVaccination.title")}</DialogTitle>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div className="space-y-2">
            <Label htmlFor="type">{t("addVaccination.type")}</Label>
            <Select 
              value={selectedType} 
              onValueChange={(value) => {
                setSelectedType(value);
                setSelectedImmunizationPlan(""); // Reset immunization plan when vaccine type changes
              }}
            >
              <SelectTrigger>
                <SelectValue placeholder={t("addVaccination.type.placeholder")} />
              </SelectTrigger>
              <SelectContent>
                {vaccinationTypes.map((type) => (
                  <SelectItem key={type.id} value={type.id}>
                    {type.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="immunization-plan">{t("addVaccination.plan")}</Label>
            <Select 
              value={selectedImmunizationPlan} 
              onValueChange={setSelectedImmunizationPlan}
              disabled={!selectedType}
            >
              <SelectTrigger>
                <SelectValue placeholder={selectedType ? t("addVaccination.plan.placeholder") : t("addVaccination.type.placeholder")} />
              </SelectTrigger>
              <SelectContent>
                {filteredImmunizationPlans.map((plan) => (
                  <SelectItem key={plan.id} value={plan.id}>
                    {plan.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>{t("addVaccination.date")}</Label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className={cn(
                    "w-full justify-start text-left font-normal",
                    !vaccinationDate && "text-muted-foreground"
                  )}
                >
                  <CalendarIcon className="mr-2 h-4 w-4" />
                  {vaccinationDate ? format(vaccinationDate, "PPP") : t("addVaccination.date")}
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  mode="single"
                  selected={vaccinationDate}
                  onSelect={setVaccinationDate}
                  initialFocus
                  className="pointer-events-auto"
                />
              </PopoverContent>
            </Popover>
          </div>

          <div className="space-y-2">
            <Label htmlFor="dose">{t("addVaccination.dose")}</Label>
            <Input
              id="dose"
              type="number"
              min="1"
              step="1"
              placeholder={t("addVaccination.dose.placeholder")}
              value={doseOrderClaimed}
              onChange={(e) => {
                const value = e.target.value;
                // Only allow positive integers
                if (value === "" || /^\d+$/.test(value)) {
                  setDoseOrderClaimed(value);
                }
              }}
            />
          </div>
        </div>

        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={() => setOpen(false)}>
            {t("addVaccination.cancel")}
          </Button>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? t("addVaccination.saving") : t("addVaccination.button")}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default AddVaccinationDialog;
