import { MoreHorizontal, Pencil, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useLanguage } from "@/contexts/LanguageContext";
import type { ImmunizationRecordDto } from "@/api/vaccinations.service";

interface VaccinationRowActionsProps {
  vaccination: ImmunizationRecordDto;
  onEdit: (vaccination: ImmunizationRecordDto) => void;
  onDelete: (vaccination: ImmunizationRecordDto, e: React.MouseEvent) => void;
}

export function VaccinationRowActions({
  vaccination,
  onEdit,
  onDelete,
}: VaccinationRowActionsProps) {
  const { t } = useLanguage();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild onClick={(e) => e.stopPropagation()}>
        <Button
          variant="ghost"
          size="sm"
          className="h-8 w-8 p-0 data-[state=open]:bg-muted"
        >
          <span className="sr-only">{t("dashboard.table.actions")}</span>
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-40">
        <DropdownMenuItem
          onClick={(e) => {
            e.stopPropagation();
            onEdit(vaccination);
          }}
          className="cursor-pointer"
        >
          <Pencil className="mr-2 h-4 w-4" />
          <span>{t("dashboard.edit.action")}</span>
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={(e) => {
            e.stopPropagation();
            onDelete(vaccination, e);
          }}
          className="cursor-pointer text-destructive focus:text-destructive"
        >
          <Trash2 className="mr-2 h-4 w-4" />
          <span>{t("dashboard.delete.action")}</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
