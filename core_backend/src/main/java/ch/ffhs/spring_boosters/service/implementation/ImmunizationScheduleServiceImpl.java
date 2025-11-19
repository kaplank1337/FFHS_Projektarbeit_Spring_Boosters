package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.PendingImmunizationDto;
import ch.ffhs.spring_boosters.controller.entity.*;
import ch.ffhs.spring_boosters.repository.*;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImmunizationScheduleServiceImpl implements ImmunizationScheduleService {

    private final UserRepository userRepository;
    private final ImmunizationPlanRepository immunizationPlanRepository;
    private final ImmunizationRecordRepository immunizationRecordRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public ImmunizationScheduleDto getPendingImmunizations(UUID userId) throws UserNotFoundException {
        // 1. Benutzer laden
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Benutzer mit ID " + userId + " nicht gefunden"));

        // 2. Alter in Tagen berechnen
        LocalDate birthDate = user.getBirthDate();
        int currentAgeDays = (int) ChronoUnit.DAYS.between(birthDate, LocalDate.now());

        // 3. Bereits erfasste Impfungen des Benutzers laden
        List<ImmunizationRecord> existingRecords = immunizationRecordRepository.findByUserId(userId);

        // Gruppiere nach Impfplan-ID und zähle Dosen
        Map<UUID, Long> completedDosesByPlan = existingRecords.stream()
                .collect(Collectors.groupingBy(
                        ImmunizationRecord::getImmunizationPlanId,
                        Collectors.counting()
                ));


        // 4. Alle Impfpläne laden
        List<ImmunizationPlan> allPlans = immunizationPlanRepository.findAll();

        // 5. Relevante Alterskategorien finden
        List<AgeCategory> relevantAgeCategories = ageCategoryRepository.findAll().stream()
                .filter(cat -> isAgeCategoryRelevant(cat, currentAgeDays))
                .toList();

        Set<UUID> relevantAgeCategoryIds = relevantAgeCategories.stream()
                .map(AgeCategory::getId)
                .collect(Collectors.toSet());

        // 6. Ausstehende Impfungen berechnen
        List<PendingImmunizationDto> pendingImmunizations = new ArrayList<>();

        for (ImmunizationPlan plan : allPlans) {
            // Prüfe ob Plan für aktuelles Alter relevant ist
            if (!relevantAgeCategoryIds.contains(plan.getAgeCategoryId())) {
                continue;
            }

            // Prüfe ob bereits vollständig geimpft
            long completedDoses = completedDosesByPlan.getOrDefault(plan.getId(), 0L);

            // Ermittle erforderliche Dosen aus Series
            int requiredDoses = plan.getImmunizationPlanSeries() != null
                    ? plan.getImmunizationPlanSeries().stream()
                            .mapToInt(ImmunizationPlanSeries::getRequiredDoses)
                            .sum()
                    : 1; // Default: 1 Dosis

            if (completedDoses >= requiredDoses) {
                continue; // Bereits vollständig geimpft
            }

            // Erstelle PendingImmunizationDto
            AgeCategory ageCategory = relevantAgeCategories.stream()
                    .filter(cat -> cat.getId().equals(plan.getAgeCategoryId()))
                    .findFirst()
                    .orElse(null);

            VaccineType vaccineType = plan.getVaccineType();

            PendingImmunizationDto pending = PendingImmunizationDto.builder()
                    .immunizationPlanId(plan.getId())
                    .immunizationPlanName(plan.getName())
                    .vaccineTypeId(vaccineType != null ? vaccineType.getId() : null)
                    .vaccineTypeName(vaccineType != null ? vaccineType.getName() : "Unbekannt")
                    .vaccineTypeCode(vaccineType != null ? vaccineType.getCode() : null)
                    .ageCategoryName(ageCategory != null ? ageCategory.getName() : "Unbekannt")
                    .ageMinDays(ageCategory != null ? ageCategory.getAgeMinDays() : null)
                    .ageMaxDays(ageCategory != null ? ageCategory.getAgeMaxDays() : null)
                    .reason(determineReason(completedDoses, requiredDoses))
                    .recommendedDoses(requiredDoses)
                    .completedDoses((int) completedDoses)
                    .missingDoses(requiredDoses - (int) completedDoses)
                    .isOverdue(isOverdue(ageCategory, currentAgeDays))
                    .priority(determinePriority(ageCategory, currentAgeDays, completedDoses, requiredDoses))
                    .build();

            pendingImmunizations.add(pending);
        }

        // 7. Nach Priorität sortieren
        pendingImmunizations.sort(Comparator
                .comparing((PendingImmunizationDto p) -> getPriorityOrder(p.getPriority()))
                .thenComparing(PendingImmunizationDto::getAgeMinDays, Comparator.nullsLast(Comparator.naturalOrder())));

        // 8. Zusammenfassung erstellen
        long highPriority = pendingImmunizations.stream().filter(p -> "HIGH".equals(p.getPriority())).count();
        long mediumPriority = pendingImmunizations.stream().filter(p -> "MEDIUM".equals(p.getPriority())).count();
        long lowPriority = pendingImmunizations.stream().filter(p -> "LOW".equals(p.getPriority())).count();

        return ImmunizationScheduleDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .birthDate(user.getBirthDate())
                .currentAgeDays(currentAgeDays)
                .pendingImmunizations(pendingImmunizations)
                .totalPending(pendingImmunizations.size())
                .highPriority((int) highPriority)
                .mediumPriority((int) mediumPriority)
                .lowPriority((int) lowPriority)
                .build();
    }

    private boolean isAgeCategoryRelevant(AgeCategory category, int currentAgeDays) {
        // Prüfe ob das aktuelle Alter in die Kategorie passt oder noch ansteht
        if (currentAgeDays < category.getAgeMinDays()) {
            // Noch nicht in dieser Kategorie, aber bald relevant (innerhalb 90 Tage)
            return (category.getAgeMinDays() - currentAgeDays) <= 90;
        }

        if (category.getAgeMaxDays() == null) {
            // Offene Kategorie (z.B. "Erwachsene")
            return true;
        }

        // In aktueller Kategorie oder kürzlich überschritten (Grace Period: 365 Tage)
        return currentAgeDays <= category.getAgeMaxDays() + 365;
    }

    private boolean isOverdue(AgeCategory category, int currentAgeDays) {
        if (category == null || category.getAgeMaxDays() == null) {
            return false;
        }
        return currentAgeDays > category.getAgeMaxDays();
    }

    private String determineReason(long completedDoses, int requiredDoses) {
        if (completedDoses == 0) {
            return "Grundimmunisierung";
        } else if (completedDoses < requiredDoses) {
            return "Fortsetzung Grundimmunisierung";
        } else {
            return "Auffrischung";
        }
    }

    private String determinePriority(AgeCategory category, int currentAgeDays, long completedDoses, int requiredDoses) {
        // Überfällig = HIGH
        if (isOverdue(category, currentAgeDays)) {
            return "HIGH";
        }

        // Grundimmunisierung noch nicht begonnen = HIGH
        if (completedDoses == 0 && currentAgeDays >= category.getAgeMinDays()) {
            return "HIGH";
        }

        // In aktuellem Altersfenster = MEDIUM
        if (currentAgeDays >= category.getAgeMinDays() &&
            (category.getAgeMaxDays() == null || currentAgeDays <= category.getAgeMaxDays())) {
            return "MEDIUM";
        }

        // Zukünftig = LOW
        return "LOW";
    }

    private int getPriorityOrder(String priority) {
        return switch (priority) {
            case "HIGH" -> 1;
            case "MEDIUM" -> 2;
            case "LOW" -> 3;
            default -> 4;
        };
    }
}

