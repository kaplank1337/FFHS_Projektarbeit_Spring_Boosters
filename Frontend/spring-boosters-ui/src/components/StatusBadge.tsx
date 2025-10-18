import { Badge } from "@/components/ui/badge";
import { CheckCircle, AlertTriangle, XCircle, Clock } from "lucide-react";

type StatusType = "up-to-date" | "due-soon" | "overdue" | "no-date";

interface StatusBadgeProps {
  status: StatusType;
}

const StatusBadge = ({ status }: StatusBadgeProps) => {
  const statusConfig = {
    "up-to-date": {
      icon: CheckCircle,
      label: "Aktuell",
      variant: "default" as const,
      className: "bg-success text-success-foreground hover:bg-success/90",
    },
    "due-soon": {
      icon: AlertTriangle,
      label: "Bald fällig",
      variant: "default" as const,
      className: "bg-warning text-warning-foreground hover:bg-warning/90",
    },
    overdue: {
      icon: XCircle,
      label: "Überfällig",
      variant: "destructive" as const,
      className: "",
    },
    "no-date": {
      icon: Clock,
      label: "Kein Fälligkeitsdatum",
      variant: "secondary" as const,
      className: "",
    },
  };

  const config = statusConfig[status];
  const Icon = config.icon;

  return (
    <Badge variant={config.variant} className={config.className}>
      <Icon className="h-3 w-3 mr-1" />
      {config.label}
    </Badge>
  );
};

export default StatusBadge;
