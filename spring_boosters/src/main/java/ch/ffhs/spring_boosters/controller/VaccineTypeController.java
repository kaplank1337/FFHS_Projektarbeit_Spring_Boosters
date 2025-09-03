package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeListDto;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.controller.mapper.VaccineTypeMapper;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import ch.ffhs.spring_boosters.service.VaccineTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vaccine-types")
@AllArgsConstructor
@Tag(name = "Impfstoff-Typen", description = "API-Endpoints für die Verwaltung von Impfstoff-Typen")
public class VaccineTypeController {

    private final VaccineTypeService vaccineTypeService;
    private final VaccineTypeMapper vaccineTypeMapper;

    @GetMapping
    @Operation(
        summary = "Alle Impfstoff-Typen abrufen",
        description = "Gibt eine Liste aller verfügbaren Impfstoff-Typen zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Impfstoff-Typen erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VaccineTypeListDto.class),
                examples = @ExampleObject(
                    name = "Impfstoff-Typen Liste",
                    value = """
                    {
                        "vaccineTypes": [
                            {
                                "name": "COVID-19 mRNA Pfizer-BioNTech",
                                "code": "COVID-PFZ",
                                "vaccineTypeActiveSubstances": [
                                    {
                                        "qualitativeAmount": "30 μg",
                                        "activeSubstance": {
                                            "name": "BNT162b2",
                                            "synonyms": ["Pfizer-BioNTech", "Comirnaty"]
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert oder ungültiges Token"
        )
    })
    public ResponseEntity<VaccineTypeListDto> getVaccineTypes() {
        List<VaccineType> vaccineTypes = vaccineTypeService.getVaccineTypes();
        VaccineTypeListDto vaccineTypeListDto = vaccineTypeMapper.vaccineTypeListDto(vaccineTypes);
        return ResponseEntity.status(HttpStatus.OK).body(vaccineTypeListDto);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Impfstoff-Typ nach ID abrufen",
        description = "Gibt einen spezifischen Impfstoff-Typ anhand seiner ID zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Impfstoff-Typ erfolgreich gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = VaccineTypeDto.class),
                examples = @ExampleObject(
                    name = "Einzelner Impfstoff-Typ",
                    value = """
                    {
                        "name": "COVID-19 mRNA Pfizer-BioNTech",
                        "code": "COVID-PFZ",
                        "vaccineTypeActiveSubstances": [
                            {
                                "qualitativeAmount": "30 μg",
                                "activeSubstance": {
                                    "name": "BNT162b2",
                                    "synonyms": ["Pfizer-BioNTech", "Comirnaty"]
                                }
                            }
                        ]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Nicht authentifiziert oder ungültiges Token"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Impfstoff-Typ nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<VaccineTypeDto> getVaccineTypeById(
        @Parameter(description = "UUID des Impfstoff-Typs", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable UUID id) throws VaccineTypeNotFoundException {
        VaccineType vaccineType = vaccineTypeService.getVaccineType(id);
        VaccineTypeDto vaccineTypeDto = vaccineTypeMapper.vaccineTypeToDto(vaccineType);
        return ResponseEntity.status(HttpStatus.OK).body(vaccineTypeDto);
    }

    @ExceptionHandler(VaccineTypeNotFoundException.class)
    public ResponseEntity<ExceptionMessageBodyDto> handleVaccineTypeNotFoundException(
            VaccineTypeNotFoundException ex,
            HttpServletRequest request) {

        ExceptionMessageBodyDto errorResponse = new ExceptionMessageBodyDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Vaccine Type Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getSimpleName()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
