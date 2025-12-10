import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { successToast, apiErrorToast } from "@/lib/toast-extension";
import {
  authService,
  type LoginRequest,
  type RegisterRequest,
} from "@/api/auth.service";

export const useLogin = () => {
  const navigate = useNavigate();

  return useMutation({
    mutationFn: (credentials: LoginRequest) => authService.login(credentials),
    onSuccess: (data) => {
      const token = data.token || data.accessToken || data.jwt;
      if (token) {
        authService.setToken(token);
        navigate("/dashboard");
      } else {
        apiErrorToast({ message: "Kein Token vom Server erhalten" });
      }
    },
    onError: (error) => {
      apiErrorToast(error);
    },
  });
};

export const useRegister = () => {
  return useMutation({
    mutationFn: (data: RegisterRequest) => authService.register(data),
    onSuccess: () => {
      successToast(
        "Account erfolgreich erstellt! Du kannst dich jetzt anmelden."
      );
    },
    onError: (error) => {
      apiErrorToast(error);
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
