package ch.ffhs.spring_boosters.service.Exception;

import java.util.UUID;

public class VaccineTypeNotFoundException extends Exception {

    public VaccineTypeNotFoundException(String message) {
        super(message);
    }

    public static VaccineTypeNotFoundException forId(UUID id) {
        return new VaccineTypeNotFoundException("Vaccine type with ID '" + id + "' not found");
    }
}
