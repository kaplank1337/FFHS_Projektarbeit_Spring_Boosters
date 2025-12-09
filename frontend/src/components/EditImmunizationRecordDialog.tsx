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
import RequiredIndicator from "./form/required-indicator";
import { LoadingButton } from "./form/loading-button";
import type { ImmunizationRecordDto } from "@/services/vaccinations.service";

interface EditImmunizationRecordDialogProps {
  vaccination: ImmunizationRecordDto;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

const formSchema = z.object({
  vaccineName: z.string(),
  administeredOn: z.date("validation.required").refine(
    (date) => {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return date <= today;
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
      vaccineName: vaccination.vaccineName,
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
          administeredOn: data.administeredOn,
          doseOrderClaimed: data.doseOrderClaimed,
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
            <Controller
              name="vaccineName"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor="vaccine-type-display">
                    {t("dashboard.vaccineType")} <RequiredIndicator />
                  </FieldLabel>
                  <Input
                    id="vaccine-type-display"
                    value={field.value}
                    disabled
                  />
                </Field>
              )}
            />

            <Controller
              name="administeredOn"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="edit-administered-on">
                    {t("dashboard.administeredOn")} <RequiredIndicator />
                  </FieldLabel>
                  <DatePicker
                    date={field.value}
                    onSelect={field.onChange}
                    placeholder={t("dashboard.selectDate")}
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
