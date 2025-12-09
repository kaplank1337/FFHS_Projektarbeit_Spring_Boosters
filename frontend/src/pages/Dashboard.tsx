import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { useLanguage } from "@/contexts/LanguageContext";
import Header from "@/components/Header";
import AddImmunizationRecordDialog from "@/components/AddImmunizationRecordDialog";
import EditImmunizationRecordDialog from "@/components/EditImmunizationRecordDialog";
import { DashboardStatsCards } from "@/components/dashboard/DashboardStatsCards";
import { VaccinationsTable } from "@/components/dashboard/VaccinationsTable";
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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  useDashboardStats,
  usePendingVaccinations,
} from "@/hooks/useDashboard";
import { useVaccinations, useDeleteVaccination } from "@/hooks/useVaccinations";
import type { PendingPriority } from "@/services/dashboard.service";
import type { ImmunizationRecordDto } from "@/services/vaccinations.service";
import { formatDate } from "@/lib/date-utils";

const Dashboard = () => {
  const [user, setUser] = useState<any>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [vaccinationToDelete, setVaccinationToDelete] =
    useState<ImmunizationRecordDto | null>(null);
  const [pendingDialogOpen, setPendingDialogOpen] = useState(false);
  const [pendingPriority, setPendingPriority] =
    useState<PendingPriority | null>(null);
  const [vaccinationToEdit, setVaccinationToEdit] =
    useState<ImmunizationRecordDto | null>(null);

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

  const openDeleteDialog = (
    vaccination: ImmunizationRecordDto,
    e: React.MouseEvent
  ) => {
    e.stopPropagation();
    setVaccinationToDelete(vaccination);
    setDeleteDialogOpen(true);
  };

  const openEditDialog = (vaccination: ImmunizationRecordDto) => {
    setVaccinationToEdit(vaccination);
  };

  const closeEditDialog = () => {
    setVaccinationToEdit(null);
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
          <AddImmunizationRecordDialog />
        </div>

        {/* Stats Cards */}
        <DashboardStatsCards
          stats={stats}
          isLoading={loadingStats}
          onCardClick={fetchPendingVaccinations}
        />

        {/* Vaccinations List */}
        <div>
          <h2 className="text-2xl font-bold mb-4">
            {t("dashboard.vaccinations")}
          </h2>
          <VaccinationsTable
            vaccinations={vaccinations}
            isLoading={loadingVaccinations}
            onEdit={openEditDialog}
            onDelete={openDeleteDialog}
          />
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
      {vaccinationToEdit && (
        <EditImmunizationRecordDialog
          vaccination={vaccinationToEdit}
          open={true}
          onOpenChange={closeEditDialog}
        />
      )}
    </div>
  );
};

export default Dashboard;
