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
import { useLogin } from "@/hooks/useAuth";
import RequiredIndicator from "@/components/form/required-indicator";
import { LoadingButton } from "@/components/form/loading-button";

const formSchema = z.object({
  username: z.string("validation.required"),
  password: z.string("validation.required"),
});

type FormData = z.infer<typeof formSchema>;

interface LoginFormProps {
  onSuccess?: () => void;
}

export function LoginForm({ onSuccess }: LoginFormProps) {
  const { t } = useLanguage();
  const loginMutation = useLogin();

  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      username: undefined,
      password: undefined,
    },
  });

  function handleSubmit(data: FormData) {
    loginMutation.mutate(data, {
      onSuccess: () => {
        form.reset();
        onSuccess?.();
      },
    });
  }

  return (
    <form
      id="login-form"
      onSubmit={form.handleSubmit(handleSubmit)}
      className="space-y-4"
    >
      <FieldGroup>
        <Controller
          name="username"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor="signin-username">
                {t("auth.username")} <RequiredIndicator />
              </FieldLabel>
              <Input
                {...field}
                id="signin-username"
                type="text"
                placeholder="neil.nasa"
                aria-invalid={fieldState.invalid}
                autoComplete="username"
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
              <FieldLabel htmlFor="signin-password">
                {t("auth.password")} <RequiredIndicator />
              </FieldLabel>
              <Input
                {...field}
                id="signin-password"
                type="password"
                aria-invalid={fieldState.invalid}
                autoComplete="current-password"
              />
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
      </FieldGroup>

      <LoadingButton
        type="submit"
        form="login-form"
        className="w-full"
        loading={loginMutation.isPending}
      >
        {t("auth.signin.button")}
      </LoadingButton>
    </form>
  );
}
