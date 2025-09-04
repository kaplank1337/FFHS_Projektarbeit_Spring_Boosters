package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceDto;
import ch.ffhs.spring_boosters.controller.dto.ActiveSubstanceUpdateDto;
import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.entity.ActiveSubstance;
import ch.ffhs.spring_boosters.controller.mapper.ActiveSubstanceMapper;
import ch.ffhs.spring_boosters.service.ActiveSubstanceService;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ActiveSubstanceNotFoundException;
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
@RequestMapping("/api/v1/active-substances")
@AllArgsConstructor
@Tag(name = "Wirkstoffe", description = "API-Endpoints für die Verwaltung von Wirkstoffen")
public class ActiveSubstanceController {

    private final ActiveSubstanceService activeSubstanceService;
    private final ActiveSubstanceMapper activeSubstanceMapper;

    @GetMapping
    @Operation(
        summary = "Alle Wirkstoffe abrufen",
        description = "Gibt eine Liste aller verfügbaren Wirkstoffe zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Wirkstoffe erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActiveSubstanceDto.class)
            )
        )
    })
    public ResponseEntity<List<ActiveSubstanceDto>> getAllActiveSubstances() {
        List<ActiveSubstance> activeSubstances = activeSubstanceService.getAllActiveSubstances();
        List<ActiveSubstanceDto> activeSubstanceDtos = activeSubstanceMapper.toDtoList(activeSubstances);
        return ResponseEntity.ok(activeSubstanceDtos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Wirkstoff nach ID abrufen",
        description = "Gibt einen spezifischen Wirkstoff anhand seiner ID zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Wirkstoff erfolgreich gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActiveSubstanceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Wirkstoff nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ActiveSubstanceDto> getActiveSubstanceById(
        @Parameter(description = "ID des Wirkstoffs", required = true)
        @PathVariable UUID id) throws ActiveSubstanceNotFoundException {
        ActiveSubstance activeSubstance = activeSubstanceService.getActiveSubstanceById(id);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(activeSubstance);
        return ResponseEntity.ok(activeSubstanceDto);
    }

    @PostMapping
    @Operation(
        summary = "Neuen Wirkstoff erstellen",
        description = "Erstellt einen neuen Wirkstoff im System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Wirkstoff erfolgreich erstellt",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActiveSubstanceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten oder Wirkstoff existiert bereits",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ActiveSubstanceDto> createActiveSubstance(
        @Parameter(description = "Daten für den neuen Wirkstoff", required = true)
        @Valid @RequestBody ActiveSubstanceCreateDto createDto) throws ActiveSubstanceAlreadyExistsException {
        ActiveSubstance activeSubstance = activeSubstanceMapper.fromCreateDto(createDto);
        ActiveSubstance createdActiveSubstance = activeSubstanceService.createActiveSubstance(activeSubstance);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(createdActiveSubstance);
        return ResponseEntity.status(HttpStatus.CREATED).body(activeSubstanceDto);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Wirkstoff aktualisieren",
        description = "Aktualisiert einen bestehenden Wirkstoff",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Wirkstoff erfolgreich aktualisiert",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActiveSubstanceDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Wirkstoff nicht gefunden",
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
    public ResponseEntity<ActiveSubstanceDto> updateActiveSubstance(
        @Parameter(description = "ID des zu aktualisierenden Wirkstoffs", required = true)
        @PathVariable UUID id,
        @Parameter(description = "Aktualisierte Daten für den Wirkstoff", required = true)
        @Valid @RequestBody ActiveSubstanceUpdateDto updateDto) throws ActiveSubstanceNotFoundException, ActiveSubstanceAlreadyExistsException {
        ActiveSubstance activeSubstance = activeSubstanceMapper.fromUpdateDto(updateDto);
        ActiveSubstance updatedActiveSubstance = activeSubstanceService.updateActiveSubstance(id, activeSubstance);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(updatedActiveSubstance);
        return ResponseEntity.ok(activeSubstanceDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Wirkstoff löschen",
        description = "Löscht einen Wirkstoff aus dem System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Wirkstoff erfolgreich gelöscht"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Wirkstoff nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<Void> deleteActiveSubstance(
        @Parameter(description = "ID des zu löschenden Wirkstoffs", required = true)
        @PathVariable UUID id) throws ActiveSubstanceNotFoundException {
        activeSubstanceService.deleteActiveSubstance(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ActiveSubstanceNotFoundException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleActiveSubstanceNotFoundException(
            ActiveSubstanceNotFoundException ex,
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

    @ExceptionHandler({ActiveSubstanceAlreadyExistsException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleActiveSubstanceAlreadyExistsException(
            ActiveSubstanceAlreadyExistsException ex,
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
