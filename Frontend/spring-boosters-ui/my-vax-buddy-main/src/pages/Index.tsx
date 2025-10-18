import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Syringe, Calendar, Bell, Shield, CheckCircle, ArrowRight } from "lucide-react";
import Header from "@/components/Header";

const Index = () => {
  const navigate = (path: string) => {
    window.location.href = path;
  };

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-primary/10 via-background to-secondary/10" />
        <div className="container relative py-24 md:py-32">
          <div className="mx-auto max-w-3xl text-center">
            <div className="inline-flex items-center justify-center p-2 mb-6 rounded-full bg-primary/10">
              <Syringe className="h-12 w-12 text-primary" />
            </div>
            <h1 className="text-4xl md:text-6xl font-bold tracking-tight mb-6">
              Your Vaccinations,{" "}
              <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                Simplified
              </span>
            </h1>
            <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
              Take control of your vaccination records with Spring Boosters. 
              Track, manage, and never miss an important booster shot again.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Get Started
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
              <Button size="lg" variant="outline" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Sign In
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 bg-muted/30">
        <div className="container">
          <div className="text-center mb-16">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Everything You Need to Stay Protected
            </h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              Comprehensive vaccination management at your fingertips
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-primary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Calendar className="h-6 w-6 text-primary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Track Vaccinations</h3>
                <p className="text-muted-foreground">
                  Record all your vaccinations with dates and notes for complete history tracking.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-secondary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Bell className="h-6 w-6 text-secondary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Smart Reminders</h3>
                <p className="text-muted-foreground">
                  Get timely notifications for upcoming boosters so you never miss a dose.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-warning/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Shield className="h-6 w-6 text-warning" />
                </div>
                <h3 className="text-xl font-bold mb-2">Secure & Private</h3>
                <p className="text-muted-foreground">
                  Your health data stays private and secure, compliant with Swiss data protection laws.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-success/10 w-12 h-12 flex items-center justify-center mb-4">
                  <CheckCircle className="h-6 w-6 text-success" />
                </div>
                <h3 className="text-xl font-bold mb-2">Status Overview</h3>
                <p className="text-muted-foreground">
                  Dashboard showing which vaccinations are current, due soon, or overdue at a glance.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-accent/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Syringe className="h-6 w-6 text-accent-foreground" />
                </div>
                <h3 className="text-xl font-bold mb-2">Master Database</h3>
                <p className="text-muted-foreground">
                  Pre-loaded with common vaccinations and their recommended booster schedules.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-primary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Calendar className="h-6 w-6 text-primary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Responsive Design</h3>
                <p className="text-muted-foreground">
                  Access your vaccination records anywhere, on any device - desktop, tablet, or mobile.
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24">
        <div className="container">
          <Card className="border-2 border-primary/20 bg-gradient-to-br from-primary/5 to-secondary/5">
            <CardContent className="p-12 text-center">
              <h2 className="text-3xl md:text-4xl font-bold mb-4">
                Ready to Take Control?
              </h2>
              <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
                Join Spring Boosters today and never worry about missing a vaccination again.
              </p>
              <Button size="lg" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Start Managing Your Health
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t py-8">
        <div className="container text-center text-muted-foreground">
          <p>© 2025 Spring Boosters. Your health, your data, your control.</p>
        </div>
      </footer>
    </div>
  );
};

export default Index;
