import { useNavigate } from "react-router";
import { Button } from "@/components/ui/button";
import { useLanguage } from "@/contexts/LanguageContext";
import { useLogout } from "@/hooks/useAuth";
import { LogOut, Syringe } from "lucide-react";
import LanguageSelector from "./LanguageSelector";

interface HeaderProps {
  user?: any;
}

const Header = ({ user }: HeaderProps) => {
  const navigate = useNavigate();
  const { t } = useLanguage();
  const handleLogout = useLogout();

  return (
    <header className="border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60 sticky top-0 z-50">
      <div className="container flex h-16 items-center justify-between">
        <div
          className="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity"
          onClick={() => navigate("/")}
        >
          <Syringe className="h-6 w-6 text-primary" />
          <span className="text-xl font-bold">Spring Boosters</span>
        </div>

        <div className="flex items-center gap-2">
          <LanguageSelector />

          {user && (
            <Button variant="ghost" onClick={handleLogout}>
              <LogOut className="h-4 w-4 mr-2" />
              <span className="hidden sm:inline">{t("header.logout")}</span>
            </Button>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;
