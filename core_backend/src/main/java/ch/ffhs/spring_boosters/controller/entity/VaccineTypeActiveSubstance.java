package ch.ffhs.spring_boosters.controller.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "vaccine_type_active_substance", schema = "spring_boosters")
@IdClass(VaccineTypeActiveSubstanceId.class)
public class VaccineTypeActiveSubstance {

    @Id
    @Column(name = "vaccine_type_id")
    private UUID vaccineTypeId;

    @Id
    @Column(name = "active_substance_id")
    private UUID activeSubstanceId;

    @Column(name = "qualitative_amount")
    private String qualitativeAmount;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_type_id", insertable = false, updatable = false)
    @JsonBackReference
    private VaccineType vaccineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_substance_id", insertable = false, updatable = false)
    @JsonBackReference
    private ActiveSubstance activeSubstance;

    // Constructors
    public VaccineTypeActiveSubstance() {}

    public VaccineTypeActiveSubstance(UUID vaccineTypeId, UUID activeSubstanceId, String qualitativeAmount) {
        this.vaccineTypeId = vaccineTypeId;
        this.activeSubstanceId = activeSubstanceId;
        this.qualitativeAmount = qualitativeAmount;
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

    public String getQualitativeAmount() {
        return qualitativeAmount;
    }

    public void setQualitativeAmount(String qualitativeAmount) {
        this.qualitativeAmount = qualitativeAmount;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public ActiveSubstance getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(ActiveSubstance activeSubstance) {
        this.activeSubstance = activeSubstance;
    }
}
