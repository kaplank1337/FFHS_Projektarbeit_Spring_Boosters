import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Pencil, Trash2, Calendar } from "lucide-react";
import StatusBadge from "./StatusBadge";
import { format } from "date-fns";

interface VaccinationCardProps {
  vaccination: {
    id: string;
    vaccination_date: string;
    next_due_date: string | null;
    notes: string | null;
    vaccination_type: {
      name: string;
      description: string | null;
    };
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
            <CardTitle className="text-lg">{vaccination.vaccination_type.name}</CardTitle>
            {vaccination.vaccination_type.description && (
              <p className="text-sm text-muted-foreground mt-1">
                {vaccination.vaccination_type.description}
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
            <span className="font-medium">Vaccinated:</span>
            <span className="ml-2">{format(new Date(vaccination.vaccination_date), "MMM dd, yyyy")}</span>
          </div>
          {vaccination.next_due_date && (
            <div className="flex items-center text-sm">
              <Calendar className="h-4 w-4 mr-2 text-muted-foreground" />
              <span className="font-medium">Next due:</span>
              <span className="ml-2">{format(new Date(vaccination.next_due_date), "MMM dd, yyyy")}</span>
            </div>
          )}
          {vaccination.notes && (
            <p className="text-sm text-muted-foreground mt-2">{vaccination.notes}</p>
          )}
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
