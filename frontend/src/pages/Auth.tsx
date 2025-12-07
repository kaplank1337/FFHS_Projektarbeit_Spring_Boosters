import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import { Syringe } from "lucide-react";
import { useLogin, useRegister } from "@/hooks/useAuth";
import {
  useVaccineTypes as useVaccineTypesQuery,
  useImmunizationPlans,
} from "@/hooks/useVaccineTypes";

const Auth = () => {
  const { t } = useLanguage();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // Sign in state
  const [signinUsername, setSigninUsername] = useState("");
  const [signinPassword, setSigninPassword] = useState("");

  // Sign up state
  const [username, setUsername] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupPassword, setSignupPassword] = useState("");

  const [activeTab, setActiveTab] = useState(
    searchParams.get("tab") === "signup" ? "signup" : "signin"
  );

  const loginMutation = useLogin();
  const registerMutation = useRegister();

  const { refetch: refetchVaccineTypes } = useVaccineTypesQuery();
  const { refetch: refetchImmunizationPlans } = useImmunizationPlans();

  useEffect(() => {
    const token = localStorage.getItem("auth_token");
    if (token) {
      navigate("/dashboard");
    }
  }, [navigate]);

  const handleSignIn = async (e: React.FormEvent) => {
    e.preventDefault();

    loginMutation.mutate(
      {
        username: signinUsername,
        password: signinPassword,
      },
      {
        onSuccess: async () => {
          await Promise.all([
            refetchVaccineTypes(),
            refetchImmunizationPlans(),
          ]);
        },
      }
    );
  };

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();

    registerMutation.mutate(
      {
        username,
        firstName,
        lastName,
        birthDate,
        email: signupEmail,
        password: signupPassword,
      },
      {
        onSuccess: () => {
          // Reset form
          setUsername("");
          setFirstName("");
          setLastName("");
          setBirthDate("");
          setSignupEmail("");
          setSignupPassword("");

          // Switch to sign in tab
          setActiveTab("signin");
        },
      }
    );
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
                  <form onSubmit={handleSignIn} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="signin-username">
                        {t("auth.username")}
                      </Label>
                      <Input
                        id="signin-username"
                        type="text"
                        placeholder="bruceWayne"
                        value={signinUsername}
                        onChange={(e) => setSigninUsername(e.target.value)}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="signin-password">
                        {t("auth.password")}
                      </Label>
                      <Input
                        id="signin-password"
                        type="password"
                        value={signinPassword}
                        onChange={(e) => setSigninPassword(e.target.value)}
                        required
                      />
                    </div>
                    <Button
                      type="submit"
                      className="w-full"
                      disabled={loginMutation.isPending}
                    >
                      {loginMutation.isPending
                        ? t("auth.signin.loading")
                        : t("auth.signin.button")}
                    </Button>
                  </form>
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
                  <form onSubmit={handleSignUp} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="signup-username">
                        {t("auth.username")}
                      </Label>
                      <Input
                        id="signup-username"
                        type="text"
                        placeholder="johndoe"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                      />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="signup-firstname">
                          {t("auth.firstName")}
                        </Label>
                        <Input
                          id="signup-firstname"
                          type="text"
                          placeholder="John"
                          value={firstName}
                          onChange={(e) => setFirstName(e.target.value)}
                          required
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="signup-lastname">
                          {t("auth.lastName")}
                        </Label>
                        <Input
                          id="signup-lastname"
                          type="text"
                          placeholder="Doe"
                          value={lastName}
                          onChange={(e) => setLastName(e.target.value)}
                          required
                        />
                      </div>
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="signup-birthdate">
                        {t("auth.birthDate")}
                      </Label>
                      <Input
                        id="signup-birthdate"
                        type="date"
                        value={birthDate}
                        onChange={(e) => setBirthDate(e.target.value)}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="signup-email">{t("auth.email")}</Label>
                      <Input
                        id="signup-email"
                        type="email"
                        placeholder="bruce.wayne@example.com"
                        value={signupEmail}
                        onChange={(e) => setSignupEmail(e.target.value)}
                        required
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="signup-password">
                        {t("auth.password")}
                      </Label>
                      <Input
                        id="signup-password"
                        type="password"
                        value={signupPassword}
                        onChange={(e) => setSignupPassword(e.target.value)}
                        required
                        minLength={6}
                      />
                    </div>
                    <Button
                      type="submit"
                      className="w-full"
                      disabled={registerMutation.isPending}
                    >
                      {registerMutation.isPending
                        ? t("auth.signup.loading")
                        : t("auth.signup.button")}
                    </Button>
                  </form>
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
