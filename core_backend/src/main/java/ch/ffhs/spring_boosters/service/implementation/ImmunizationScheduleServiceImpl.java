package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationSchedulRecordSortedByPriorityDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.PendingImmunizationDto;
import ch.ffhs.spring_boosters.controller.entity.*;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
import ch.ffhs.spring_boosters.repository.FollowUpRuleRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationPlanRepository;
import ch.ffhs.spring_boosters.repository.ImmunizationRecordRepository;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationScheduleService;
import ch.ffhs.spring_boosters.service.implementation.enumerator.PriorityEnum;
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
    private final FollowUpRuleRepository followUpRuleRepository;

    @Override
    @Transactional(readOnly = true)
    public ImmunizationScheduleDto getPendingImmunizations(UUID userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Benutzer mit ID " + userId + " nicht gefunden"));

        LocalDate birthDate = user.getBirthDate();
        int currentAgeDays = (int) ChronoUnit.DAYS.between(birthDate, LocalDate.now());

        List<ImmunizationRecord> existingRecords = immunizationRecordRepository.findByUserId(userId);

        if (existingRecords.isEmpty()) {
            return ImmunizationScheduleDto.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .birthDate(user.getBirthDate())
                    .currentAgeDays(currentAgeDays)
                    .pendingImmunizations(List.of())
                    .totalPending(0)
                    .overdueCount(0)
                    .dueSoonCount(0)
                    .upcomingDueCount(0)
                    .build();
        }

        Set<UUID> startedPlanIds = existingRecords.stream()
                .map(ImmunizationRecord::getImmunizationPlanId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<UUID, Long> completedDosesByPlan = existingRecords.stream()
                .filter(r -> r.getImmunizationPlanId() != null)
                .collect(Collectors.groupingBy(
                        ImmunizationRecord::getImmunizationPlanId,
                        Collectors.counting()
                ));

        List<ImmunizationPlan> allPlans = immunizationPlanRepository.findAll().stream()
                .filter(p -> startedPlanIds.contains(p.getId()))
                .toList();

        List<AgeCategory> relevantAgeCategories = ageCategoryRepository.findAll().stream()
                .filter(cat -> isAgeCategoryRelevant(cat, currentAgeDays))
                .toList();

        Set<UUID> relevantAgeCategoryIds = relevantAgeCategories.stream()
                .map(AgeCategory::getId)
                .collect(Collectors.toSet());

        List<PendingImmunizationDto> pendingImmunizations = new ArrayList<>();

        for (ImmunizationPlan plan : allPlans) {
            if (!relevantAgeCategoryIds.contains(plan.getAgeCategoryId())) {
                continue;
            }

            long completedDoses = completedDosesByPlan.getOrDefault(plan.getId(), 0L);

            int requiredDoses = plan.getImmunizationPlanSeries() != null
                    ? plan.getImmunizationPlanSeries().stream()
                    .mapToInt(ImmunizationPlanSeries::getRequiredDoses)
                    .sum()
                    : 1;

            if (completedDoses >= requiredDoses) {
                continue;
            }

            AgeCategory ageCategory = relevantAgeCategories.stream()
                    .filter(cat -> cat.getId().equals(plan.getAgeCategoryId()))
                    .findFirst()
                    .orElse(null);

            VaccineType vaccineType = plan.getVaccineType();

            List<ImmunizationRecord> planRecords = existingRecords.stream()
                    .filter(r -> plan.getId().equals(r.getImmunizationPlanId()))
                    .toList();

            // Finde das Datum der letzten Impfung für diesen Plan
            LocalDate lastDoseDate = planRecords.stream()
                    .map(ImmunizationRecord::getAdministeredOn)
                    .filter(Objects::nonNull)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            // Berechne Fälligkeit und Priorität mit verbesserter Logik
            String priority = determinePriority(ageCategory, plan, currentAgeDays, completedDoses, lastDoseDate, birthDate);
            LocalDate calculatedDueDate = calculateDueDate(ageCategory, plan, completedDoses, lastDoseDate, birthDate);

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
                    .isOverdue("Überfällig".equals(priority))
                    .priority(priority)
                    .dueDate(calculatedDueDate)
                    .build();

            pendingImmunizations.add(pending);
        }

        pendingImmunizations.sort(Comparator
                .comparing((PendingImmunizationDto p) -> getPriorityOrder(p.getPriority()))
                .thenComparing(PendingImmunizationDto::getAgeMinDays, Comparator.nullsLast(Comparator.naturalOrder())));

        long ueberfaellig = pendingImmunizations.stream().filter(p -> "Überfällig".equals(p.getPriority())).count();
        long terminVereinbaren = pendingImmunizations.stream().filter(p -> "Termin vereinbaren".equals(p.getPriority())).count();
        long baldFaellig = pendingImmunizations.stream().filter(p -> "Bald fällig".equals(p.getPriority())).count();

        return ImmunizationScheduleDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .birthDate(user.getBirthDate())
                .currentAgeDays(currentAgeDays)
                .pendingImmunizations(pendingImmunizations)
                .totalPending(pendingImmunizations.size())
                .overdueCount((int) ueberfaellig)
                .dueSoonCount((int) terminVereinbaren)
                .upcomingDueCount((int) baldFaellig)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ImmunizationSchedulRecordSortedByPriorityDto getImmunizationRecordsByUserIdAndFilterByDueStatus(
            UUID userId,
            PriorityEnum priorityEnum
    ) {
        try {
            ImmunizationScheduleDto schedule = getPendingImmunizations(userId);

            String targetPriority = switch (priorityEnum) {
                case OVERDUE -> "Überfällig";
                case DUE_SOON -> "Termin vereinbaren";
                case UPCOMING -> "Bald fällig";
            };

            // Gefilterte Pending-Immunizations -> VaccinationNameDto mappen
            List<String> vaccinationNames = schedule.getPendingImmunizations().stream()
                    .filter(p -> targetPriority.equals(p.getPriority()))
                    .map(PendingImmunizationDto::getVaccineTypeName
                    )
                    .toList();

            return new ImmunizationSchedulRecordSortedByPriorityDto(vaccinationNames);
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        return currentAgeDays <= category.getAgeMaxDays();
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

    private String determinePriority(
            AgeCategory category,
            ImmunizationPlan plan,
            int currentAgeDays,
            long completedDoses,
            LocalDate lastDoseDate,
            LocalDate birthDate
    ) {
        // Fall 1: Noch gar keine Impfung -> Standardlogik basierend auf Alter
        if (completedDoses == 0) {
            if (isOverdue(category, currentAgeDays)) {
                return "Überfällig";
            }
            if (category != null && category.getAgeMinDays() != null) {
                int daysUntilMinAge = category.getAgeMinDays() - currentAgeDays;
                if (daysUntilMinAge <= 30) return "Termin vereinbaren";
                if (daysUntilMinAge <= 90) return "Bald fällig";
            }
            return "Bald fällig";
        }

        // Fall 2: Folgeimpfungen -> Logik basierend auf Intervall zur letzten Dosis
        if (lastDoseDate == null) {
            return "Bald fällig";
        }

        long intervalDays = getIntervalDays(plan, completedDoses);
        LocalDate dueDate = lastDoseDate.plusDays(intervalDays);
        long daysUntilDue = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        if (daysUntilDue < 0) {
            return "Überfällig";
        } else if (daysUntilDue <= 30) {
            return "Termin vereinbaren";
        } else if (daysUntilDue <= 90) {
            return "Bald fällig";
        } else {
            return "Bald fällig";
        }
    }

    private LocalDate calculateDueDate(AgeCategory category, ImmunizationPlan plan, long completedDoses, LocalDate lastDoseDate, LocalDate birthDate) {
        if (completedDoses == 0) {
            return (category != null && category.getAgeMinDays() != null)
                    ? birthDate.plusDays(category.getAgeMinDays())
                    : null;
        } else {
            long daysToNextDose = getIntervalDays(plan, completedDoses);
            return (lastDoseDate != null) ? lastDoseDate.plusDays(daysToNextDose) : null;
        }
    }

    private long getIntervalDays(ImmunizationPlan plan, long completedDoses) {
        // Versuche, eine Regel aus der Datenbank zu laden
        return followUpRuleRepository.findByFromPlanIdAndMinCompletedDoses(plan.getId(), (int) completedDoses)
                .map(rule -> (long) rule.getMinIntervalDaysSinceLast())
                .orElse(28L); // Fallback: 28 Tage (4 Wochen), wenn keine Regel existiert
    }

    private int getPriorityOrder(String priority) {
        return switch (priority) {
            case "Überfällig" -> 1;
            case "Termin vereinbaren" -> 2;
            case "Bald fällig" -> 3;
            default -> 4;
        };
    }
}
