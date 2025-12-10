import { CheckCircle, AlertTriangle, XCircle } from "lucide-react";
import { useLanguage } from "@/contexts/LanguageContext";
import { StatCard } from "./StatCard";
import type { PendingPriority } from "@/api/dashboard.service";
import type { DashboardStatsDto } from "@/api/dashboard.service";
import { StatCardSkeleton } from "./StatCardSkeleton";

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
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        {[1, 2, 3].map((i) => (
          <StatCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
      <StatCard
        title={t("dashboard.upcoming")}
        value={stats?.upcomingDueCount || 0}
        description={t("dashboard.upcoming.desc")}
        icon={CheckCircle}
        iconColor="success"
        onClick={() => onCardClick("upcoming")}
      />

      <StatCard
        title={t("dashboard.dueSoon")}
        value={stats?.dueSoonCount || 0}
        description={t("dashboard.dueSoon.desc")}
        icon={AlertTriangle}
        iconColor="warning"
        onClick={() => onCardClick("due-soon")}
      />

      <StatCard
        title={t("dashboard.overdue")}
        value={stats?.overdueCount || 0}
        description={t("dashboard.overdue.desc")}
        icon={XCircle}
        iconColor="destructive"
        onClick={() => onCardClick("overdue")}
      />
    </div>
  );
}
