package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.config.JwtTokenReader;
import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationRecordUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationRecord;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationRecordMapper;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationRecordNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationRecordService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/immunization-records")
@AllArgsConstructor
@Tag(name = "Impfungen", description = "API-Endpoints für die Verwaltung von Impfungen")
public class ImmunizationRecordController {

    private final ImmunizationRecordService immunizationRecordService;
    private final ImmunizationRecordMapper immunizationRecordMapper;
    private final JwtTokenReader jwtTokenReader;

    @GetMapping
    public ResponseEntity<List<ImmunizationRecordDto>> getAllImmunizationRecords(
            @RequestHeader(value = "Authorization", required = false) String authToken
    ) {

        List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getAllImmunizationRecords(getUserIdFromToken(authToken));
        List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
        return ResponseEntity.ok(immunizationRecordDtos);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ImmunizationRecordDto> getImmunizationRecordById(
        @PathVariable UUID id) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord immunizationRecord = immunizationRecordService.getImmunizationRecordById(id);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(immunizationRecord);
        return ResponseEntity.ok(immunizationRecordDto);
    }

    @PostMapping
    public ResponseEntity<ImmunizationRecordDto> createImmunizationRecord(
        @Valid @RequestBody ImmunizationRecordCreateDto createDto,
        @RequestHeader("Authorization") String authToken) {
        ImmunizationRecord immunizationRecord = immunizationRecordMapper.fromCreateDto(createDto, getUserIdFromToken(authToken));
        ImmunizationRecord createdImmunizationRecord = immunizationRecordService.createImmunizationRecord(immunizationRecord);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(createdImmunizationRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(immunizationRecordDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ImmunizationRecordDto> updateImmunizationRecord(
        @PathVariable UUID id,
        @Valid @RequestBody ImmunizationRecordUpdateDto updateDto,
        @RequestHeader("Authorization") String authToken) throws ImmunizationRecordNotFoundException {
        ImmunizationRecord immunizationRecord = immunizationRecordMapper.fromUpdateDto(updateDto, getUserIdFromToken(authToken));
        ImmunizationRecord updatedImmunizationRecord = immunizationRecordService.updateImmunizationRecord(id, immunizationRecord);
        ImmunizationRecordDto immunizationRecordDto = immunizationRecordMapper.toDto(updatedImmunizationRecord);
        return ResponseEntity.ok(immunizationRecordDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImmunizationRecord(
        @PathVariable UUID id,
        @RequestHeader("Authorization") String authToken) throws ImmunizationRecordNotFoundException {

        try {
            immunizationRecordService.deleteImmunizationRecord(getUserIdFromToken(authToken), id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
    public ResponseEntity<List<ImmunizationRecordDto>> getMyImmunizationRecords(
            @RequestHeader("Authorization") String authToken
    ) {
        try {
            List<ImmunizationRecord> immunizationRecords = immunizationRecordService.getImmunizationRecordsByUser(getUserIdFromToken(authToken));
            List<ImmunizationRecordDto> immunizationRecordDtos = immunizationRecordMapper.toDtoList(immunizationRecords);
            return ResponseEntity.ok(immunizationRecordDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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

    private UUID getUserIdFromToken(String authToken) {
        String token = authToken.replace("Bearer ", "");
        return UUID.fromString(jwtTokenReader.getUserId(token));
    }
}
