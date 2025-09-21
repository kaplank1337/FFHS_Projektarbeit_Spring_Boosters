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
    public ResponseEntity<List<ActiveSubstanceDto>> getAllActiveSubstances() {
        List<ActiveSubstance> activeSubstances = activeSubstanceService.getAllActiveSubstances();
        List<ActiveSubstanceDto> activeSubstanceDtos = activeSubstanceMapper.toDtoList(activeSubstances);
        return ResponseEntity.ok(activeSubstanceDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiveSubstanceDto> getActiveSubstanceById(
        @PathVariable UUID id) throws ActiveSubstanceNotFoundException {
        ActiveSubstance activeSubstance = activeSubstanceService.getActiveSubstanceById(id);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(activeSubstance);
        return ResponseEntity.ok(activeSubstanceDto);
    }

    @PostMapping
    public ResponseEntity<ActiveSubstanceDto> createActiveSubstance(
        @Valid @RequestBody ActiveSubstanceCreateDto createDto) throws ActiveSubstanceAlreadyExistsException {
        ActiveSubstance activeSubstance = activeSubstanceMapper.fromCreateDto(createDto);
        ActiveSubstance createdActiveSubstance = activeSubstanceService.createActiveSubstance(activeSubstance);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(createdActiveSubstance);
        return ResponseEntity.status(HttpStatus.CREATED).body(activeSubstanceDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActiveSubstanceDto> updateActiveSubstance(
        @PathVariable UUID id,
        @Valid @RequestBody ActiveSubstanceUpdateDto updateDto) throws ActiveSubstanceNotFoundException, ActiveSubstanceAlreadyExistsException {
        ActiveSubstance activeSubstance = activeSubstanceMapper.fromUpdateDto(updateDto);
        ActiveSubstance updatedActiveSubstance = activeSubstanceService.updateActiveSubstance(id, activeSubstance);
        ActiveSubstanceDto activeSubstanceDto = activeSubstanceMapper.toDto(updatedActiveSubstance);
        return ResponseEntity.ok(activeSubstanceDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActiveSubstance(
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
