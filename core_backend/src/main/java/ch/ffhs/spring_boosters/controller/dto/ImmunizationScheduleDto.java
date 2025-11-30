package ch.ffhs.spring_boosters.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImmunizationScheduleDto {
    private UUID userId;
    private String username;
    private LocalDate birthDate;
    private Integer currentAgeDays;
    private List<PendingImmunizationDto> pendingImmunizations;
    private Integer totalPending;
    private Integer overdueCount;          // Überfällig
    private Integer dueSoonCount;           // Noch <= 30 Tage
    private Integer upcomingDueCount;          // 31 - 90 Tage
}
