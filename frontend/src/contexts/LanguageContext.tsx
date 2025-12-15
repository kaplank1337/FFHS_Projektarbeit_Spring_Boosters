import { createContext, useContext, useState, type ReactNode } from "react";

export type Language = "de" | "en" | "fr" | "it";

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: (key: string) => string;
}

const translations: Record<Language, Record<string, string>> = {
  de: {
    // Landing page
    "hero.title.part1": "Ihre Impfungen,",
    "hero.title.part2": "Vereinfacht",
    "hero.description":
      "Übernehmen Sie die Kontrolle über Ihre Impfunterlagen mit Spring Boosters. Verfolgen, verwalten und verpassen Sie nie wieder eine wichtige Auffrischungsimpfung.",
    "hero.getStarted": "Loslegen",
    "hero.signUp": "Registrieren",

    // Features
    "features.title": "Alles was Sie brauchen, um geschützt zu bleiben",
    "features.subtitle": "Umfassende Impfverwaltung auf einen Blick",
    "features.track.title": "Impfungen verfolgen",
    "features.track.description":
      "Erfassen Sie alle Ihre Impfungen mit Daten und Notizen für eine vollständige Dokumentation.",
    "features.reminders.title": "Intelligente Erinnerungen",
    "features.reminders.description":
      "Erhalten Sie rechtzeitige Benachrichtigungen für anstehende Auffrischungen, damit Sie keine Dosis verpassen.",
    "features.status.title": "Statusübersicht",
    "features.status.description":
      "Dashboard zeigt auf einen Blick, welche Impfungen aktuell, bald fällig oder überfällig sind.",
    "features.database.title": "Impfdatenbank",
    "features.database.description":
      "Vorinstalliert mit gängigen Impfungen und deren empfohlenen Auffrischungsplänen.",

    // Footer
    "footer.copyright":
      "© 2025 Spring Boosters. Ihre Gesundheit, Ihre Daten, Ihre Kontrolle.",

    // Header
    "header.dashboard": "Dashboard",
    "header.signOut": "Abmelden",
    "header.signIn": "Anmelden",

    // Auth page
    "auth.welcome": "Willkommen bei Spring Boosters",
    "auth.subtitle": "Verwalten Sie Ihre Impfunterlagen sicher",
    "auth.signin": "Anmelden",
    "auth.signup": "Registrieren",
    "auth.signin.title": "Anmelden",
    "auth.signin.description":
      "Geben Sie Ihre Anmeldedaten ein, um auf Ihr Konto zuzugreifen",
    "auth.username": "Benutzername",
    "auth.password": "Passwort",
    "auth.signin.button": "Anmelden",
    "auth.signin.loading": "Anmeldung...",
    "auth.signup.title": "Konto erstellen",
    "auth.signup.description": "Starten Sie mit Ihrer Impfverfolgung",
    "auth.firstName": "Vorname",
    "auth.lastName": "Nachname",
    "auth.birthDate": "Geburtsdatum",
    "auth.email": "E-Mail",
    "auth.signup.button": "Registrieren",
    "auth.signup.loading": "Konto wird erstellt...",

    // Dashboard
    "dashboard.title": "Impf-Dashboard",
    "dashboard.subtitle": "Verfolgen und verwalten Sie Ihre Impfunterlagen",
    "dashboard.addVaccination": "Impfung hinzufügen",
    "dashboard.vaccineType": "Impfstofftyp",
    "dashboard.selectVaccineType": "Impfstoff auswählen",
    "dashboard.administeredOn": "Verabreicht am",
    "dashboard.doseOrder": "Dosisnummer",
    "dashboard.optional": "Optional",
    "dashboard.cancel": "Abbrechen",
    "dashboard.save": "Speichern",
    "dashboard.saving": "Wird gespeichert...",
    "dashboard.editVaccination": "Impfung bearbeiten",
    "header.logout": "Abmelden",
    "dashboard.upcoming": "Anstehend",
    "dashboard.upcoming.desc": "Fällig in 90 Tagen",
    "dashboard.dueSoon": "Bald fällig",
    "dashboard.dueSoon.desc": "Fällig in 30 Tagen",
    "dashboard.overdue": "Überfällig",
    "dashboard.overdue.desc": "Erfordert Aufmerksamkeit",
    "dashboard.vaccinations": "Ihre Impfungen",
    "dashboard.loading": "Impfungen werden geladen...",
    "dashboard.empty": "Noch keine Impfungen erfasst.",
    "dashboard.empty.hint":
      'Klicken Sie auf "Impfung hinzufügen", um zu beginnen.',
    "dashboard.table.status": "Status",
    "dashboard.table.vaccine": "Impfstoff",
    "dashboard.table.date": "Verabreicht am",
    "dashboard.table.dose": "Dosis",
    "dashboard.table.created": "Erstellt am",
    "dashboard.table.actions": "Aktionen",
    "dashboard.vaccinationDeleted": "Impfung gelöscht",
    "dashboard.pending.title": "Ausstehende Impfungen",
    "dashboard.pending.vaccine": "Impfstoff",
    "dashboard.pending.empty": "Keine ausstehenden Impfungen",
    "dashboard.edit.action": "Bearbeiten",
    "dashboard.delete.action": "Löschen",
    "dashboard.delete.title": "Impfung löschen",
    "dashboard.delete.description":
      "Möchten Sie diese Impfung wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.",
    "dashboard.delete.cancel": "Abbrechen",
    "dashboard.delete.confirm": "Löschen",

    // Add vaccination dialog
    "addVaccination.title": "Impfung hinzufügen",
    "addVaccination.description":
      "Fügen Sie eine neue Impfung zu Ihren Unterlagen hinzu",
    "addVaccination.type": "Impfstofftyp",
    "addVaccination.type.placeholder": "Impfstoff auswählen",
    "addVaccination.plan": "Impfplan",
    "addVaccination.plan.placeholder": "Impfplan auswählen",
    "addVaccination.plan.empty": "Kein Impfplan verfügbar",
    "addVaccination.date": "Verabreichungsdatum",
    "addVaccination.date.placeholder": "Verabreichungsdatum auswählen",
    "addVaccination.dose": "Dosisnummer",
    "addVaccination.dose.placeholder": "z.B. 1, 2, 3",
    "addVaccination.button": "Impfung hinzufügen",
    "addVaccination.cancel": "Abbrechen",
    "addVaccination.success": "Impfung erfolgreich hinzugefügt",

    // Edit vaccination dialog
    "editVaccination.title": "Impfung bearbeiten",
    "editVaccination.vaccineName": "Impfstoff",
    "editVaccination.save": "Speichern",
    "editVaccination.saving": "Wird gespeichert...",
    "editVaccination.success": "Impfung erfolgreich aktualisiert",
    "editVaccination.error.missingInfo": "Fehlende Informationen",
    "editVaccination.error.missingInfoDesc":
      "Bitte füllen Sie alle erforderlichen Felder aus.",
    "editVaccination.error.invalidDose": "Ungültige Dosisnummer",
    "editVaccination.error.invalidDoseDesc":
      "Die Dosisnummer muss eine positive Ganzzahl sein.",
    "editVaccination.error.notAuth": "Nicht authentifiziert",
    "editVaccination.error.notAuthDesc": "Bitte melden Sie sich an.",
    "editVaccination.error.failed": "Fehler beim Aktualisieren",
    "editVaccination.error.failedDesc":
      "Die Impfung konnte nicht aktualisiert werden.",

    "deleteVaccination.success": "Impfung erfolgreich gelöscht",

    // Validation
    "validation.required": "Pflichtfeld",
    "validation.invalidDate": "Datum muss in der Vergangenheit liegen",
    "validation.invalidDoseNumber": "Ungültige Dosisnummer",
    "validation.positiveNumber": "Muss eine positive Zahl sein",
  },
  en: {
    // Landing page
    "hero.title.part1": "Your Vaccinations,",
    "hero.title.part2": "Simplified",
    "hero.description":
      "Take control of your vaccination records with Spring Boosters. Track, manage, and never miss an important booster shot again.",
    "hero.getStarted": "Get Started",
    "hero.signUp": "Sign Up",

    // Features
    "features.title": "Everything You Need to Stay Protected",
    "features.subtitle":
      "Comprehensive vaccination management at your fingertips",
    "features.track.title": "Track Vaccinations",
    "features.track.description":
      "Record all your vaccinations with dates and notes for complete history tracking.",
    "features.reminders.title": "Smart Reminders",
    "features.reminders.description":
      "Get timely notifications for upcoming boosters so you never miss a dose.",
    "features.status.title": "Status Overview",
    "features.status.description":
      "Dashboard showing which vaccinations are current, due soon, or overdue at a glance.",
    "features.database.title": "Master Database",
    "features.database.description":
      "Pre-loaded with common vaccinations and their recommended booster schedules.",

    // Footer
    "footer.copyright":
      "© 2025 Spring Boosters. Your health, your data, your control.",

    // Header
    "header.dashboard": "Dashboard",
    "header.signOut": "Sign Out",
    "header.signIn": "Sign In",

    // Auth page
    "auth.welcome": "Welcome to Spring Boosters",
    "auth.subtitle": "Manage your vaccination records securely",
    "auth.signin": "Sign In",
    "auth.signup": "Sign Up",
    "auth.signin.title": "Sign In",
    "auth.signin.description": "Enter your credentials to access your account",
    "auth.username": "Username",
    "auth.password": "Password",
    "auth.signin.button": "Sign In",
    "auth.signin.loading": "Signing in...",
    "auth.signup.title": "Create Account",
    "auth.signup.description": "Get started with your vaccination tracking",
    "auth.firstName": "First Name",
    "auth.lastName": "Last Name",
    "auth.birthDate": "Birth Date",
    "auth.email": "Email",
    "auth.signup.button": "Sign Up",
    "auth.signup.loading": "Creating account...",

    // Dashboard
    "dashboard.title": "Vaccination Dashboard",
    "dashboard.subtitle": "Track and manage your vaccination records",
    "dashboard.addVaccination": "Add Vaccination",
    "dashboard.vaccineType": "Vaccine Type",
    "dashboard.selectVaccineType": "Select vaccine type",
    "dashboard.administeredOn": "Administered On",
    "dashboard.doseOrder": "Dose Number",
    "dashboard.optional": "Optional",
    "dashboard.cancel": "Cancel",
    "dashboard.save": "Save",
    "dashboard.saving": "Saving...",
    "dashboard.editVaccination": "Edit Vaccination",
    "header.logout": "Logout",
    "dashboard.upcoming": "Upcoming",
    "dashboard.upcoming.desc": "Due in 90 days",
    "dashboard.dueSoon": "Due Soon",
    "dashboard.dueSoon.desc": "Due in 30 days",
    "dashboard.overdue": "Overdue",
    "dashboard.overdue.desc": "Requires attention",
    "dashboard.vaccinations": "Your Vaccinations",
    "dashboard.loading": "Loading vaccinations...",
    "dashboard.empty": "No vaccinations recorded yet.",
    "dashboard.empty.hint": 'Click "Add Vaccination" to get started.',
    "dashboard.table.status": "Status",
    "dashboard.table.vaccine": "Vaccine",
    "dashboard.table.date": "Administered Date",
    "dashboard.table.dose": "Dose Number",
    "dashboard.table.created": "Created At",
    "dashboard.table.actions": "Actions",
    "dashboard.vaccinationDeleted": "Vaccination deleted",
    "dashboard.pending.title": "Pending Vaccinations",
    "dashboard.pending.vaccine": "Vaccine",
    "dashboard.pending.empty": "No pending vaccinations",
    "dashboard.edit.action": "Edit",
    "dashboard.delete.action": "Delete",
    "dashboard.delete.title": "Delete Vaccination",
    "dashboard.delete.description":
      "Are you sure you want to delete this vaccination? This action cannot be undone.",
    "dashboard.delete.cancel": "Cancel",
    "dashboard.delete.confirm": "Delete",

    // Add vaccination dialog
    "addVaccination.title": "Add Vaccination",
    "addVaccination.description": "Add a new vaccination to your records",
    "addVaccination.type": "Vaccination Type",
    "addVaccination.type.placeholder": "Select a vaccine",
    "addVaccination.plan": "Immunization Plan",
    "addVaccination.plan.placeholder": "Select an immunization plan",
    "addVaccination.date": "Date Administered",
    "addVaccination.dose": "Dose Number",
    "addVaccination.dose.placeholder": "e.g. 1, 2, 3",
    "addVaccination.button": "Add Vaccination",
    "addVaccination.saving": "Saving...",
    "addVaccination.cancel": "Cancel",

    // Edit vaccination dialog
    "editVaccination.title": "Edit Vaccination",
    "editVaccination.vaccineName": "Vaccine Name",
    "editVaccination.save": "Save",
    "editVaccination.saving": "Saving...",
    "editVaccination.success": "Vaccination updated",
    "editVaccination.error.missingInfo": "Missing information",
    "editVaccination.error.missingInfoDesc":
      "Please fill in all required fields.",
    "editVaccination.error.invalidDose": "Invalid dose number",
    "editVaccination.error.invalidDoseDesc":
      "Dose number must be a positive integer.",
    "editVaccination.error.notAuth": "Not authenticated",
    "editVaccination.error.notAuthDesc": "Please sign in.",
    "editVaccination.error.failed": "Error updating",
    "editVaccination.error.failedDesc": "Could not update vaccination record.",
  },
  fr: {
    // Landing page
    "hero.title.part1": "Vos Vaccinations,",
    "hero.title.part2": "Simplifiées",
    "hero.description":
      "Prenez le contrôle de vos dossiers de vaccination avec Spring Boosters. Suivez, gérez et ne manquez plus jamais un rappel important.",
    "hero.getStarted": "Commencer",
    "hero.signUp": "S'inscrire",

    // Features
    "features.title": "Tout ce dont vous avez besoin pour rester protégé",
    "features.subtitle": "Gestion complète des vaccinations à portée de main",
    "features.track.title": "Suivre les vaccinations",
    "features.track.description":
      "Enregistrez toutes vos vaccinations avec dates et notes pour un suivi complet.",
    "features.reminders.title": "Rappels intelligents",
    "features.reminders.description":
      "Recevez des notifications pour les rappels à venir afin de ne jamais manquer une dose.",
    "features.status.title": "Aperçu du statut",
    "features.status.description":
      "Tableau de bord montrant quelles vaccinations sont à jour, bientôt dues ou en retard.",
    "features.database.title": "Base de données",
    "features.database.description":
      "Pré-chargé avec les vaccinations courantes et leurs calendriers de rappel recommandés.",

    // Footer
    "footer.copyright":
      "© 2025 Spring Boosters. Votre santé, vos données, votre contrôle.",

    // Header
    "header.dashboard": "Tableau de bord",
    "header.signOut": "Déconnexion",
    "header.signIn": "Connexion",

    // Auth page
    "auth.welcome": "Bienvenue sur Spring Boosters",
    "auth.subtitle": "Gérez vos dossiers de vaccination en toute sécurité",
    "auth.signin": "Connexion",
    "auth.signup": "S'inscrire",
    "auth.signin.title": "Connexion",
    "auth.signin.description":
      "Entrez vos identifiants pour accéder à votre compte",
    "auth.username": "Nom d'utilisateur",
    "auth.password": "Mot de passe",
    "auth.signin.button": "Se connecter",
    "auth.signin.loading": "Connexion...",
    "auth.signup.title": "Créer un compte",
    "auth.signup.description": "Commencez le suivi de vos vaccinations",
    "auth.firstName": "Prénom",
    "auth.lastName": "Nom",
    "auth.birthDate": "Date de naissance",
    "auth.email": "E-mail",
    "auth.signup.button": "S'inscrire",
    "auth.signup.loading": "Création du compte...",

    // Dashboard
    "dashboard.title": "Tableau de bord vaccinations",
    "dashboard.subtitle": "Suivez et gérez vos dossiers de vaccination",
    "dashboard.addVaccination": "Ajouter une vaccination",
    "dashboard.vaccineType": "Type de vaccin",
    "dashboard.selectVaccineType": "Sélectionner le type de vaccin",
    "dashboard.administeredOn": "Administré le",
    "dashboard.doseOrder": "Numéro de dose",
    "dashboard.optional": "Optionnel",
    "dashboard.cancel": "Annuler",
    "dashboard.save": "Enregistrer",
    "dashboard.saving": "Enregistrement...",
    "dashboard.editVaccination": "Modifier la vaccination",
    "header.logout": "Déconnexion",
    "dashboard.upcoming": "À venir",
    "dashboard.upcoming.desc": "Dû dans 90 jours",
    "dashboard.dueSoon": "Bientôt dû",
    "dashboard.dueSoon.desc": "Dû dans 30 jours",
    "dashboard.overdue": "En retard",
    "dashboard.overdue.desc": "Nécessite attention",
    "dashboard.vaccinations": "Vos vaccinations",
    "dashboard.loading": "Chargement des vaccinations...",
    "dashboard.empty": "Aucune vaccination enregistrée.",
    "dashboard.empty.hint":
      'Cliquez sur "Ajouter une vaccination" pour commencer.',
    "dashboard.table.status": "Statut",
    "dashboard.table.vaccine": "Vaccin",
    "dashboard.table.date": "Date d'administration",
    "dashboard.table.dose": "Numéro de dose",
    "dashboard.table.created": "Créé le",
    "dashboard.table.actions": "Actions",
    "dashboard.edit.action": "Modifier",
    "dashboard.delete.action": "Supprimer",
    "dashboard.vaccinationDeleted": "Vaccination supprimée",
    "dashboard.pending.title": "Vaccinations en attente",
    "dashboard.pending.vaccine": "Vaccin",
    "dashboard.pending.empty": "Aucune vaccination en attente",
    "dashboard.delete.title": "Supprimer la vaccination",
    "dashboard.delete.description":
      "Êtes-vous sûr de vouloir supprimer cette vaccination ? Cette action est irréversible.",
    "dashboard.delete.cancel": "Annuler",
    "dashboard.delete.confirm": "Supprimer",

    // Add vaccination dialog
    "addVaccination.title": "Ajouter une vaccination",
    "addVaccination.description":
      "Ajoutez une nouvelle vaccination à vos dossiers",
    "addVaccination.type": "Type de vaccin",
    "addVaccination.type.placeholder": "Sélectionner un vaccin",
    "addVaccination.plan": "Plan de vaccination",
    "addVaccination.plan.placeholder": "Sélectionner un plan",
    "addVaccination.date": "Date d'administration",
    "addVaccination.dose": "Numéro de dose",
    "addVaccination.dose.placeholder": "ex. 1, 2, 3",
    "addVaccination.button": "Ajouter la vaccination",
    "addVaccination.saving": "Enregistrement...",
    "addVaccination.cancel": "Annuler",

    // Edit vaccination dialog
    "editVaccination.title": "Modifier la vaccination",
    "editVaccination.vaccineName": "Nom du vaccin",
    "editVaccination.save": "Enregistrer",
    "editVaccination.saving": "Enregistrement...",
    "editVaccination.success": "Vaccination mise à jour",
    "editVaccination.error.missingInfo": "Informations manquantes",
    "editVaccination.error.missingInfoDesc":
      "Veuillez remplir tous les champs requis.",
    "editVaccination.error.invalidDose": "Numéro de dose invalide",
    "editVaccination.error.invalidDoseDesc":
      "Le numéro de dose doit être un entier positif.",
    "editVaccination.error.notAuth": "Non authentifié",
    "editVaccination.error.notAuthDesc": "Veuillez vous connecter.",
    "editVaccination.error.failed": "Erreur de mise à jour",
    "editVaccination.error.failedDesc":
      "Impossible de mettre à jour la vaccination.",
  },
  it: {
    // Landing page
    "hero.title.part1": "Le tue vaccinazioni,",
    "hero.title.part2": "Semplificate",
    "hero.description":
      "Prendi il controllo dei tuoi registri vaccinali con Spring Boosters. Monitora, gestisci e non perdere mai più un richiamo importante.",
    "hero.getStarted": "Inizia",
    "hero.signUp": "Registrati",

    // Features
    "features.title": "Tutto ciò che serve per restare protetti",
    "features.subtitle": "Gestione completa delle vaccinazioni a portata di mano",
    "features.track.title": "Monitora le vaccinazioni",
    "features.track.description":
      "Registra tutte le tue vaccinazioni con date e note per una cronologia completa.",
    "features.reminders.title": "Promemoria intelligenti",
    "features.reminders.description":
      "Ricevi notifiche puntuali per i richiami in arrivo, così non perdi mai una dose.",
    "features.status.title": "Panoramica dello stato",
    "features.status.description":
      "La dashboard mostra a colpo d'occhio quali vaccinazioni sono aggiornate, in scadenza o in ritardo.",
    "features.database.title": "Database principale",
    "features.database.description":
      "Precaricato con le vaccinazioni comuni e i relativi piani di richiamo raccomandati.",

    // Footer
    "footer.copyright":
      "© 2025 Spring Boosters. La tua salute, i tuoi dati, il tuo controllo.",

    // Header
    "header.dashboard": "Dashboard",
    "header.signOut": "Disconnetti",
    "header.signIn": "Accedi",

    // Auth page
    "auth.welcome": "Benvenuto su Spring Boosters",
    "auth.subtitle": "Gestisci i tuoi registri vaccinali in modo sicuro",
    "auth.signin": "Accedi",
    "auth.signup": "Registrati",
    "auth.signin.title": "Accedi",
    "auth.signin.description": "Inserisci le tue credenziali per accedere al tuo account",
    "auth.username": "Nome utente",
    "auth.password": "Password",
    "auth.signin.button": "Accedi",
    "auth.signin.loading": "Accesso in corso...",
    "auth.signup.title": "Crea account",
    "auth.signup.description": "Inizia con il monitoraggio delle tue vaccinazioni",
    "auth.firstName": "Nome",
    "auth.lastName": "Cognome",
    "auth.birthDate": "Data di nascita",
    "auth.email": "Email",
    "auth.signup.button": "Registrati",
    "auth.signup.loading": "Creazione account...",

    // Dashboard
    "dashboard.title": "Pannello Vaccinazioni",
    "dashboard.subtitle": "Monitora e gestisci i tuoi registri vaccinali",
    "dashboard.addVaccination": "Aggiungi vaccinazione",
    "dashboard.vaccineType": "Tipo di vaccino",
    "dashboard.selectVaccineType": "Seleziona il tipo di vaccino",
    "dashboard.administeredOn": "Somministrato il",
    "dashboard.doseOrder": "Numero dose",
    "dashboard.optional": "Opzionale",
    "dashboard.cancel": "Annulla",
    "dashboard.save": "Salva",
    "dashboard.saving": "Salvataggio...",
    "dashboard.editVaccination": "Modifica vaccinazione",
    "header.logout": "Esci",
    "dashboard.upcoming": "In arrivo",
    "dashboard.upcoming.desc": "Scadenza in 90 giorni",
    "dashboard.dueSoon": "Prossima scadenza",
    "dashboard.dueSoon.desc": "Scadenza in 30 giorni",
    "dashboard.overdue": "Scaduta",
    "dashboard.overdue.desc": "Richiede attenzione",
    "dashboard.vaccinations": "Le tue vaccinazioni",
    "dashboard.loading": "Caricamento vaccinazioni...",
    "dashboard.empty": "Nessuna vaccinazione registrata.",
    "dashboard.empty.hint": 'Clicca su "Aggiungi vaccinazione" per iniziare.',
    "dashboard.table.status": "Stato",
    "dashboard.table.vaccine": "Vaccino",
    "dashboard.table.date": "Data somministrazione",
    "dashboard.table.dose": "Numero dose",
    "dashboard.table.created": "Creato il",
    "dashboard.table.actions": "Azioni",
    "dashboard.vaccinationDeleted": "Vaccinazione eliminata",
    "dashboard.pending.title": "Vaccinazioni in attesa",
    "dashboard.pending.vaccine": "Vaccino",
    "dashboard.pending.empty": "Nessuna vaccinazione in attesa",
    "dashboard.edit.action": "Modifica",
    "dashboard.delete.action": "Elimina",
    "dashboard.delete.title": "Elimina vaccinazione",
    "dashboard.delete.description":
      "Sei sicuro di voler eliminare questa vaccinazione? Questa azione non può essere annullata.",
    "dashboard.delete.cancel": "Annulla",
    "dashboard.delete.confirm": "Elimina",

    // Add vaccination dialog
    "addVaccination.title": "Aggiungi vaccinazione",
    "addVaccination.description": "Aggiungi una nuova vaccinazione ai tuoi registri",
    "addVaccination.type": "Tipo di vaccinazione",
    "addVaccination.type.placeholder": "Seleziona un vaccino",
    "addVaccination.plan": "Piano di immunizzazione",
    "addVaccination.plan.placeholder": "Seleziona un piano di immunizzazione",
    "addVaccination.date": "Data somministrazione",
    "addVaccination.dose": "Numero dose",
    "addVaccination.dose.placeholder": "es. 1, 2, 3",
    "addVaccination.button": "Aggiungi vaccinazione",
    "addVaccination.saving": "Salvataggio...",
    "addVaccination.cancel": "Annulla",
    "addVaccination.success": "Vaccinazione aggiunta con successo",

    // Edit vaccination dialog
    "editVaccination.title": "Modifica vaccinazione",
    "editVaccination.vaccineName": "Nome vaccino",
    "editVaccination.save": "Salva",
    "editVaccination.saving": "Salvataggio...",
    "editVaccination.success": "Vaccinazione aggiornata",
    "editVaccination.error.missingInfo": "Informazioni mancanti",
    "editVaccination.error.missingInfoDesc":
      "Per favore compila tutti i campi obbligatori.",
    "editVaccination.error.invalidDose": "Numero dose non valido",
    "editVaccination.error.invalidDoseDesc":
      "Il numero della dose deve essere un intero positivo.",
    "editVaccination.error.notAuth": "Non autenticato",
    "editVaccination.error.notAuthDesc": "Per favore effettua l'accesso.",
    "editVaccination.error.failed": "Errore durante l'aggiornamento",
    "editVaccination.error.failedDesc":
      "Impossibile aggiornare la vaccinazione.",

    "deleteVaccination.success": "Vaccinazione eliminata con successo",

    // Validation
    "validation.required": "Campo obbligatorio",
    "validation.invalidDate": "La data deve essere nel passato",
    "validation.invalidDoseNumber": "Numero dose non valido",
    "validation.positiveNumber": "Deve essere un numero positivo",
  },
};

const LanguageContext = createContext<LanguageContextType | undefined>(
  undefined
);

export const LanguageProvider = ({ children }: { children: ReactNode }) => {
  const [language, setLanguage] = useState<Language>(() => {
    const saved = localStorage.getItem("language");
    return (saved as Language) || "de";
  });

  const handleSetLanguage = (lang: Language) => {
    setLanguage(lang);
    localStorage.setItem("language", lang);
  };

  const t = (key: string): string => {
    return translations[language][key] || key;
  };

  return (
    <LanguageContext.Provider
      value={{ language, setLanguage: handleSetLanguage, t }}
    >
      {children}
    </LanguageContext.Provider>
  );
};

export const useLanguage = () => {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error("useLanguage must be used within a LanguageProvider");
  }
  return context;
};
