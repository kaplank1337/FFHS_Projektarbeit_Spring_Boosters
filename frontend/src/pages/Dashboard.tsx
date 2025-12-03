import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "@/hooks/use-toast";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import AddVaccinationDialog from "@/components/AddVaccinationDialog";
import EditVaccinationDialog from "@/components/EditVaccinationDialog";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { CheckCircle, AlertTriangle, XCircle, Trash2, Circle, Pencil } from "lucide-react";
import { format } from "date-fns";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

const Dashboard = () => {
  const [user, setUser] = useState<any>(null);
  const [vaccinations, setVaccinations] = useState<any[]>([]);
  const [stats, setStats] = useState({
    overdueCount: 0,
    dueSoonCount: 0,
    upcomingDueCount: 0,
    totalPending: 0,
  });
  const [loadingStats, setLoadingStats] = useState(true);
  const [loadingVaccinations, setLoadingVaccinations] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [vaccinationToDelete, setVaccinationToDelete] = useState<any>(null);
  const [pendingDialogOpen, setPendingDialogOpen] = useState(false);
  const [pendingVaccinations, setPendingVaccinations] = useState<string[]>([]);
  const [pendingPriority, setPendingPriority] = useState<string>("");
  const [loadingPending, setLoadingPending] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [vaccinationToEdit, setVaccinationToEdit] = useState<any>(null);
  const navigate = useNavigate();
  const { toast } = useToast();
  const { t } = useLanguage();

  useEffect(() => {
    checkUser();
  }, [navigate]);

  useEffect(() => {
    if (user) {
      fetchVaccinations();
    }
  }, [user]);

  const checkUser = async () => {
    const token = localStorage.getItem("auth_token");
    if (token) {
      // Token exists, user is authenticated
      setUser({ token }); // Store token in user state
      fetchDashboardData(token);
    } else {
      navigate("/auth");
    }
  };

  const fetchDashboardData = async (token: string) => {
    setLoadingStats(true);
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    console.log("Fetching dashboard data with token:", token ? "Token present" : "No token");
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-schedule/pending/summary`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      console.log("Dashboard API response status:", response.status);

      if (response.ok) {
        const data = await response.json();
        console.log("Dashboard data received:", data);
        // Update stats with API response
        setStats({
          overdueCount: data.overdueCount || 0,
          dueSoonCount: data.dueSoonCount || 0,
          upcomingDueCount: data.upcomingDueCount || 0,
          totalPending: data.totalPending || 0,
        });
      } else {
        const errorText = await response.text();
        console.error("Dashboard API error:", errorText);
        toast({
          variant: "destructive",
          title: "Error loading dashboard data",
          description: "Could not fetch vaccination statistics",
        });
      }
    } catch (error) {
      console.error("Dashboard fetch error:", error);
      toast({
        variant: "destructive",
        title: "Error loading dashboard data",
        description: error instanceof Error ? error.message : "An error occurred",
      });
    } finally {
      setLoadingStats(false);
    }
  };

  const fetchVaccinations = async () => {
    setLoadingVaccinations(true);
    const token = localStorage.getItem("auth_token");
    if (!token) {
      setLoadingVaccinations(false);
      return;
    }

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-records`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const data = await response.json();
        setVaccinations(data || []);
      } else {
        toast({
          variant: "destructive",
          title: "Error loading vaccinations",
          description: "Could not fetch vaccination records",
        });
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error loading vaccinations",
        description: error instanceof Error ? error.message : "An error occurred",
      });
    } finally {
      setLoadingVaccinations(false);
    }
  };


  const openDeleteDialog = (vaccination: any, e: React.MouseEvent) => {
    e.stopPropagation();
    setVaccinationToDelete(vaccination);
    setDeleteDialogOpen(true);
  };

  const openEditDialog = (vaccination: any) => {
    setVaccinationToEdit(vaccination);
    setEditDialogOpen(true);
  };

  const handleDelete = async () => {
    if (!vaccinationToDelete) return;
    
    const token = localStorage.getItem("auth_token");
    if (!token) return;

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-records/${vaccinationToDelete.id}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.status === 204 || response.ok) {
        toast({
          title: t("dashboard.vaccinationDeleted"),
        });
        fetchVaccinations();
        const token = localStorage.getItem("auth_token");
        if (token) fetchDashboardData(token);
      } else {
        toast({
          variant: "destructive",
          title: "Error deleting vaccination",
          description: "Could not delete vaccination record",
        });
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error deleting vaccination",
        description: error instanceof Error ? error.message : "An error occurred",
      });
    } finally {
      setDeleteDialogOpen(false);
      setVaccinationToDelete(null);
    }
  };

  const fetchPendingVaccinations = async (priority: string) => {
    const token = localStorage.getItem("auth_token");
    if (!token) return;

    setLoadingPending(true);
    setPendingPriority(priority);
    setPendingDialogOpen(true);

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8000";
    
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/immunization-schedule/pending/${priority}`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const data = await response.json();
        setPendingVaccinations(data.vaccinationNames || []);
      } else {
        toast({
          variant: "destructive",
          title: "Error",
          description: "Could not fetch pending vaccinations",
        });
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "An error occurred",
      });
    } finally {
      setLoadingPending(false);
    }
  };

  const getPriorityTitle = (priority: string) => {
    switch (priority) {
      case "overdue": return t("dashboard.overdue");
      case "due-soon": return t("dashboard.dueSoon");
      case "upcoming": return t("dashboard.upcoming");
      default: return t("dashboard.pending.title");
    }
  };


  return (
    <div className="min-h-screen bg-background">
      <Header user={user} />
      <div className="container py-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-4xl font-bold">{t("dashboard.title")}</h1>
            <p className="text-muted-foreground mt-2">
              {t("dashboard.subtitle")}
            </p>
          </div>
          <AddVaccinationDialog onSuccess={() => {
            fetchVaccinations();
            const token = localStorage.getItem("auth_token");
            if (token) fetchDashboardData(token);
          }} />
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => fetchPendingVaccinations("upcoming")}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{t("dashboard.upcoming")}</CardTitle>
                <CheckCircle className="h-4 w-4 text-success" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {loadingStats ? "..." : stats.upcomingDueCount}
                </div>
                <p className="text-xs text-muted-foreground">{t("dashboard.upcoming.desc")}</p>
              </CardContent>
            </Card>

            <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => fetchPendingVaccinations("due-soon")}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{t("dashboard.dueSoon")}</CardTitle>
                <AlertTriangle className="h-4 w-4 text-warning" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {loadingStats ? "..." : stats.dueSoonCount}
                </div>
                <p className="text-xs text-muted-foreground">{t("dashboard.dueSoon.desc")}</p>
              </CardContent>
            </Card>

            <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => fetchPendingVaccinations("overdue")}>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">{t("dashboard.overdue")}</CardTitle>
                <XCircle className="h-4 w-4 text-destructive" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">
                  {loadingStats ? "..." : stats.overdueCount}
                </div>
                <p className="text-xs text-muted-foreground">{t("dashboard.overdue.desc")}</p>
              </CardContent>
            </Card>
        </div>

        {/* Vaccinations List */}
        <div>
          <h2 className="text-2xl font-bold mb-4">{t("dashboard.vaccinations")}</h2>
          {loadingVaccinations ? (
            <p className="text-muted-foreground">{t("dashboard.loading")}</p>
          ) : vaccinations.length === 0 ? (
            <Card>
              <CardContent className="py-8 text-center">
                <p className="text-muted-foreground">{t("dashboard.empty")}</p>
                <p className="text-sm text-muted-foreground mt-2">
                  {t("dashboard.empty.hint")}
                </p>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardContent className="p-0">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead className="w-[100px]">{t("dashboard.table.status")}</TableHead>
                      <TableHead>{t("dashboard.table.vaccine")}</TableHead>
                      <TableHead>{t("dashboard.table.date")}</TableHead>
                      <TableHead>{t("dashboard.table.dose")}</TableHead>
                      <TableHead>{t("dashboard.table.created")}</TableHead>
                      <TableHead className="text-right">{t("dashboard.table.actions")}</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {vaccinations.map((vaccination) => (
                      <TableRow 
                        key={vaccination.id} 
                        className="cursor-pointer hover:bg-muted/50"
                        onClick={() => openEditDialog(vaccination)}
                      >
                        <TableCell>
                          <Circle className="h-5 w-5 fill-success text-success" />
                        </TableCell>
                        <TableCell className="font-medium">
                          {vaccination.vaccineName || "-"}
                        </TableCell>
                        <TableCell>
                          {format(new Date(vaccination.administeredOn), "MMM dd, yyyy")}
                        </TableCell>
                        <TableCell>
                          {vaccination.doseOrderClaimed ? `Dose ${vaccination.doseOrderClaimed}` : "-"}
                        </TableCell>
                        <TableCell className="text-muted-foreground">
                          {format(new Date(vaccination.createdAt), "MMM dd, yyyy")}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={(e) => {
                              e.stopPropagation();
                              openEditDialog(vaccination);
                            }}
                          >
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={(e) => openDeleteDialog(vaccination, e)}
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
          )}
        </div>
      </div>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>{t("dashboard.delete.title")}</AlertDialogTitle>
            <AlertDialogDescription>
              {t("dashboard.delete.description")}
              {vaccinationToDelete && (
                <div className="mt-4 p-3 bg-muted rounded-md">
                  <p><strong>{t("dashboard.table.date")}:</strong> {format(new Date(vaccinationToDelete.administeredOn), "MMM dd, yyyy")}</p>
                  {vaccinationToDelete.doseOrderClaimed && (
                    <p><strong>{t("dashboard.table.dose")}:</strong> {vaccinationToDelete.doseOrderClaimed}</p>
                  )}
                </div>
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t("dashboard.delete.cancel")}</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
              {t("dashboard.delete.confirm")}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Pending Vaccinations Dialog */}
      <Dialog open={pendingDialogOpen} onOpenChange={setPendingDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>{getPriorityTitle(pendingPriority)}</DialogTitle>
          </DialogHeader>
          {loadingPending ? (
            <p className="text-muted-foreground">{t("dashboard.loading")}</p>
          ) : pendingVaccinations.length === 0 ? (
            <p className="text-muted-foreground">{t("dashboard.pending.empty")}</p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{t("dashboard.pending.vaccine")}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {pendingVaccinations.map((vaccine, index) => (
                  <TableRow key={index}>
                    <TableCell>{vaccine}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DialogContent>
      </Dialog>

      {/* Edit Vaccination Dialog */}
      <EditVaccinationDialog
        vaccination={vaccinationToEdit}
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        onSuccess={() => {
          fetchVaccinations();
          const token = localStorage.getItem("auth_token");
          if (token) fetchDashboardData(token);
        }}
      />
    </div>
  );
};

export default Dashboard;
