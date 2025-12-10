import { type LucideIcon } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface StatCardProps {
  title: string;
  value: number;
  description: string;
  icon: LucideIcon;
  iconColor: "success" | "warning" | "destructive";
  onClick?: () => void;
}

const colorVariants = {
  success:
    "text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-950/30",
  warning:
    "text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/30",
  destructive: "text-red-600 dark:text-red-400 bg-red-50 dark:bg-red-950/30",
};

export function StatCard({
  title,
  value,
  description,
  icon: Icon,
  iconColor,
  onClick,
}: StatCardProps) {
  return (
    <Card
      className={cn(
        "transition-all duration-200 hover:shadow-lg hover:scale-[1.02]",
        onClick && "cursor-pointer"
      )}
      onClick={onClick}
    >
      <CardHeader className="flex flex-row items-center justify-between pb-3">
        <CardTitle className="text-sm font-medium text-muted-foreground">
          {title}
        </CardTitle>
        <div className={cn("p-2 rounded-lg", colorVariants[iconColor])}>
          <Icon className="h-5 w-5" />
        </div>
      </CardHeader>
      <CardContent className="space-y-1">
        <div className="text-3xl font-bold tracking-tight">{value}</div>
        <p className="text-sm text-muted-foreground leading-relaxed">
          {description}
        </p>
      </CardContent>
    </Card>
  );
}
