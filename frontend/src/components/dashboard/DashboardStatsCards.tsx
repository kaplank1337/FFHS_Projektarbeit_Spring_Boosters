import { CheckCircle, AlertTriangle, XCircle } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { useLanguage } from "@/contexts/LanguageContext";
import type { PendingPriority } from "@/services/dashboard.service";
import type { DashboardStatsDto } from "@/services/dashboard.service";

interface DashboardStatsCardsProps {
  stats?: DashboardStatsDto;
  isLoading: boolean;
  onCardClick: (priority: PendingPriority) => void;
}

export function DashboardStatsCards({
  stats,
  isLoading,
  onCardClick,
}: DashboardStatsCardsProps) {
  const { t } = useLanguage();

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {[1, 2, 3].map((i) => (
          <Card key={i}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-4 w-4 rounded-full" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-8 w-16 mb-2" />
              <Skeleton className="h-3 w-32" />
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <Card
        className="cursor-pointer hover:shadow-md transition-shadow"
        onClick={() => onCardClick("upcoming")}
      >
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">
            {t("dashboard.upcoming")}
          </CardTitle>
          <CheckCircle className="h-4 w-4 text-success" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">
            {stats?.upcomingDueCount || 0}
          </div>
          <p className="text-xs text-muted-foreground">
            {t("dashboard.upcoming.desc")}
          </p>
        </CardContent>
      </Card>

      <Card
        className="cursor-pointer hover:shadow-md transition-shadow"
        onClick={() => onCardClick("due-soon")}
      >
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">
            {t("dashboard.dueSoon")}
          </CardTitle>
          <AlertTriangle className="h-4 w-4 text-warning" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{stats?.dueSoonCount || 0}</div>
          <p className="text-xs text-muted-foreground">
            {t("dashboard.dueSoon.desc")}
          </p>
        </CardContent>
      </Card>

      <Card
        className="cursor-pointer hover:shadow-md transition-shadow"
        onClick={() => onCardClick("overdue")}
      >
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">
            {t("dashboard.overdue")}
          </CardTitle>
          <XCircle className="h-4 w-4 text-destructive" />
        </CardHeader>
        <CardContent>
          <div className="text-2xl font-bold">{stats?.overdueCount || 0}</div>
          <p className="text-xs text-muted-foreground">
            {t("dashboard.overdue.desc")}
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
