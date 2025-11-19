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
    public ResponseEntity<List<AgeCategoryDto>> getAllAgeCategories() {
        List<AgeCategory> ageCategories = ageCategoryService.getAllAgeCategories();
        List<AgeCategoryDto> ageCategoryDtos = ageCategoryMapper.toDtoList(ageCategories);
        return ResponseEntity.ok(ageCategoryDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgeCategoryDto> getAgeCategoryById(
        @PathVariable UUID id) throws AgeCategoryNotFoundException {
        AgeCategory ageCategory = ageCategoryService.getAgeCategoryById(id);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(ageCategory);
        return ResponseEntity.ok(ageCategoryDto);
    }

    @PostMapping
    public ResponseEntity<AgeCategoryDto> createAgeCategory(
        @Valid @RequestBody AgeCategoryCreateDto createDto) throws AgeCategoryAlreadyExistsException {
        AgeCategory ageCategory = ageCategoryMapper.fromCreateDto(createDto);
        AgeCategory createdAgeCategory = ageCategoryService.createAgeCategory(ageCategory);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(createdAgeCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(ageCategoryDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgeCategoryDto> updateAgeCategory(
        @PathVariable UUID id,
        @Valid @RequestBody AgeCategoryUpdateDto updateDto) throws AgeCategoryNotFoundException, AgeCategoryAlreadyExistsException {
        AgeCategory ageCategory = ageCategoryMapper.fromUpdateDto(updateDto);
        AgeCategory updatedAgeCategory = ageCategoryService.updateAgeCategory(id, ageCategory);
        AgeCategoryDto ageCategoryDto = ageCategoryMapper.toDto(updatedAgeCategory);
        return ResponseEntity.ok(ageCategoryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgeCategory(
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
