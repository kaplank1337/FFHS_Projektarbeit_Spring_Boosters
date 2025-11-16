package ch.ffhs.spring_boosters.controller.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class VaccineTypeActiveSubstanceId implements Serializable {

    private UUID vaccineTypeId;
    private UUID activeSubstanceId;

    // Constructors
    public VaccineTypeActiveSubstanceId() {}

    public VaccineTypeActiveSubstanceId(UUID vaccineTypeId, UUID activeSubstanceId) {
        this.vaccineTypeId = vaccineTypeId;
        this.activeSubstanceId = activeSubstanceId;
    }

    // Getters and Setters
    public UUID getVaccineTypeId() {
        return vaccineTypeId;
    }

    public void setVaccineTypeId(UUID vaccineTypeId) {
        this.vaccineTypeId = vaccineTypeId;
    }

    public UUID getActiveSubstanceId() {
        return activeSubstanceId;
    }

    public void setActiveSubstanceId(UUID activeSubstanceId) {
        this.activeSubstanceId = activeSubstanceId;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VaccineTypeActiveSubstanceId that = (VaccineTypeActiveSubstanceId) o;
        return Objects.equals(vaccineTypeId, that.vaccineTypeId) &&
               Objects.equals(activeSubstanceId, that.activeSubstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vaccineTypeId, activeSubstanceId);
    }
}
