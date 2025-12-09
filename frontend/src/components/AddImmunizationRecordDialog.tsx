import { useState, useMemo, useEffect } from "react";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useLanguage } from "@/contexts/LanguageContext";
import { useVaccineTypes, useImmunizationPlans } from "@/hooks/useVaccineTypes";
import { useCreateVaccination } from "@/hooks/useVaccinations";
import { Plus } from "lucide-react";
import RequiredIndicator from "./form/required-indicator";
import { LoadingButton } from "./form/loading-button";

interface AddImmunizationRecordDialogProps {
  onSuccess?: () => void;
}

const formSchema = z.object({
  vaccineTypeId: z.string().min(1, "validation.required"),
  immunizationPlanId: z.string().min(1, "validation.required"),
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

type FormData = z.infer<typeof formSchema>;

const AddImmunizationRecordDialog = ({
  onSuccess,
}: AddImmunizationRecordDialogProps) => {
  const { t } = useLanguage();
  const [open, setOpen] = useState(false);

  const { data: vaccineTypes, isLoading: isLoadingVaccineTypes } =
    useVaccineTypes();
  const { data: immunizationPlans, isLoading: isLoadingPlans } =
    useImmunizationPlans();
  const createMutation = useCreateVaccination();

  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      vaccineTypeId: undefined,
      immunizationPlanId: undefined,
      administeredOn: new Date(),
      doseOrderClaimed: undefined,
    },
  });

  const selectedVaccineTypeId = form.watch("vaccineTypeId");
  const filteredImmunizationPlans = useMemo(() => {
    if (!selectedVaccineTypeId || !immunizationPlans) return [];
    return immunizationPlans.filter(
      (plan) => plan.vaccineTypeId === selectedVaccineTypeId
    );
  }, [selectedVaccineTypeId, immunizationPlans]);

  // Reset immunization plan when vaccine type changes
  useEffect(() => {
    form.setValue("immunizationPlanId", "");
  }, [selectedVaccineTypeId, form]);

  function handleSubmit(data: FormData) {
    createMutation.mutate(
      {
        vaccineTypeId: data.vaccineTypeId,
        ageCategoryId: data.immunizationPlanId,
        administeredOn: data.administeredOn,
        doseOrderClaimed: data.doseOrderClaimed,
      },
      {
        onSuccess: () => {
          setOpen(false);
          form.reset();
          onSuccess?.();
        },
      }
    );
  }

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
        <form
          id="add-vaccination-form"
          onSubmit={form.handleSubmit(handleSubmit)}
        >
          <FieldGroup>
            <Controller
              name="vaccineTypeId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="vaccine-type">
                    {t("addVaccination.type")} <RequiredIndicator />
                  </FieldLabel>
                  <Select
                    name={field.name}
                    value={field.value}
                    onValueChange={field.onChange}
                  >
                    <SelectTrigger
                      id="vaccine-type"
                      aria-invalid={fieldState.invalid}
                    >
                      <SelectValue
                        placeholder={t("addVaccination.type.placeholder")}
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {isLoadingVaccineTypes ? (
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
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />

            <Controller
              name="immunizationPlanId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="immunization-plan">
                    {t("addVaccination.plan")} <RequiredIndicator />
                  </FieldLabel>
                  <Select
                    name={field.name}
                    value={field.value}
                    onValueChange={field.onChange}
                    disabled={!selectedVaccineTypeId}
                  >
                    <SelectTrigger
                      id="immunization-plan"
                      aria-invalid={fieldState.invalid}
                    >
                      <SelectValue
                        placeholder={t("addVaccination.plan.placeholder")}
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {isLoadingPlans ? (
                        <div className="p-2 text-sm text-muted-foreground">
                          {t("dashboard.loading")}
                        </div>
                      ) : filteredImmunizationPlans.length === 0 ? (
                        <div className="p-2 text-sm text-muted-foreground">
                          {t("addVaccination.plan.empty")}
                        </div>
                      ) : (
                        filteredImmunizationPlans.map((plan) => (
                          <SelectItem key={plan.id} value={plan.id}>
                            {plan.name}
                          </SelectItem>
                        ))
                      )}
                    </SelectContent>
                  </Select>
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />

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
                  <FieldLabel htmlFor="dose-order">
                    {t("addVaccination.dose")} <RequiredIndicator />
                  </FieldLabel>
                  <Input
                    {...field}
                    id="dose-order"
                    type="number"
                    min="1"
                    aria-invalid={fieldState.invalid}
                    placeholder={t("addVaccination.dose.placeholder")}
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
              onClick={() => {
                setOpen(false);
                form.reset();
              }}
            >
              {t("dashboard.cancel")}
            </Button>

            <LoadingButton
              type="submit"
              form="add-vaccination-form"
              loading={createMutation.isPending}
            >
              {t("dashboard.save")}
            </LoadingButton>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default AddImmunizationRecordDialog;
