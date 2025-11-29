import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { CalendarIcon, Plus } from "lucide-react";
import { format, addMonths } from "date-fns";
import { cn } from "@/lib/utils";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";

interface AddVaccinationDialogProps {
  onSuccess?: () => void;
}

const AddVaccinationDialog = ({ onSuccess }: AddVaccinationDialogProps) => {
  const [open, setOpen] = useState(false);
  const [vaccinationTypes, setVaccinationTypes] = useState<any[]>([]);
  const [selectedType, setSelectedType] = useState<string>("");
  const [vaccinationDate, setVaccinationDate] = useState<Date>();
  const [notes, setNotes] = useState("");
  const [loading, setLoading] = useState(false);
  const { toast } = useToast();

  useEffect(() => {
    fetchVaccinationTypes();
  }, []);

  const fetchVaccinationTypes = async () => {
    const { data, error } = await supabase
      .from("vaccination_types")
      .select("*")
      .order("name");
    
    if (error) {
      toast({
        variant: "destructive",
        title: "Error loading vaccination types",
        description: error.message,
      });
    } else {
      setVaccinationTypes(data || []);
    }
  };

  const calculateNextDueDate = (date: Date, intervalMonths: number) => {
    if (intervalMonths === 0) return null;
    return format(addMonths(date, intervalMonths), "yyyy-MM-dd");
  };

  const handleSubmit = async () => {
    if (!selectedType || !vaccinationDate) {
      toast({
        variant: "destructive",
        title: "Missing information",
        description: "Please select a vaccination type and date.",
      });
      return;
    }

    setLoading(true);
    
    const { data: { user } } = await supabase.auth.getUser();
    if (!user) {
      toast({
        variant: "destructive",
        title: "Not authenticated",
        description: "Please sign in to add vaccinations.",
      });
      setLoading(false);
      return;
    }

    const selectedVaccType = vaccinationTypes.find(vt => vt.id === selectedType);
    const nextDueDate = calculateNextDueDate(
      vaccinationDate,
      selectedVaccType?.recommended_interval_months || 0
    );

    const { error } = await supabase
      .from("vaccinations")
      .insert({
        user_id: user.id,
        vaccination_type_id: selectedType,
        vaccination_date: format(vaccinationDate, "yyyy-MM-dd"),
        next_due_date: nextDueDate,
        notes: notes || null,
      });

    setLoading(false);

    if (error) {
      toast({
        variant: "destructive",
        title: "Error adding vaccination",
        description: error.message,
      });
    } else {
      toast({
        title: "Vaccination added",
        description: "Your vaccination record has been saved.",
      });
      setOpen(false);
      setSelectedType("");
      setVaccinationDate(undefined);
      setNotes("");
      onSuccess?.();
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="h-4 w-4 mr-2" />
          Add Vaccination
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Add New Vaccination</DialogTitle>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div className="space-y-2">
            <Label htmlFor="type">Vaccination Type</Label>
            <Select value={selectedType} onValueChange={setSelectedType}>
              <SelectTrigger>
                <SelectValue placeholder="Select vaccination type" />
              </SelectTrigger>
              <SelectContent>
                {vaccinationTypes.map((type) => (
                  <SelectItem key={type.id} value={type.id}>
                    {type.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="space-y-2">
            <Label>Vaccination Date</Label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className={cn(
                    "w-full justify-start text-left font-normal",
                    !vaccinationDate && "text-muted-foreground"
                  )}
                >
                  <CalendarIcon className="mr-2 h-4 w-4" />
                  {vaccinationDate ? format(vaccinationDate, "PPP") : "Pick a date"}
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  mode="single"
                  selected={vaccinationDate}
                  onSelect={setVaccinationDate}
                  initialFocus
                  className="pointer-events-auto"
                />
              </PopoverContent>
            </Popover>
          </div>

          <div className="space-y-2">
            <Label htmlFor="notes">Notes (Optional)</Label>
            <Textarea
              id="notes"
              placeholder="Add any notes about this vaccination..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              rows={3}
            />
          </div>
        </div>

        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={() => setOpen(false)}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? "Adding..." : "Add Vaccination"}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default AddVaccinationDialog;
