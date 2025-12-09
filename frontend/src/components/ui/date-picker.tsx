import { de, enUS, fr } from "date-fns/locale";
import { Calendar as CalendarIcon } from "lucide-react";

import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { useLanguage } from "@/contexts/LanguageContext";
import { formatDate } from "@/lib/date-utils";

interface DatePickerProps {
  date?: Date;
  onSelect?: (date: Date | undefined) => void;
  placeholder?: string;
  disabled?: boolean;
  "aria-invalid"?: boolean;
}

const localeMap = {
  de: de,
  en: enUS,
  fr: fr,
};

export function DatePicker({
  date,
  onSelect,
  placeholder = "Pick a date",
  disabled,
  "aria-invalid": ariaInvalid,
}: DatePickerProps) {
  const { language } = useLanguage();
  const locale = localeMap[language];

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          disabled={disabled}
          aria-invalid={ariaInvalid}
          data-empty={!date}
          className={cn(
            "w-full justify-start text-left font-normal",
            !date && "text-muted-foreground"
          )}
        >
          <CalendarIcon />
          {date ? formatDate(date) : <span>{placeholder}</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0" align="start">
        <Calendar
          mode="single"
          selected={date}
          onSelect={onSelect}
          locale={locale}
        />
      </PopoverContent>
    </Popover>
  );
}
