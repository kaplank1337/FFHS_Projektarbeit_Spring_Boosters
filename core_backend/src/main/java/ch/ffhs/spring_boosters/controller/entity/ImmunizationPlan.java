package ch.ffhs.spring_boosters.controller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "immunization_plan", schema = "spring_boosters")
public class ImmunizationPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name = "vaccine_type_id", nullable = false)
    private UUID vaccineTypeId;

    @NotNull
    @Column(name = "age_category_id", nullable = false)
    private UUID ageCategoryId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_type_id", insertable = false, updatable = false)
    private VaccineType vaccineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "age_category_id", insertable = false, updatable = false)
    private AgeCategory ageCategory;

    @OneToMany(mappedBy = "immunizationPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationPlanSeries> immunizationPlanSeries;

    @OneToMany(mappedBy = "fromPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowUpRule> fromRules;

    @OneToMany(mappedBy = "toPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowUpRule> toRules;

    @OneToMany(mappedBy = "immunizationPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationRecord> immunizationRecords;

    // Constructors
    public ImmunizationPlan() {}

    public ImmunizationPlan(String name, UUID vaccineTypeId, UUID ageCategoryId) {
        this.name = name;
        this.vaccineTypeId = vaccineTypeId;
        this.ageCategoryId = ageCategoryId;
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

    public UUID getVaccineTypeId() {
        return vaccineTypeId;
    }

    public void setVaccineTypeId(UUID vaccineTypeId) {
        this.vaccineTypeId = vaccineTypeId;
    }

    public UUID getAgeCategoryId() {
        return ageCategoryId;
    }

    public void setAgeCategoryId(UUID ageCategoryId) {
        this.ageCategoryId = ageCategoryId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public AgeCategory getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(AgeCategory ageCategory) {
        this.ageCategory = ageCategory;
    }

    public List<ImmunizationPlanSeries> getImmunizationPlanSeries() {
        return immunizationPlanSeries;
    }

    public void setImmunizationPlanSeries(List<ImmunizationPlanSeries> immunizationPlanSeries) {
        this.immunizationPlanSeries = immunizationPlanSeries;
    }

    public List<FollowUpRule> getFromRules() {
        return fromRules;
    }

    public void setFromRules(List<FollowUpRule> fromRules) {
        this.fromRules = fromRules;
    }

    public List<FollowUpRule> getToRules() {
        return toRules;
    }

    public void setToRules(List<FollowUpRule> toRules) {
        this.toRules = toRules;
    }

    public List<ImmunizationRecord> getImmunizationRecords() {
        return immunizationRecords;
    }

    public void setImmunizationRecords(List<ImmunizationRecord> immunizationRecords) {
        this.immunizationRecords = immunizationRecords;
    }
}
