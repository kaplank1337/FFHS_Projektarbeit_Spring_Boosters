package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationRecordMapper;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/immunization-records")
@AllArgsConstructor
@Tag(name = "Impfungen", description = "API-Endpoints für die Verwaltung von Impfungen")
public class ImmunizationRecordController {

    private final ImmunizationRecordService immunizationRecordService;
    private final ImmunizationRecordMapper immunizationRecordMapper;

    @GetMapping
    @Operation(
        summary = "Alle Impfungen abrufen",
        description = "Gibt eine Liste aller Impfungen zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Liste der Impfungen erfolgreich abgerufen",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationRecordDto.class)
            )
        )
    })
    public ResponseEntity<List<ImmunizationRecordDto>> getAllImmunizationRecords() {
        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getAllImmunizationRecords();
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Impfung nach ID abrufen",
        description = "Gibt eine spezifische Impfung anhand ihrer ID zurück",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Impfung erfolgreich gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationRecordDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Impfung nicht gefunden",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ImmunizationRecordDto> getImmunizationRecordById(
        @PathVariable UUID id) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord immunizationRecord = immunizationRecordService.getImmunizationRecordById(id);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(immunizationRecord);
        return ResponseEntity.ok(immunizationRecordDto);
    }

    @PostMapping
    @Operation(
        summary = "Neue Impfung erstellen",
        description = "Erstellt eine neue Impfung im System",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Impfung erfolgreich erstellt",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImmunizationRecordDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ExceptionMessageBodyDto.class)
            )
        )
    })
    public ResponseEntity<ImmunizationRecordDto> createImmunizationRecord(
        @Valid @RequestBody ImmunizationRecordCreateDto createDto) {
        ImmunizationRecord immunizationRecord = immunizationRecordMapper.fromCreateDto(createDto);
        ImmunizationRecord createdImmunizationRecord = immunizationRecordService.createImmunizationRecord(immunizationRecord);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(createdImmunizationRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(immunizationRecordDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ImmunizationRecordDto> updateImmunizationRecord(
        @PathVariable UUID id,
        @Valid @RequestBody ImmunizationRecordUpdateDto updateDto) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord immunizationRecord = immunizationRecordMapper.fromUpdateDto(updateDto);
        ImmunizationRecord updatedImmunizationRecord = immunizationRecordService.updateImmunizationRecord(id, immunizationRecord);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(updatedImmunizationRecord);
        return ResponseEntity.ok(immunizationRecordDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImmunizationRecord(
        @PathVariable UUID id, Principal principal) throws ImmunizationRecordNotFoundException {
        var userId = ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        immunizationRecordService.deleteImmunizationRecord(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ImmunizationRecordDto>> getImmunizationRecordsByUser(
        @PathVariable UUID userId) {
        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getImmunizationRecordsByUser(userId);
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }

    @GetMapping("/by-vaccine-type/{vaccineTypeId}")
    public ResponseEntity<List<ImmunizationRecordDto>> getImmunizationRecordsByVaccineType(
        @PathVariable UUID vaccineTypeId) {
        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getImmunizationRecordsByVaccineType(vaccineTypeId);
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }

    @GetMapping("/by-user/{userId}/vaccine-type/{vaccineTypeId}")
    public ResponseEntity<List<ImmunizationRecordDto>> getImmunizationRecordsByUserAndVaccineType(
        @PathVariable UUID userId,
        @PathVariable UUID vaccineTypeId) {
        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getImmunizationRecordsByUserAndVaccineType(userId, vaccineTypeId);
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }

    @GetMapping("/myVaccinations")
    public ResponseEntity<List<ImmunizationRecordDto>> getMyImmunizationRecords(Principal principal) {
        UUID userId;
        try {
            userId = ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getImmunizationRecordsByUser(userId);
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }

    @ExceptionHandler({ImmunizationRecordNotFoundException.class})
    public ResponseEntity<ExceptionMessageBodyDto> handleImmunizationRecordNotFoundException(
            ImmunizationRecordNotFoundException ex,
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
}
