package ch.ffhs.spring_boosters.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingImmunizationDto {
    private UUID immunizationPlanId;
    private String immunizationPlanName;
    private UUID vaccineTypeId;
    private String vaccineTypeName;
    private String vaccineTypeCode;
    private String ageCategoryName;
    private Integer ageMinDays;
    private Integer ageMaxDays;
    private String reason; // z.B. "Grundimmunisierung", "Auffrischung", "Follow-up"
    private Integer recommendedDoses;
    private Integer completedDoses;
    private Integer missingDoses;
    private Integer preferredAgeDays;
    private boolean isOverdue;
    private String priority; // "HIGH", "MEDIUM", "LOW"
}

