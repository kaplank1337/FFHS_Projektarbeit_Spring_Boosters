package ch.ffhs.spring_boosters.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/immunization-schedule")
public class ImmunizationScheduleController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getImmunizationSchedule() {
        // Placeholder implementation
        Map<String, String> response = new HashMap<>();
        response.put("message", "Get Immunization Schedule - Not Implemented");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createImmunizationSchedule() {
        // Placeholder implementation
        Map<String, String> response = new HashMap<>();
        response.put("message", "Create Immunization Schedule - Not Implemented");
        return ResponseEntity.ok(response);
    }
}
