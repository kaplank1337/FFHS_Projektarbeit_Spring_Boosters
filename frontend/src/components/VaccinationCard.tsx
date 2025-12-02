import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2, Calendar } from "lucide-react";
import StatusBadge from "./StatusBadge";
import { format } from "date-fns";

interface VaccinationCardProps {
  vaccination: {
    id: string;
    administeredOn: string;
    doseOrderClaimed: number | null;
    createdAt: string;
    updatedAt: string;
  };
  status: "up-to-date" | "due-soon" | "overdue" | "no-date";
  onEdit?: () => void;
  onDelete?: () => void;
}

const VaccinationCard = ({ vaccination, status, onEdit, onDelete }: VaccinationCardProps) => {
  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="text-lg">Vaccination Record</CardTitle>
            {vaccination.doseOrderClaimed && (
              <p className="text-sm text-muted-foreground mt-1">
                Dose {vaccination.doseOrderClaimed}
              </p>
            )}
          </div>
          <StatusBadge status={status} />
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="flex items-center text-sm">
            <Calendar className="h-4 w-4 mr-2 text-muted-foreground" />
            <span className="font-medium">Administered:</span>
            <span className="ml-2">{format(new Date(vaccination.administeredOn), "MMM dd, yyyy")}</span>
          </div>
        </div>
        <div className="flex gap-2 mt-4">
          <Button variant="outline" size="sm" onClick={onEdit}>
            <Pencil className="h-3 w-3 mr-1" />
            Edit
          </Button>
          <Button variant="outline" size="sm" onClick={onDelete}>
            <Trash2 className="h-3 w-3 mr-1" />
            Delete
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default VaccinationCard;
