package ch.ffhs.spring_boosters.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/immunization-schedule")
public class ImmunizationScheduleController {

    @GetMapping()
    public ResponseEntity<?> getImmunizationSchedule(){
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Immunization schedule endpoint - to be implemented");
        response.put("status", "placeholder");
        return ResponseEntity.ok(response);
    }
}
