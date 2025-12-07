import { apiClient } from "@/lib/api-client";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token?: string;
  accessToken?: string;
  jwt?: string;
}

export interface RegisterRequest {
  username: string;
  firstName: string;
  lastName: string;
  birthDate: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  message?: string;
}

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    return apiClient.post<LoginResponse>("/api/v1/auth/login", credentials);
  },

  register: async (data: RegisterRequest): Promise<RegisterResponse> => {
    return apiClient.post<RegisterResponse>("/api/v1/auth/register", data);
  },

  logout: () => {
    localStorage.removeItem("auth_token");
  },

  getToken: (): string | null => {
    return localStorage.getItem("auth_token");
  },

  setToken: (token: string) => {
    localStorage.setItem("auth_token", token);
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem("auth_token");
  },
};
