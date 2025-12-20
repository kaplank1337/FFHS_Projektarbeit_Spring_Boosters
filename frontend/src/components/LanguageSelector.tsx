import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useLanguage, type Language } from "@/contexts/LanguageContext";
import { Check } from "lucide-react";

const languageOptions = [
  { code: "de" as Language, label: "Deutsch", flag: "🇩🇪" },
  { code: "en" as Language, label: "English", flag: "🇬🇧" },
  { code: "fr" as Language, label: "Français", flag: "🇫🇷" },
  { code: "it" as Language, label: "Italiano", flag: "🇮🇹" },
];

const LanguageSelector = () => {
  const { language, setLanguage } = useLanguage();

  const currentLanguage = languageOptions.find(
    (lang) => lang.code === language
  );

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="gap-2">
          <span className="hidden sm:inline">
            {currentLanguage?.flag} {currentLanguage?.label}
          </span>
          <span className="sm:hidden">{currentLanguage?.flag}</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-48">
        {languageOptions.map((lang) => (
          <DropdownMenuItem
            key={lang.code}
            onClick={() => setLanguage(lang.code)}
            className="flex items-center justify-between cursor-pointer"
          >
            <span className="flex items-center gap-2">
              <span className="text-lg">{lang.flag}</span>
              <span>{lang.label}</span>
            </span>
            {language === lang.code && (
              <Check className="h-4 w-4 text-primary" />
            )}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default LanguageSelector;
