import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { supabase } from "@/integrations/supabase/client";
import { useToast } from "@/hooks/use-toast";
import Header from "@/components/Header";
import VaccinationCard from "@/components/VaccinationCard";
import AddVaccinationDialog from "@/components/AddVaccinationDialog";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { CheckCircle, AlertTriangle, XCircle } from "lucide-react";
import { differenceInDays, parseISO } from "date-fns";

const Dashboard = () => {
  const [user, setUser] = useState<any>(null);
  const [vaccinations, setVaccinations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    checkUser();
    const {
      data: { subscription },
    } = supabase.auth.onAuthStateChange((event, session) => {
      if (session) {
        setUser(session.user);
        fetchVaccinations();
      } else {
        navigate("/auth");
      }
    });

    return () => subscription.unsubscribe();
  }, [navigate]);

  const checkUser = async () => {
    const {
      data: { session },
    } = await supabase.auth.getSession();
    if (session) {
      setUser(session.user);
      fetchVaccinations();
    } else {
      navigate("/auth");
    }
  };

  const fetchVaccinations = async () => {
    setLoading(true);
    const { data, error } = await supabase
      .from("vaccinations")
      .select(`
        *,
        vaccination_type:vaccination_types(name, description)
      `)
      .order("vaccination_date", { ascending: false });

    if (error) {
      toast({
        variant: "destructive",
        title: "Error loading vaccinations",
        description: error.message,
      });
    } else {
      setVaccinations(data || []);
    }
    setLoading(false);
  };

  const getVaccinationStatus = (nextDueDate: string | null) => {
    if (!nextDueDate) return "no-date";
    
    const daysUntilDue = differenceInDays(parseISO(nextDueDate), new Date());
    
    if (daysUntilDue < 0) return "overdue";
    if (daysUntilDue <= 30) return "due-soon";
    return "up-to-date";
  };

  const handleDelete = async (id: string) => {
    const { error } = await supabase
      .from("vaccinations")
      .delete()
      .eq("id", id);

    if (error) {
      toast({
        variant: "destructive",
        title: "Error deleting vaccination",
        description: error.message,
      });
    } else {
      toast({
        title: "Vaccination deleted",
      });
      fetchVaccinations();
    }
  };

  const stats = {
    upToDate: vaccinations.filter(v => getVaccinationStatus(v.next_due_date) === "up-to-date").length,
    dueSoon: vaccinations.filter(v => getVaccinationStatus(v.next_due_date) === "due-soon").length,
    overdue: vaccinations.filter(v => getVaccinationStatus(v.next_due_date) === "overdue").length,
  };

  return (
    <div className="min-h-screen bg-background">
      <Header user={user} />
      <div className="container py-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-4xl font-bold">Vaccination Dashboard</h1>
            <p className="text-muted-foreground mt-2">
              Track and manage your vaccination records
            </p>
          </div>
          <AddVaccinationDialog onSuccess={fetchVaccinations} />
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Up to Date</CardTitle>
              <CheckCircle className="h-4 w-4 text-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.upToDate}</div>
              <p className="text-xs text-muted-foreground">Vaccinations current</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Due Soon</CardTitle>
              <AlertTriangle className="h-4 w-4 text-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.dueSoon}</div>
              <p className="text-xs text-muted-foreground">Within 30 days</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Overdue</CardTitle>
              <XCircle className="h-4 w-4 text-destructive" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.overdue}</div>
              <p className="text-xs text-muted-foreground">Requires attention</p>
            </CardContent>
          </Card>
        </div>

        {/* Vaccinations List */}
        <div>
          <h2 className="text-2xl font-bold mb-4">Your Vaccinations</h2>
          {loading ? (
            <p className="text-muted-foreground">Loading vaccinations...</p>
          ) : vaccinations.length === 0 ? (
            <Card>
              <CardContent className="py-8 text-center">
                <p className="text-muted-foreground">No vaccinations recorded yet.</p>
                <p className="text-sm text-muted-foreground mt-2">
                  Click "Add Vaccination" to get started.
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {vaccinations.map((vaccination) => (
                <VaccinationCard
                  key={vaccination.id}
                  vaccination={vaccination}
                  status={getVaccinationStatus(vaccination.next_due_date)}
                  onDelete={() => handleDelete(vaccination.id)}
                />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
