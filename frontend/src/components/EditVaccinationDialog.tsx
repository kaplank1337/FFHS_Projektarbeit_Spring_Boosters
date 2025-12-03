import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { CalendarIcon } from "lucide-react";
import { format } from "date-fns";
import { cn } from "@/lib/utils";
import { useToast } from "@/hooks/use-toast";
import { useLanguage } from "@/contexts/LanguageContext";
import { useVaccinationTypes } from "@/contexts/VaccinationTypesContext";

interface Vaccination {
  id: string;
  administeredOn: string;
  doseOrderClaimed: number | null;
  vaccineName?: string;
  createdAt: string;
}

interface EditVaccinationDialogProps {
  vaccination: Vaccination | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

const EditVaccinationDialog = ({ vaccination, open, onOpenChange, onSuccess }: EditVaccinationDialogProps) => {
  const { t } = useLanguage();
  const { vaccinationTypes } = useVaccinationTypes();
  const [vaccinationDate, setVaccinationDate] = useState<Date>();
  const [doseOrderClaimed, setDoseOrderClaimed] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  useEffect(() => {
    if (vaccination) {
      setVaccinationDate(new Date(vaccination.administeredOn));
      setDoseOrderClaimed(vaccination.doseOrderClaimed?.toString() || "");
    }
  }, [vaccination]);

  const handleSubmit = async () => {
    if (!vaccination || !vaccinationDate) {
      toast({
        variant: "destructive",
        title: t("editVaccination.error.missingInfo"),
        description: t("editVaccination.error.missingInfoDesc"),
      });
      return;
    }

    const doseNumber = doseOrderClaimed ? parseInt(doseOrderClaimed, 10) : null;
    if (doseOrderClaimed && (isNaN(doseNumber!) || doseNumber! < 1)) {
      toast({
        variant: "destructive",
        title: t("editVaccination.error.invalidDose"),
        description: t("editVaccination.error.invalidDoseDesc"),
      });
      return;
    }

    setLoading(true);
    
    const token = localStorage.getItem("auth_token");
    if (!token) {
      toast({
        variant: "destructive",
        title: t("editVaccination.error.notAuth"),
        description: t("editVaccination.error.notAuthDesc"),
      });
      setLoading(false);
      return;
    }

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    // Find vaccineTypeId from vaccine types based on vaccine name
    const vaccineType = vaccinationTypes.find(vt => vt.name === vaccination.vaccineName);
    if (!vaccineType) {
      toast({
        variant: "destructive",
        title: t("editVaccination.error.failed"),
        description: t("editVaccination.error.failedDesc"),
      });
      setLoading(false);
      return;
    }
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-records/${vaccination.id}`, {
        method: "PATCH",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          id: vaccination.id,
          vaccineTypeId: vaccineType.id,
          administeredOn: format(vaccinationDate, "yyyy-MM-dd"),
          doseOrderClaimed: doseNumber,
        }),
      });

      setLoading(false);

      if (response.ok) {
        toast({
          title: t("editVaccination.success"),
        });
        onOpenChange(false);
        onSuccess?.();
      } else {
        const errorData = await response.json().catch(() => ({}));
        toast({
          variant: "destructive",
          title: t("editVaccination.error.failed"),
          description: errorData.message || t("editVaccination.error.failedDesc"),
        });
      }
    } catch (error) {
      setLoading(false);
      toast({
        variant: "destructive",
        title: t("editVaccination.error.failed"),
        description: error instanceof Error ? error.message : t("editVaccination.error.failedDesc"),
      });
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>{t("editVaccination.title")}</DialogTitle>
        </DialogHeader>
        <div className="space-y-4 py-4">
          {vaccination?.vaccineName && (
            <div className="space-y-2">
              <Label>{t("editVaccination.vaccineName")}</Label>
              <p className="text-sm font-medium p-2 bg-muted rounded-md">{vaccination.vaccineName}</p>
            </div>
          )}

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
                if (value === "" || /^\d+$/.test(value)) {
                  setDoseOrderClaimed(value);
                }
              }}
            />
          </div>
        </div>

        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            {t("addVaccination.cancel")}
          </Button>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? t("editVaccination.saving") : t("editVaccination.save")}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default EditVaccinationDialog;
