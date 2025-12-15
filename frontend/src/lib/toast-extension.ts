import { toast } from "sonner";
import type { ApiError } from "@/api/api-client";

type TranslationFunction = (key: string) => string;

const successToast = (t: TranslationFunction, message?: string) => {
  toast(t("toast.success.title"), {
    description: message || t("toast.success.description"),
    duration: 2000,
  });
};

const errorToast = (t: TranslationFunction, message: string) => {
  toast.error(t("toast.error.title"), {
    description: message,
    duration: 4000,
  });
};

const apiErrorToast = (t: TranslationFunction, error: unknown) => {
  const apiError = error as ApiError;
  const message = apiError?.message || t("toast.error.unknown");

  toast.error(t("toast.error.title"), {
    description: message,
    duration: 4000,
  });
};

const loadingToast = (message: string) => {
  return toast.loading(message, {
    duration: Infinity,
  });
};

const dismissToast = (toastId: string | number) => {
  toast.dismiss(toastId);
};

export { successToast, errorToast, apiErrorToast, loadingToast, dismissToast };
