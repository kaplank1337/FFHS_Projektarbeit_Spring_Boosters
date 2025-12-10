import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import { Syringe } from "lucide-react";
import { LoginForm } from "@/pages/auth/components/LoginForm";
import { RegisterForm } from "@/pages/auth/components/RegisterForm";
import {
  useVaccineTypes as useVaccineTypesQuery,
  useImmunizationPlans,
} from "@/hooks/useVaccineTypes";

const Auth = () => {
  const { t } = useLanguage();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState(
    searchParams.get("tab") === "signup" ? "signup" : "signin"
  );

  const { refetch: refetchVaccineTypes } = useVaccineTypesQuery();
  const { refetch: refetchImmunizationPlans } = useImmunizationPlans();

  useEffect(() => {
    const token = localStorage.getItem("auth_token");
    if (token) {
      navigate("/dashboard");
    }
  }, [navigate]);

  const handleLoginSuccess = async () => {
    await Promise.all([refetchVaccineTypes(), refetchImmunizationPlans()]);
  };

  const handleRegisterSuccess = () => {
    setActiveTab("signin");
  };

  return (
    <div className="min-h-screen bg-background">
      <Header />
      <div className="container flex items-center justify-center py-16">
        <div className="w-full max-w-md">
          <div className="flex flex-col items-center mb-8">
            <div className="flex items-center justify-center w-16 h-16 rounded-full bg-primary/10 mb-4">
              <Syringe className="h-8 w-8 text-primary" />
            </div>
            <h1 className="text-2xl md:text-3xl font-bold whitespace-nowrap">
              {t("auth.welcome")}
            </h1>
            <p className="text-muted-foreground mt-2 text-center">
              {t("auth.subtitle")}
            </p>
          </div>

          <Tabs value={activeTab} onValueChange={setActiveTab}>
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="signin">{t("auth.signin")}</TabsTrigger>
              <TabsTrigger value="signup">{t("auth.signup")}</TabsTrigger>
            </TabsList>

            <TabsContent value="signin">
              <Card>
                <CardHeader>
                  <CardTitle>{t("auth.signin.title")}</CardTitle>
                  <CardDescription>
                    {t("auth.signin.description")}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <LoginForm onSuccess={handleLoginSuccess} />
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="signup">
              <Card>
                <CardHeader>
                  <CardTitle>{t("auth.signup.title")}</CardTitle>
                  <CardDescription>
                    {t("auth.signup.description")}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <RegisterForm onSuccess={handleRegisterSuccess} />
                </CardContent>
              </Card>
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  );
};

export default Auth;
