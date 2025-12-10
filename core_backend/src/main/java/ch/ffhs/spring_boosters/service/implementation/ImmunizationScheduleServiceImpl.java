package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.dto.ImmunizationSchedulRecordSortedByPriorityDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationScheduleDto;
import ch.ffhs.spring_boosters.controller.dto.PendingImmunizationDto;
import ch.ffhs.spring_boosters.controller.entity.*;
import ch.ffhs.spring_boosters.repository.AgeCategoryRepository;
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
                    .dueDate(ageCategory != null && ageCategory.getAgeMinDays() != null
                            ? birthDate.plusDays(ageCategory.getAgeMinDays())
                            : null)
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
        // Überfällig
        if (isOverdue(category, currentAgeDays)) {
            return "Überfällig";
        }

        // Ermittle Tage bis zur nächsten fälligen Dosis (vereinfachte Annahme)
        Integer nextDueAgeDays = null;
        if (category != null) {
            if (completedDoses == 0) {
                nextDueAgeDays = category.getAgeMinDays();
            } else {
                // Wenn bereits Dosen vorhanden: wir verwenden das Min-Alter als nächstes Ziel (vereinfachung)
                nextDueAgeDays = category.getAgeMinDays();
            }
        }

        if (nextDueAgeDays == null) {
            return "Bald fällig"; // Fallback
        }

        int daysUntil = nextDueAgeDays - currentAgeDays;

        // Noch <= 30 Tage -> Termin vereinbaren
        if (daysUntil <= 30) {
            return "Termin vereinbaren";
        }

        // 31 - 90 Tage -> Bald fällig
        if (daysUntil <= 90) {
            return "Bald fällig";
        }

        // Standardmäßig "Bald fällig" (sollte durch isAgeCategoryRelevant bereits eingegrenzt sein)
        return "Bald fällig";
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
