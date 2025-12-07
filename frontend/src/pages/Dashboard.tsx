import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import AddVaccinationDialog from "@/components/AddVaccinationDialog";
import EditVaccinationDialog from "@/components/EditVaccinationDialog";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import {
  CheckCircle,
  AlertTriangle,
  XCircle,
  Trash2,
  Circle,
  Pencil,
} from "lucide-react";
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
import { useDashboardStats, usePendingVaccinations } from "@/hooks/useDashboard";
import { useVaccinations, useDeleteVaccination } from "@/hooks/useVaccinations";
import type { PendingPriority } from "@/services/dashboard.service";
import type { Vaccination } from "@/services/vaccinations.service";
import { formatDate } from "@/lib/date-utils";

const Dashboard = () => {
  const [user, setUser] = useState<any>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [vaccinationToDelete, setVaccinationToDelete] =
    useState<Vaccination | null>(null);
  const [pendingDialogOpen, setPendingDialogOpen] = useState(false);
  const [pendingPriority, setPendingPriority] = useState<PendingPriority | null>(
    null
  );
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [vaccinationToEdit, setVaccinationToEdit] = useState<Vaccination | null>(
    null
  );

  const navigate = useNavigate();
  const { t } = useLanguage();

  // React Query hooks
  const { data: stats, isLoading: loadingStats } = useDashboardStats();
  const { data: vaccinations = [], isLoading: loadingVaccinations } =
    useVaccinations();
  const { data: pendingVaccinations, isLoading: loadingPending } =
    usePendingVaccinations(pendingPriority);
  const deleteMutation = useDeleteVaccination();

  useEffect(() => {
    checkUser();
  }, [navigate]);

  const checkUser = () => {
    const token = localStorage.getItem("auth_token");
    if (token) {
      setUser({ token });
    } else {
      navigate("/auth");
    }
  };

  const openDeleteDialog = (vaccination: Vaccination, e: React.MouseEvent) => {
    e.stopPropagation();
    setVaccinationToDelete(vaccination);
    setDeleteDialogOpen(true);
  };

  const openEditDialog = (vaccination: Vaccination) => {
    setVaccinationToEdit(vaccination);
    setEditDialogOpen(true);
  };

  const handleDelete = async () => {
    if (!vaccinationToDelete) return;

    deleteMutation.mutate(vaccinationToDelete.id, {
      onSuccess: () => {
        setDeleteDialogOpen(false);
        setVaccinationToDelete(null);
      },
    });
  };

  const fetchPendingVaccinations = (priority: PendingPriority) => {
    setPendingPriority(priority);
    setPendingDialogOpen(true);
  };

  const getPriorityTitle = (priority: PendingPriority | null) => {
    switch (priority) {
      case "overdue":
        return t("dashboard.overdue");
      case "due-soon":
        return t("dashboard.dueSoon");
      case "upcoming":
        return t("dashboard.upcoming");
      default:
        return t("dashboard.pending.title");
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
          <AddVaccinationDialog />
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => fetchPendingVaccinations("upcoming")}
          >
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                {t("dashboard.upcoming")}
              </CardTitle>
              <CheckCircle className="h-4 w-4 text-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {loadingStats ? "..." : stats?.upcomingDueCount || 0}
              </div>
              <p className="text-xs text-muted-foreground">
                {t("dashboard.upcoming.desc")}
              </p>
            </CardContent>
          </Card>

          <Card
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => fetchPendingVaccinations("due-soon")}
          >
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                {t("dashboard.dueSoon")}
              </CardTitle>
              <AlertTriangle className="h-4 w-4 text-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {loadingStats ? "..." : stats?.dueSoonCount || 0}
              </div>
              <p className="text-xs text-muted-foreground">
                {t("dashboard.dueSoon.desc")}
              </p>
            </CardContent>
          </Card>

          <Card
            className="cursor-pointer hover:shadow-md transition-shadow"
            onClick={() => fetchPendingVaccinations("overdue")}
          >
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                {t("dashboard.overdue")}
              </CardTitle>
              <XCircle className="h-4 w-4 text-destructive" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {loadingStats ? "..." : stats?.overdueCount || 0}
              </div>
              <p className="text-xs text-muted-foreground">
                {t("dashboard.overdue.desc")}
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Vaccinations List */}
        <div>
          <h2 className="text-2xl font-bold mb-4">
            {t("dashboard.vaccinations")}
          </h2>
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
                      <TableHead className="w-[100px]">
                        {t("dashboard.table.status")}
                      </TableHead>
                      <TableHead>{t("dashboard.table.vaccine")}</TableHead>
                      <TableHead>{t("dashboard.table.date")}</TableHead>
                      <TableHead>{t("dashboard.table.dose")}</TableHead>
                      <TableHead>{t("dashboard.table.created")}</TableHead>
                      <TableHead className="text-right">
                        {t("dashboard.table.actions")}
                      </TableHead>
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
                          {formatDate(vaccination.administeredOn)}
                        </TableCell>
                        <TableCell>
                          {vaccination.doseOrderClaimed
                            ? `Dose ${vaccination.doseOrderClaimed}`
                            : "-"}
                        </TableCell>
                        <TableCell className="text-muted-foreground">
                          {formatDate(vaccination.createdAt)}
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
                  <p>
                    <strong>{t("dashboard.table.date")}:</strong>{" "}
                    {formatDate(vaccinationToDelete.administeredOn)}
                  </p>
                  {vaccinationToDelete.doseOrderClaimed && (
                    <p>
                      <strong>{t("dashboard.table.dose")}:</strong>{" "}
                      {vaccinationToDelete.doseOrderClaimed}
                    </p>
                  )}
                </div>
              )}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>
              {t("dashboard.delete.cancel")}
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
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
          ) : !pendingVaccinations?.vaccinationNames ||
            pendingVaccinations.vaccinationNames.length === 0 ? (
            <p className="text-muted-foreground">
              {t("dashboard.pending.empty")}
            </p>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>{t("dashboard.pending.vaccine")}</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {pendingVaccinations.vaccinationNames.map((vaccine, index) => (
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
      />
    </div>
  );
};

export default Dashboard;
