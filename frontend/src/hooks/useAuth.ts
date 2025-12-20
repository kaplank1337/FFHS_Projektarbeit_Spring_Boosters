import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { successToast, apiErrorToast } from "@/lib/toast-extension";
import {
  authService,
  type LoginRequest,
  type RegisterRequest,
} from "@/api/auth.service";
import { useLanguage } from "@/contexts/LanguageContext";

export const useLogin = () => {
  const navigate = useNavigate();
  const { t } = useLanguage();

  return useMutation({
    mutationFn: (credentials: LoginRequest) => authService.login(credentials),
    onSuccess: (data) => {
      const token = data.token || data.accessToken || data.jwt;
      if (token) {
        authService.setToken(token);
        navigate("/dashboard");
      } else {
        apiErrorToast(t, { message: "Kein Token vom Server erhalten" });
      }
    },
    onError: (error) => {
      apiErrorToast(t, error);
    },
  });
};

export const useRegister = () => {
  const { t } = useLanguage();

  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: () => {
      successToast(
        t,
        "Account erfolgreich erstellt! Du kannst dich jetzt anmelden."
      );
    },
    onError: (error) => {
      apiErrorToast(t, error);
    },
  });
};

export const useLogout = () => {
  const navigate = useNavigate();

  return () => {
    authService.logout();
    navigate("/auth");
  };
};
