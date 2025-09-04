package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationPlanMapper;
import ch.ffhs.spring_boosters.service.ImmunizationPlanService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/immunization-plans")
@AllArgsConstructor
@Tag(name = "Impfpläne", description = "API-Endpoints für die Verwaltung von Impfplänen")
public class ImmunizationPlanController {

    private final ImmunizationPlanService immunizationPlanService;
    private final ImmunizationPlanMapper immunizationPlanMapper;

    @GetMapping
    @Operation(
        summary = "Alle Impfpläne abrufen",
        description = "Gibt eine Liste aller verfügbaren Impfpläne zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Impfpläne erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        )
    })
    public ResponseEntity<List<ImmunizationPlanDto>> getAllImmunizationPlans() {
        List<ImmunizationPlan> immunizationPlans = immunizationPlanService.getAllImmunizationPlans();
        List<ImmunizationPlanDto> immunizationPlanDtos = immunizationPlanMapper.toDtoList(immunizationPlans);
        return ResponseEntity.ok(immunizationPlanDtos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Impfplan nach ID abrufen",
        description = "Gibt einen spezifischen Impfplan anhand seiner ID zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Impfplan erfolgreich gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Impfplan nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ImmunizationPlanDto> getImmunizationPlanById(
        @Parameter(description = "ID des Impfplans", required = true)
        @PathVariable UUID id) throws ImmunizationPlanNotFoundException {
        ImmunizationPlan immunizationPlan = immunizationPlanService.getImmunizationPlanById(id);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(immunizationPlan);
        return ResponseEntity.ok(immunizationPlanDto);
    }

    @PostMapping
    @Operation(
        summary = "Neuen Impfplan erstellen",
        description = "Erstellt einen neuen Impfplan im System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Impfplan erfolgreich erstellt",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten oder Impfplan existiert bereits",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ImmunizationPlanDto> createImmunizationPlan(
        @Parameter(description = "Daten für den neuen Impfplan", required = true)
        @Valid @RequestBody ImmunizationPlanCreateDto createDto) throws ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan immunizationPlan = immunizationPlanMapper.fromCreateDto(createDto);
        ImmunizationPlan createdImmunizationPlan = immunizationPlanService.createImmunizationPlan(immunizationPlan);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(createdImmunizationPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(immunizationPlanDto);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Impfplan aktualisieren",
        description = "Aktualisiert einen bestehenden Impfplan",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Impfplan erfolgreich aktualisiert",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Impfplan nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten oder Name bereits vergeben",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ImmunizationPlanDto> updateImmunizationPlan(
        @Parameter(description = "ID des zu aktualisierenden Impfplans", required = true)
        @PathVariable UUID id,
        @Parameter(description = "Aktualisierte Daten für den Impfplan", required = true)
        @Valid @RequestBody ImmunizationPlanUpdateDto updateDto) throws ImmunizationPlanNotFoundException, ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan immunizationPlan = immunizationPlanMapper.fromUpdateDto(updateDto);
        ImmunizationPlan updatedImmunizationPlan = immunizationPlanService.updateImmunizationPlan(id, immunizationPlan);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(updatedImmunizationPlan);
        return ResponseEntity.ok(immunizationPlanDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Impfplan löschen",
        description = "Löscht einen Impfplan aus dem System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Impfplan erfolgreich gelöscht"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Impfplan nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<Void> deleteImmunizationPlan(
        @Parameter(description = "ID des zu löschenden Impfplans", required = true)
        @PathVariable UUID id) throws ImmunizationPlanNotFoundException {
        immunizationPlanService.deleteImmunizationPlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-vaccine-type/{vaccineTypeId}")
    @Operation(
        summary = "Impfpläne nach Impfstoff-Typ abrufen",
        description = "Gibt alle Impfpläne für einen bestimmten Impfstoff-Typ zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Impfpläne erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        )
    })
    public ResponseEntity<List<ImmunizationPlanDto>> getImmunizationPlansByVaccineType(
        @Parameter(description = "ID des Impfstoff-Typs", required = true)
        @PathVariable UUID vaccineTypeId) {
        List<ImmunizationPlan> immunizationPlans = immunizationPlanService.getImmunizationPlansByVaccineType(vaccineTypeId);
        List<ImmunizationPlanDto> immunizationPlanDtos = immunizationPlanMapper.toDtoList(immunizationPlans);
        return ResponseEntity.ok(immunizationPlanDtos);
    }

    @GetMapping("/by-age-category/{ageCategoryId}")
    @Operation(
        summary = "Impfpläne nach Alterskategorie abrufen",
        description = "Gibt alle Impfpläne für eine bestimmte Alterskategorie zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Impfpläne erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationPlanDto.class)
            )
        )
    })
    public ResponseEntity<List<ImmunizationPlanDto>> getImmunizationPlansByAgeCategory(
        @Parameter(description = "ID der Alterskategorie", required = true)
        @PathVariable UUID ageCategoryId) {
        List<ImmunizationPlan> immunizationPlans = immunizationPlanService.getImmunizationPlansByAgeCategory(ageCategoryId);
        List<ImmunizationPlanDto> immunizationPlanDtos = immunizationPlanMapper.toDtoList(immunizationPlans);
        return ResponseEntity.ok(immunizationPlanDtos);
    }

    @ExceptionHandler({ImmunizationPlanNotFoundException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleImmunizationPlanNotFoundException(
            ImmunizationPlanNotFoundException ex,
            HttpServletRequest request) {
        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({ImmunizationPlanAlreadyExistsException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleImmunizationPlanAlreadyExistsException(
            ImmunizationPlanAlreadyExistsException ex,
            HttpServletRequest request) {
        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
