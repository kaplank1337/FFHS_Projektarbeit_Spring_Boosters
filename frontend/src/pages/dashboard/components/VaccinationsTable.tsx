import { Card, CardContent } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Skeleton } from "@/components/ui/skeleton";
import { useLanguage } from "@/contexts/LanguageContext";
import { formatDate } from "@/lib/date-utils";
import { VaccinationRowActions } from "./VaccinationRowActions";
import type { ImmunizationRecordDto } from "@/api/vaccinations.service";
import { Circle } from "lucide-react";

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
              <TableRow className="hover:bg-transparent">
                <TableHead className="w-[140px] pl-6">
                  {t("dashboard.table.status")}
                </TableHead>
                <TableHead>{t("dashboard.table.vaccine")}</TableHead>
                <TableHead>{t("dashboard.table.date")}</TableHead>
                <TableHead>{t("dashboard.table.dose")}</TableHead>
                <TableHead>{t("dashboard.table.created")}</TableHead>
                <TableHead className="w-20 text-right pr-6">
                  {t("dashboard.table.actions")}
                </TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {[1, 2, 3, 4, 5].map((i) => (
                <TableRow key={i} className="hover:bg-transparent">
                  <TableCell className="pl-6">
                    <Skeleton className="h-6 w-24 rounded-full" />
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
                  <TableCell className="text-right pr-6">
                    <div className="flex justify-end">
                      <Skeleton className="h-8 w-8 rounded-md" />
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
        <CardContent className="py-12 text-center">
          <div className="mx-auto max-w-md space-y-3">
            <p className="text-lg font-medium text-muted-foreground">
              {t("dashboard.empty")}
            </p>
            <p className="text-sm text-muted-foreground">
              {t("dashboard.empty.hint")}
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent className="p-0">
        <Table>
          <TableHeader>
            <TableRow className="hover:bg-transparent">
              <TableHead className="w-[140px] pl-6">
                {t("dashboard.table.status")}
              </TableHead>
              <TableHead>{t("dashboard.table.vaccine")}</TableHead>
              <TableHead>{t("dashboard.table.date")}</TableHead>
              <TableHead>{t("dashboard.table.dose")}</TableHead>
              <TableHead>{t("dashboard.table.created")}</TableHead>
              <TableHead className="w-20 text-right pr-6">
                {t("dashboard.table.actions")}
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {vaccinations.map((vaccination) => (
              <TableRow
                key={vaccination.id}
                className="cursor-pointer transition-colors"
                onClick={() => onEdit(vaccination)}
              >
                <TableCell className="pl-6">
                  <Circle className="h-5 w-5 fill-success text-success" />
                </TableCell>
                <TableCell className="font-medium">
                  {vaccination.vaccineName || "-"}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {formatDate(vaccination.administeredOn)}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {vaccination.doseOrderClaimed
                    ? `${t("dashboard.table.dose")} ${
                        vaccination.doseOrderClaimed
                      }`
                    : "-"}
                </TableCell>
                <TableCell className="text-sm text-muted-foreground">
                  {formatDate(vaccination.createdAt)}
                </TableCell>
                <TableCell className="text-right pr-6">
                  <VaccinationRowActions
                    vaccination={vaccination}
                    onEdit={onEdit}
                    onDelete={onDelete}
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
