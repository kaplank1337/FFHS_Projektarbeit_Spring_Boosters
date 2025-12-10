import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import * as z from "zod";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { DatePicker } from "@/components/ui/date-picker";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { useLanguage } from "@/contexts/LanguageContext";
import { useUpdateVaccination } from "@/hooks/useVaccinations";
import RequiredIndicator from "../../../components/form/required-indicator";
import { LoadingButton } from "../../../components/form/loading-button";
import type { ImmunizationRecordDto } from "@/api/vaccinations.service";

interface EditImmunizationRecordDialogProps {
  vaccination: ImmunizationRecordDto;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

const formSchema = z.object({
  administeredOn: z.date("validation.required").refine(
    (date) => {
      const selectedDate = new Date(date);
      selectedDate.setHours(0, 0, 0, 0);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return selectedDate <= today;
    },
    { message: "validation.invalidDate" }
  ),
  doseOrderClaimed: z.coerce
    .number("validation.required")
    .min(1, "validation.positiveNumber"),
});

const EditImmunizationRecordDialog = ({
  vaccination,
  open,
  onOpenChange,
  onSuccess,
}: EditImmunizationRecordDialogProps) => {
  const { t } = useLanguage();
  const updateMutation = useUpdateVaccination();

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      administeredOn: vaccination.administeredOn,
      doseOrderClaimed: vaccination.doseOrderClaimed,
    },
  });

  useEffect(() => {
    if (vaccination) {
      form.reset({
        administeredOn: vaccination.administeredOn
          ? new Date(vaccination.administeredOn)
          : new Date(),
        doseOrderClaimed: vaccination.doseOrderClaimed,
      });
    }
  }, [vaccination, form]);

  function handleSubmit(data: z.infer<typeof formSchema>) {
    if (!vaccination?.id) return;

    updateMutation.mutate(
      {
        id: vaccination.id,
        data: {
          id: vaccination.id,
          doseOrderClaimed: data.doseOrderClaimed,
          administeredOn: new Date(data.administeredOn.setHours(12, 0, 0, 0)),
        },
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          onSuccess?.();
        },
      }
    );
  }

  if (!vaccination) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{t("dashboard.editVaccination")}</DialogTitle>
        </DialogHeader>
        <form
          id="edit-vaccination-form"
          onSubmit={form.handleSubmit(handleSubmit)}
        >
          <FieldGroup>
            <Field>
              <FieldLabel>{t("dashboard.vaccineType")}</FieldLabel>
              <div className="flex items-center gap-3 p-3 bg-muted/50 border border-muted rounded-md cursor-not-allowed">
                <span className="text-sm font-medium text-foreground">
                  {vaccination.vaccineName}
                </span>
              </div>
            </Field>

            <Controller
              name="administeredOn"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="administered-on">
                    {t("addVaccination.date")} <RequiredIndicator />
                  </FieldLabel>
                  <DatePicker
                    date={field.value}
                    onSelect={field.onChange}
                    placeholder={t("addVaccination.date.placeholder")}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />

            <Controller
              name="doseOrderClaimed"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="edit-dose-order">
                    {t("dashboard.doseOrder")} <RequiredIndicator />
                  </FieldLabel>
                  <Input
                    {...field}
                    id="edit-dose-order"
                    type="number"
                    min="1"
                    aria-invalid={fieldState.invalid}
                    placeholder={t("dashboard.optional")}
                  />
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />
          </FieldGroup>

          <div className="flex justify-end gap-2 mt-6">
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              {t("dashboard.cancel")}
            </Button>

            <LoadingButton
              type="submit"
              form="edit-vaccination-form"
              loading={updateMutation.isPending}
            >
              {t("dashboard.save")}
            </LoadingButton>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default EditImmunizationRecordDialog;
