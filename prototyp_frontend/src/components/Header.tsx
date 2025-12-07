import { Button } from "@/components/ui/button";
import { Syringe, LogOut } from "lucide-react";
import { useNavigate, useLocation } from "react-router-dom";
import { useToast } from "@/hooks/use-toast";
import { useLanguage } from "@/contexts/LanguageContext";
import LanguageSelector from "./LanguageSelector";

interface HeaderProps {
  user?: any;
}

const Header = ({ user }: HeaderProps) => {
  const { toast } = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useLanguage();

  const handleLogout = () => {
    // Clear the authentication token
    localStorage.removeItem("auth_token");
    
    // Redirect to landing page
    navigate("/");
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        <button 
          onClick={() => navigate("/")} 
          className="flex items-center space-x-2 cursor-pointer bg-transparent border-none"
        >
          <Syringe className="h-6 w-6 text-primary" />
          <span className="font-bold text-xl">Spring Boosters</span>
        </button>
        
        <nav className="flex items-center space-x-4">
          <LanguageSelector />
          {user && (
            <>
              {location.pathname !== "/dashboard" && (
                <Button variant="ghost" onClick={() => navigate("/dashboard")}>
                  {t("header.dashboard")}
                </Button>
              )}
              <Button variant="ghost" onClick={handleLogout}>
                <LogOut className="h-4 w-4 mr-2" />
                {t("header.signOut")}
              </Button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
