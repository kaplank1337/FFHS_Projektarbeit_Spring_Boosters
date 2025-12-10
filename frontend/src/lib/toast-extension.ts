import { toast } from "sonner";
import type { ApiError } from "@/api/api-client";

const successToast = (message?: string) => {
  toast("Super! Das hat funktioniert. 🎉", {
    description: message || "Du bist on fire! 🔥",
    duration: 2000,
  });
};

const errorToast = (message: string) => {
  toast.error("Uups! Es ist ein Fehler aufgetreten.", {
    description: message,
    duration: 4000,
  });
};

const apiErrorToast = (error: unknown) => {
  const apiError = error as ApiError;
  const message = apiError?.message || "Ein unbekannter Fehler ist aufgetreten";

  toast.error("Uups! Es ist ein Fehler aufgetreten.", {
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
