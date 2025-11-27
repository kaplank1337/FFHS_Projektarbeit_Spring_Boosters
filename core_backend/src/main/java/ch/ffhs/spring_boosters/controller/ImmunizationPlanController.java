package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanCreateDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanDto;
import ch.ffhs.spring_boosters.controller.dto.ImmunizationPlanUpdateDto;
import ch.ffhs.spring_boosters.controller.entity.ImmunizationPlan;
import ch.ffhs.spring_boosters.controller.mapper.ImmunizationPlanMapper;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanAlreadyExistsException;
import ch.ffhs.spring_boosters.service.Exception.ImmunizationPlanNotFoundException;
import ch.ffhs.spring_boosters.service.ImmunizationPlanService;
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
    public ResponseEntity<List<ImmunizationPlanDto>> getAllImmunizationPlans() {
        List<ImmunizationPlan> immunizationPlans = immunizationPlanService.getAllImmunizationPlans();
        List<ImmunizationPlanDto> immunizationPlanDtos = immunizationPlanMapper.toDtoList(immunizationPlans);
        return ResponseEntity.ok(immunizationPlanDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImmunizationPlanDto> getImmunizationPlanById(
        @PathVariable UUID id) throws ImmunizationPlanNotFoundException {
        ImmunizationPlan immunizationPlan = immunizationPlanService.getImmunizationPlanById(id);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(immunizationPlan);
        return ResponseEntity.ok(immunizationPlanDto);
    }

    @PostMapping
    public ResponseEntity<ImmunizationPlanDto> createImmunizationPlan(
        @Valid @RequestBody ImmunizationPlanCreateDto createDto) throws ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan immunizationPlan = immunizationPlanMapper.fromCreateDto(createDto);
        ImmunizationPlan createdImmunizationPlan = immunizationPlanService.createImmunizationPlan(immunizationPlan);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(createdImmunizationPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(immunizationPlanDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ImmunizationPlanDto> updateImmunizationPlan(
        @PathVariable UUID id,
        @Valid @RequestBody ImmunizationPlanUpdateDto updateDto) throws ImmunizationPlanNotFoundException, ImmunizationPlanAlreadyExistsException {
        ImmunizationPlan immunizationPlan = immunizationPlanMapper.fromUpdateDto(updateDto);
        ImmunizationPlan updatedImmunizationPlan = immunizationPlanService.updateImmunizationPlan(id, immunizationPlan);
        ImmunizationPlanDto immunizationPlanDto = immunizationPlanMapper.toDto(updatedImmunizationPlan);
        return ResponseEntity.ok(immunizationPlanDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImmunizationPlan(
        @PathVariable UUID id) throws ImmunizationPlanNotFoundException {
        immunizationPlanService.deleteImmunizationPlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-vaccine-type/{vaccineTypeId}")
    public ResponseEntity<List<ImmunizationPlanDto>> getImmunizationPlansByVaccineType(
        @PathVariable UUID vaccineTypeId) {
        List<ImmunizationPlan> immunizationPlans = immunizationPlanService.getImmunizationPlansByVaccineType(vaccineTypeId);
        List<ImmunizationPlanDto> immunizationPlanDtos = immunizationPlanMapper.toDtoList(immunizationPlans);
        return ResponseEntity.ok(immunizationPlanDtos);
    }

    @GetMapping("/by-age-category/{ageCategoryId}")
    public ResponseEntity<List<ImmunizationPlanDto>> getImmunizationPlansByAgeCategory(
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
