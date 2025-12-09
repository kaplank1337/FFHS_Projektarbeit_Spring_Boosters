import { Circle, Pencil, Trash2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { useLanguage } from "@/contexts/LanguageContext";
import { formatDate } from "@/lib/date-utils";
import type { ImmunizationRecordDto } from "@/services/vaccinations.service";

interface VaccinationsTableProps {
  vaccinations: ImmunizationRecordDto[];
  isLoading: boolean;
  onEdit: (vaccination: ImmunizationRecordDto) => void;
  onDelete: (vaccination: ImmunizationRecordDto, e: React.MouseEvent) => void;
}

export function VaccinationsTable({
  vaccinations,
  isLoading,
  onEdit,
  onDelete,
}: VaccinationsTableProps) {
  const { t } = useLanguage();

  if (isLoading) {
    return (
      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[100px]">
                  {t("dashboard.table.status")}
                </TableHead>
                <TableHead>{t("dashboard.table.vaccine")}</TableHead>
                <TableHead>{t("dashboard.table.date")}</TableHead>
                <TableHead>{t("dashboard.table.dose")}</TableHead>
                <TableHead>{t("dashboard.table.created")}</TableHead>
                <TableHead className="text-right">
                  {t("dashboard.table.actions")}
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {[1, 2, 3, 4, 5].map((i) => (
                <TableRow key={i}>
                  <TableCell>
                    <Skeleton className="h-5 w-5 rounded-full" />
                  </TableCell>
                  <TableCell>
                    <Skeleton className="h-4 w-32" />
                  </TableCell>
                  <TableCell>
                    <Skeleton className="h-4 w-24" />
                  </TableCell>
                  <TableCell>
                    <Skeleton className="h-4 w-16" />
                  </TableCell>
                  <TableCell>
                    <Skeleton className="h-4 w-24" />
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Skeleton className="h-8 w-8" />
                      <Skeleton className="h-8 w-8" />
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    );
  }

  if (vaccinations.length === 0) {
    return (
      <Card>
        <CardContent className="py-8 text-center">
          <p className="text-muted-foreground">{t("dashboard.empty")}</p>
          <p className="text-sm text-muted-foreground mt-2">
            {t("dashboard.empty.hint")}
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent className="p-0">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[100px]">
                {t("dashboard.table.status")}
              </TableHead>
              <TableHead>{t("dashboard.table.vaccine")}</TableHead>
              <TableHead>{t("dashboard.table.date")}</TableHead>
              <TableHead>{t("dashboard.table.dose")}</TableHead>
              <TableHead>{t("dashboard.table.created")}</TableHead>
              <TableHead className="text-right">
                {t("dashboard.table.actions")}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {vaccinations.map((vaccination) => (
              <TableRow
                key={vaccination.id}
                className="cursor-pointer hover:bg-muted/50"
                onClick={() => onEdit(vaccination)}
              >
                <TableCell>
                  <Circle className="h-5 w-5 fill-success text-success" />
                </TableCell>
                <TableCell className="font-medium">
                  {vaccination.vaccineName || "-"}
                </TableCell>
                <TableCell>
                  {formatDate(vaccination.administeredOn)}
                </TableCell>
                <TableCell>
                  {vaccination.doseOrderClaimed
                    ? `Dose ${vaccination.doseOrderClaimed}`
                    : "-"}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {formatDate(vaccination.createdAt)}
                </TableCell>
                <TableCell className="text-right">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      onEdit(vaccination);
                    }}
                  >
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={(e) => onDelete(vaccination, e)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
