import { useState } from "react";
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
import { useCreateVaccination } from "@/hooks/useVaccinations";
import { Plus } from "lucide-react";
import { formatDate } from "@/lib/date-utils";

interface AddVaccinationDialogProps {
  onSuccess?: () => void;
}

const AddVaccinationDialog = ({ onSuccess }: AddVaccinationDialogProps) => {
  const { t } = useLanguage();
  const [open, setOpen] = useState(false);
  const [vaccineTypeId, setVaccineTypeId] = useState("");
  const [administeredOn, setAdministeredOn] = useState(
    formatDate(new Date(), "yyyy-MM-dd")
  );
  const [doseOrderClaimed, setDoseOrderClaimed] = useState("");

  const { data: vaccineTypes, isLoading } = useVaccineTypes();
  const createMutation = useCreateVaccination();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!vaccineTypeId || !administeredOn) {
      return;
    }

    createMutation.mutate(
      {
        vaccineTypeId,
        administeredOn,
        doseOrderClaimed: doseOrderClaimed ? parseInt(doseOrderClaimed) : undefined,
      },
      {
        onSuccess: () => {
          setOpen(false);
          setVaccineTypeId("");
          setAdministeredOn(formatDate(new Date(), "yyyy-MM-dd"));
          setDoseOrderClaimed("");
          onSuccess?.();
        },
      }
    );
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <Button onClick={() => setOpen(true)}>
        <Plus className="h-4 w-4 mr-2" />
        {t("dashboard.addVaccination")}
      </Button>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t("dashboard.addVaccination")}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="vaccine-type">{t("dashboard.vaccineType")}</Label>
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
            <Label htmlFor="administered-on">{t("dashboard.administeredOn")}</Label>
            <Input
              id="administered-on"
              type="date"
              value={administeredOn}
              onChange={(e) => setAdministeredOn(e.target.value)}
              required
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="dose-order">{t("dashboard.doseOrder")}</Label>
            <Input
              id="dose-order"
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
              onClick={() => setOpen(false)}
            >
              {t("dashboard.cancel")}
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending ? t("dashboard.saving") : t("dashboard.save")}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default AddVaccinationDialog;
