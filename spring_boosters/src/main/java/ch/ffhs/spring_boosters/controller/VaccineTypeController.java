package ch.ffhs.spring_boosters.controller;

import ch.ffhs.spring_boosters.controller.dto.ExceptionMessageBodyDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeDto;
import ch.ffhs.spring_boosters.controller.dto.VaccineTypeListDto;
import ch.ffhs.spring_boosters.controller.entity.VaccineType;
import ch.ffhs.spring_boosters.controller.mapper.VaccineTypeMapper;
import ch.ffhs.spring_boosters.service.Exception.VaccineTypeNotFoundException;
import ch.ffhs.spring_boosters.service.VaccineTypeService;
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
public class VaccineTypeController {

    private final VaccineTypeService vaccineTypeService;
    private final VaccineTypeMapper vaccineTypeMapper;


    @GetMapping
    public ResponseEntity<VaccineTypeListDto> getVaccineTypes() {
        List<VaccineType> vaccineTypes = vaccineTypeService.getVaccineTypes();
        VaccineTypeListDto vaccineTypeListDto = vaccineTypeMapper.vaccineTypeListDto(vaccineTypes);
        return ResponseEntity.status(HttpStatus.OK).body(vaccineTypeListDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VaccineTypeDto> getVaccineTypeById(@PathVariable UUID id) throws VaccineTypeNotFoundException {
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
