import { useState, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";
import { useVaccinationTypes } from "@/contexts/VaccinationTypesContext";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import { Syringe } from "lucide-react";

const Auth = () => {
  const { fetchVaccinationTypes, fetchImmunizationPlans } = useVaccinationTypes();
  const { t } = useLanguage();
  const [searchParams] = useSearchParams();
  const [signinUsername, setSigninUsername] = useState("");
  const [signinPassword, setSigninPassword] = useState("");
  const [username, setUsername] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [signupEmail, setSignupEmail] = useState("");
  const [signupPassword, setSignupPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState(searchParams.get("tab") === "signup" ? "signup" : "signin");
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    // Check if user is already logged in with local auth token
    const token = localStorage.getItem("auth_token");
    if (token) {
      navigate("/dashboard");
    }
  }, [navigate]);

  const handleSignUp = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/auth/register`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username,
          firstName,
          lastName,
          birthDate,
          email: signupEmail,
          password: signupPassword,
        }),
      });

      if (response.ok) {
        // Success - 2xx status code
        toast({
          title: "Account created!",
          description: "You can now sign in with your credentials.",
        });
        
        // Reset form
        setUsername("");
        setFirstName("");
        setLastName("");
        setBirthDate("");
        setSignupEmail("");
        setSignupPassword("");
        
        // Redirect to sign in tab
        setActiveTab("signin");
      } else {
        // Non-2xx status code
        const data = await response.json();
        throw new Error(data.message || "Could not create account");
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Could not create account",
        description: error instanceof Error ? error.message : "An error occurred during registration",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleSignIn = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username: signinUsername,
          password: signinPassword,
        }),
      });

      if (response.ok) {
        // Success - 2xx status code
        const data = await response.json();
        
        // Store the authentication token - try different possible field names
        const token = data.token || data.accessToken || data.jwt;
        if (token) {
          localStorage.setItem("auth_token", token);
          console.log("Token stored successfully");
        } else {
          console.error("No token found in response:", data);
          toast({
            variant: "destructive",
            title: "Authentication error",
            description: "No token received from server",
          });
          setLoading(false);
          return;
        }
        
        // Fetch vaccination types and immunization plans after successful login
        await Promise.all([fetchVaccinationTypes(), fetchImmunizationPlans()]);
        
        // Redirect to dashboard
        navigate("/dashboard");
      } else {
        // Non-2xx status code
        const data = await response.json();
        throw new Error(data.message || "Sign in failed");
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Sign in failed",
        description: error instanceof Error ? error.message : "An error occurred during sign in",
      });
    } finally {
      setLoading(false);
    }
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
            <h1 className="text-2xl md:text-3xl font-bold whitespace-nowrap">{t("auth.welcome")}</h1>
            <p className="text-muted-foreground mt-2 text-center">
              {t("auth.subtitle")}
            </p>
          </div>

          <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="signin">{t("auth.signin")}</TabsTrigger>
              <TabsTrigger value="signup">{t("auth.signup")}</TabsTrigger>
            </TabsList>

            <TabsContent value="signin">
              <Card>
                <CardHeader>
                  <CardTitle>{t("auth.signin.title")}</CardTitle>
                  <CardDescription>{t("auth.signin.description")}</CardDescription>
                </CardHeader>
                <CardContent>
                  <form onSubmit={handleSignIn} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="signin-username">{t("auth.username")}</Label>
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
                      <Label htmlFor="signin-password">{t("auth.password")}</Label>
                      <Input
                        id="signin-password"
                        type="password"
                        value={signinPassword}
                        onChange={(e) => setSigninPassword(e.target.value)}
                        required
                      />
                    </div>
                    <Button type="submit" className="w-full" disabled={loading}>
                      {loading ? t("auth.signin.loading") : t("auth.signin.button")}
                    </Button>
                  </form>
                </CardContent>
              </Card>
            </TabsContent>

            <TabsContent value="signup">
              <Card>
                <CardHeader>
                  <CardTitle>{t("auth.signup.title")}</CardTitle>
                  <CardDescription>{t("auth.signup.description")}</CardDescription>
                </CardHeader>
                <CardContent>
                  <form onSubmit={handleSignUp} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="signup-username">{t("auth.username")}</Label>
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
                        <Label htmlFor="signup-firstname">{t("auth.firstName")}</Label>
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
                        <Label htmlFor="signup-lastname">{t("auth.lastName")}</Label>
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
                      <Label htmlFor="signup-birthdate">{t("auth.birthDate")}</Label>
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
                      <Label htmlFor="signup-password">{t("auth.password")}</Label>
                      <Input
                        id="signup-password"
                        type="password"
                        value={signupPassword}
                        onChange={(e) => setSignupPassword(e.target.value)}
                        required
                        minLength={6}
                      />
                    </div>
                    <Button type="submit" className="w-full" disabled={loading}>
                      {loading ? t("auth.signup.loading") : t("auth.signup.button")}
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
