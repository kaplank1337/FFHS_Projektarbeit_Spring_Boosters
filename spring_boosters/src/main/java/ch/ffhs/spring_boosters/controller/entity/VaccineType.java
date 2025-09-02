package ch.ffhs.spring_boosters.controller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vaccine_type")
public class VaccineType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column
    private String code;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "vaccineType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VaccineTypeActiveSubstance> vaccineTypeActiveSubstances;

    @OneToMany(mappedBy = "vaccineType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationPlan> immunizationPlans;

    @OneToMany(mappedBy = "vaccineType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationRecord> immunizationRecords;

    // Constructors
    public VaccineType() {}

    public VaccineType(String name, String code) {
        this.name = name;
        this.code = code;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<VaccineTypeActiveSubstance> getVaccineTypeActiveSubstances() {
        return vaccineTypeActiveSubstances;
    }

    public void setVaccineTypeActiveSubstances(List<VaccineTypeActiveSubstance> vaccineTypeActiveSubstances) {
        this.vaccineTypeActiveSubstances = vaccineTypeActiveSubstances;
    }

    public List<ImmunizationPlan> getImmunizationPlans() {
        return immunizationPlans;
    }

    public void setImmunizationPlans(List<ImmunizationPlan> immunizationPlans) {
        this.immunizationPlans = immunizationPlans;
    }

    public List<ImmunizationRecord> getImmunizationRecords() {
        return immunizationRecords;
    }

    public void setImmunizationRecords(List<ImmunizationRecord> immunizationRecords) {
        this.immunizationRecords = immunizationRecords;
    }
}
