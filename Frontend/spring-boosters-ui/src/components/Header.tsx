import { Button } from "@/components/ui/button";
import { Syringe, LogOut } from "lucide-react";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";

interface HeaderProps {
  user?: any;
}

const Header = ({ user }: HeaderProps) => {
  const { toast } = useToast();

  const handleLogout = async () => {
    const { error } = await supabase.auth.signOut();
    if (error) {
      toast({
        variant: "destructive",
        title: "Fehler beim Abmelden",
        description: error.message,
      });
    } else {
      toast({
        title: "Erfolgreich abgemeldet",
      });
    }
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        <button 
          onClick={() => window.location.href = "/"} 
          className="flex items-center space-x-2 cursor-pointer bg-transparent border-none"
        >
          <Syringe className="h-6 w-6 text-primary" />
          <span className="font-bold text-xl">Spring Boosters</span>
        </button>
        
        <nav className="flex items-center space-x-4">
          {user ? (
            <>
              <Button variant="ghost" onClick={() => window.location.href = "/dashboard"}>
                Dashboard
              </Button>
              <Button variant="ghost" onClick={handleLogout}>
                <LogOut className="h-4 w-4 mr-2" />
                Abmelden
              </Button>
            </>
          ) : (
            <Button onClick={() => window.location.href = "/auth"}>
              Anmelden
            </Button>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
