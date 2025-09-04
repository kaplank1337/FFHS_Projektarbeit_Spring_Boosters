package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.AgeCategoryCreateDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryDto;
import ch.ffhs.spring_boosters.controller.dto.AgeCategoryUpdateDto;
import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.entity.AgeCategory;
import ch.ffhs.spring_boosters.controller.mapper.AgeCategoryMapper;
import ch.ffhs.spring_boosters.service.AgeCategoryService;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.AgeCategoryNotFoundException;
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
@RequestMapping("/api/v1/age-categories")
@AllArgsConstructor
@Tag(name = "Alterskategorien", description = "API-Endpoints für die Verwaltung von Alterskategorien")
public class AgeCategoryController {

    private final AgeCategoryService ageCategoryService;
    private final AgeCategoryMapper ageCategoryMapper;

    @GetMapping
    @Operation(
        summary = "Alle Alterskategorien abrufen",
        description = "Gibt eine Liste aller verfügbaren Alterskategorien zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Alterskategorien erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AgeCategoryDto.class)
            )
        )
    })
    public ResponseEntity<List<AgeCategoryDto>> getAllAgeCategories() {
        List<AgeCategory> ageCategories = ageCategoryService.getAllAgeCategories();
        List<AgeCategoryDto> ageCategoryDtos = ageCategoryMapper.toDtoList(ageCategories);
        return ResponseEntity.ok(ageCategoryDtos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Alterskategorie nach ID abrufen",
        description = "Gibt eine spezifische Alterskategorie anhand ihrer ID zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Alterskategorie erfolgreich gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AgeCategoryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Alterskategorie nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<AgeCategoryDto> getAgeCategoryById(
        @Parameter(description = "ID der Alterskategorie", required = true)
        @PathVariable UUID id) throws AgeCategoryNotFoundException {
        AgeCategory ageCategory = ageCategoryService.getAgeCategoryById(id);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(ageCategory);
        return ResponseEntity.ok(ageCategoryDto);
    }

    @PostMapping
    @Operation(
        summary = "Neue Alterskategorie erstellen",
        description = "Erstellt eine neue Alterskategorie im System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Alterskategorie erfolgreich erstellt",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AgeCategoryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten oder Alterskategorie existiert bereits",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<AgeCategoryDto> createAgeCategory(
        @Parameter(description = "Daten für die neue Alterskategorie", required = true)
        @Valid @RequestBody AgeCategoryCreateDto createDto) throws AgeCategoryAlreadyExistsException {
        AgeCategory ageCategory = ageCategoryMapper.fromCreateDto(createDto);
        AgeCategory createdAgeCategory = ageCategoryService.createAgeCategory(ageCategory);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(createdAgeCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(ageCategoryDto);
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Alterskategorie aktualisieren",
        description = "Aktualisiert eine bestehende Alterskategorie",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Alterskategorie erfolgreich aktualisiert",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AgeCategoryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Alterskategorie nicht gefunden",
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
    public ResponseEntity<AgeCategoryDto> updateAgeCategory(
        @Parameter(description = "ID der zu aktualisierenden Alterskategorie", required = true)
        @PathVariable UUID id,
        @Parameter(description = "Aktualisierte Daten für die Alterskategorie", required = true)
        @Valid @RequestBody AgeCategoryUpdateDto updateDto) throws AgeCategoryNotFoundException, AgeCategoryAlreadyExistsException {
        AgeCategory ageCategory = ageCategoryMapper.fromUpdateDto(updateDto);
        AgeCategory updatedAgeCategory = ageCategoryService.updateAgeCategory(id, ageCategory);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(updatedAgeCategory);
        return ResponseEntity.ok(ageCategoryDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Alterskategorie löschen",
        description = "Löscht eine Alterskategorie aus dem System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Alterskategorie erfolgreich gelöscht"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Alterskategorie nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<Void> deleteAgeCategory(
        @Parameter(description = "ID der zu löschenden Alterskategorie", required = true)
        @PathVariable UUID id) throws AgeCategoryNotFoundException {
        ageCategoryService.deleteAgeCategory(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({AgeCategoryNotFoundException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleAgeCategoryNotFoundException(
            AgeCategoryNotFoundException ex,
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

    @ExceptionHandler({AgeCategoryAlreadyExistsException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleAgeCategoryAlreadyExistsException(
            AgeCategoryAlreadyExistsException ex,
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
