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
              Ihre Impfungen,{" "}
              <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
                Vereinfacht
              </span>
            </h1>
            <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
              Behalten Sie die Kontrolle über Ihre Impfaufzeichnungen mit Spring Boosters.
              Verfolgen, verwalten und verpassen Sie nie wieder eine wichtige Auffrischungsimpfung.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Jetzt starten
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
              <Button size="lg" variant="outline" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Anmelden
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
              Alles, was Sie für Ihren Schutz brauchen
            </h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              Umfassende Impfverwaltung auf Knopfdruck
            </p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 max-w-6xl mx-auto">
            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-primary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Calendar className="h-6 w-6 text-primary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Impfungen verfolgen</h3>
                <p className="text-muted-foreground">
                  Erfassen Sie alle Ihre Impfungen mit Datum und Notizen für eine vollständige Historienübersicht.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-secondary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Bell className="h-6 w-6 text-secondary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Intelligente Erinnerungen</h3>
                <p className="text-muted-foreground">
                  Erhalten Sie rechtzeitige Benachrichtigungen für anstehende Auffrischungen, damit Sie keine Dosis verpassen.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-warning/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Shield className="h-6 w-6 text-warning" />
                </div>
                <h3 className="text-xl font-bold mb-2">Sicher & Privat</h3>
                <p className="text-muted-foreground">
                  Ihre Gesundheitsdaten bleiben privat und sicher, konform mit den Schweizer Datenschutzgesetzen.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-success/10 w-12 h-12 flex items-center justify-center mb-4">
                  <CheckCircle className="h-6 w-6 text-success" />
                </div>
                <h3 className="text-xl font-bold mb-2">Statusübersicht</h3>
                <p className="text-muted-foreground">
                  Sehen Sie auf einen Blick, welche Impfungen aktuell sind und welche bald fällig werden.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-accent/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Syringe className="h-6 w-6 text-accent-foreground" />
                </div>
                <h3 className="text-xl font-bold mb-2">Master-Datenbank</h3>
                <p className="text-muted-foreground">
                  Vorab geladen mit gängigen Impfungen und ihren empfohlenen Auffrischungsplänen.
                </p>
              </CardContent>
            </Card>

            <Card className="border-2 hover:border-primary/50 transition-colors">
              <CardContent className="pt-6">
                <div className="rounded-full bg-primary/10 w-12 h-12 flex items-center justify-center mb-4">
                  <Calendar className="h-6 w-6 text-primary" />
                </div>
                <h3 className="text-xl font-bold mb-2">Responsives Design</h3>
                <p className="text-muted-foreground">
                  Greifen Sie von überall und auf jedem Gerät - Desktop, Tablet oder Mobiltelefon - auf Ihre Impfaufzeichnungen zu.
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
                Bereit, die Kontrolle zu übernehmen?
              </h2>
              <p className="text-xl text-muted-foreground mb-8 max-w-2xl mx-auto">
                Werden Sie noch heute Mitglied bei Spring Boosters und machen Sie sich nie wieder Sorgen, eine Impfung zu verpassen.
              </p>
              <Button size="lg" className="text-lg px-8" onClick={() => navigate("/auth")}>
                Beginnen Sie, Ihre Gesundheit zu verwalten
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Footer */}
      <footer className="border-t py-8">
        <div className="container text-center text-muted-foreground">
          <p>© 2025 Spring Boosters. Ihre Gesundheit, Ihre Daten, Ihre Kontrolle.</p>
        </div>
      </footer>
    </div>
  );
};

export default Index;
