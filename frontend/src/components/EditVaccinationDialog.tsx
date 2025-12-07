import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useLanguage } from "@/contexts/LanguageContext";
import { useVaccineTypes } from "@/hooks/useVaccineTypes";
import { useUpdateVaccination } from "@/hooks/useVaccinations";
import { formatDate } from "@/lib/date-utils";

interface EditVaccinationDialogProps {
  vaccination: any;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

const EditVaccinationDialog = ({
  vaccination,
  open,
  onOpenChange,
  onSuccess,
}: EditVaccinationDialogProps) => {
  const { t } = useLanguage();
  const [vaccineTypeId, setVaccineTypeId] = useState("");
  const [administeredOn, setAdministeredOn] = useState("");
  const [doseOrderClaimed, setDoseOrderClaimed] = useState("");

  const { data: vaccineTypes, isLoading } = useVaccineTypes();
  const updateMutation = useUpdateVaccination();

  useEffect(() => {
    if (vaccination) {
      setVaccineTypeId(vaccination.vaccineTypeId || "");
      setAdministeredOn(
        vaccination.administeredOn
          ? formatDate(new Date(vaccination.administeredOn), "yyyy-MM-dd")
          : ""
      );
      setDoseOrderClaimed(
        vaccination.doseOrderClaimed?.toString() || ""
      );
    }
  }, [vaccination]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!vaccination?.id) return;

    updateMutation.mutate(
      {
        id: vaccination.id,
        data: {
          vaccineTypeId: vaccineTypeId || undefined,
          administeredOn: administeredOn || undefined,
          doseOrderClaimed: doseOrderClaimed ? parseInt(doseOrderClaimed) : undefined,
        },
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          onSuccess?.();
        },
      }
    );
  };

  if (!vaccination) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t("dashboard.editVaccination")}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="edit-vaccine-type">{t("dashboard.vaccineType")}</Label>
            <Select value={vaccineTypeId} onValueChange={setVaccineTypeId}>
              <SelectTrigger>
                <SelectValue placeholder={t("dashboard.selectVaccineType")} />
              </SelectTrigger>
              <SelectContent>
                {isLoading ? (
                  <div className="p-2 text-sm text-muted-foreground">
                    {t("dashboard.loading")}
                  </div>
                ) : (
                  vaccineTypes?.map((type) => (
                    <SelectItem key={type.id} value={type.id}>
                      {type.name}
                    </SelectItem>
                  ))
                )}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-administered-on">
              {t("dashboard.administeredOn")}
            </Label>
            <Input
              id="edit-administered-on"
              type="date"
              value={administeredOn}
              onChange={(e) => setAdministeredOn(e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="edit-dose-order">{t("dashboard.doseOrder")}</Label>
            <Input
              id="edit-dose-order"
              type="number"
              min="1"
              value={doseOrderClaimed}
              onChange={(e) => setDoseOrderClaimed(e.target.value)}
              placeholder={t("dashboard.optional")}
            />
          </div>

          <div className="flex justify-end gap-2">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              {t("dashboard.cancel")}
            </Button>
            <Button type="submit" disabled={updateMutation.isPending}>
              {updateMutation.isPending
                ? t("dashboard.saving")
                : t("dashboard.save")}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default EditVaccinationDialog;
