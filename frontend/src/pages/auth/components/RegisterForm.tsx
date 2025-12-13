import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import * as z from "zod";
import { Input } from "@/components/ui/input";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { useLanguage } from "@/contexts/LanguageContext";
import { useRegister } from "@/hooks/useAuth";
import RequiredIndicator from "@/components/form/required-indicator";
import { LoadingButton } from "@/components/form/loading-button";
import { DatePicker } from "../../../components/ui/date-picker";

const formSchema = z.object({
  username: z.string("validation.required"),
  firstName: z.string("validation.required"),
  lastName: z.string("validation.required"),
  birthDate: z.date("validation.required"),
  email: z.string("validation.email"),
  password: z
    .string("validation.required")
    .min(6, "validation.passwordMinLength"),
});

type FormData = z.infer<typeof formSchema>;

interface RegisterFormProps {
  onSuccess?: () => void;
}

export function RegisterForm({ onSuccess }: RegisterFormProps) {
  const { t } = useLanguage();
  const registerMutation = useRegister();

  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: undefined,
      firstName: undefined,
      lastName: undefined,
      birthDate: undefined,
      email: undefined,
      password: undefined,
    },
  });

  function handleSubmit(data: FormData) {
    registerMutation.mutate(data, {
      onSuccess: () => {
        form.reset();
        onSuccess?.();
      },
    });
  }

  return (
    <form
      id="register-form"
      onSubmit={form.handleSubmit(handleSubmit)}
      className="space-y-4"
    >
      <FieldGroup>
        <Controller
          name="username"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor="signup-username">
                {t("auth.username")} <RequiredIndicator />
              </FieldLabel>
              <Input
                {...field}
                id="signup-username"
                type="text"
                placeholder="neil.nasa"
                aria-invalid={fieldState.invalid}
                autoComplete="username"
              />
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />

        <div className="grid grid-cols-2 gap-4">
          <Controller
            name="firstName"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <FieldLabel htmlFor="signup-firstname">
                  {t("auth.firstName")} <RequiredIndicator />
                </FieldLabel>
                <Input
                  {...field}
                  id="signup-firstname"
                  type="text"
                  placeholder="Neil"
                  aria-invalid={fieldState.invalid}
                  autoComplete="given-name"
                />
                {fieldState.invalid && (
                  <FieldError errors={[fieldState.error]} />
                )}
              </Field>
            )}
          />

          <Controller
            name="lastName"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <FieldLabel htmlFor="signup-lastname">
                  {t("auth.lastName")} <RequiredIndicator />
                </FieldLabel>
                <Input
                  {...field}
                  id="signup-lastname"
                  type="text"
                  placeholder="Armstrong"
                  aria-invalid={fieldState.invalid}
                  autoComplete="family-name"
                />
                {fieldState.invalid && (
                  <FieldError errors={[fieldState.error]} />
                )}
              </Field>
            )}
          />
        </div>

        <Controller
          name="birthDate"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor="signup-birthdate">
                {t("auth.birthDate")} <RequiredIndicator />
              </FieldLabel>
              <DatePicker
                date={field.value}
                onSelect={field.onChange}
                placeholder={t("auth.birthDate")}
                aria-invalid={fieldState.invalid}
              />
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />

        <Controller
          name="email"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor="signup-email">
                {t("auth.email")} <RequiredIndicator />
              </FieldLabel>
              <Input
                {...field}
                id="signup-email"
                type="email"
                placeholder="neil.armstrong@nasa.gov"
                aria-invalid={fieldState.invalid}
                autoComplete="email"
              />
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />

        <Controller
          name="password"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor="signup-password">
                {t("auth.password")} <RequiredIndicator />
              </FieldLabel>
              <Input
                {...field}
                id="signup-password"
                type="password"
                aria-invalid={fieldState.invalid}
                autoComplete="new-password"
              />
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
      </FieldGroup>

      <LoadingButton
        type="submit"
        form="register-form"
        className="w-full"
        loading={registerMutation.isPending}
      >
        {t("auth.signup.button")}
      </LoadingButton>
    </form>
  );
}
