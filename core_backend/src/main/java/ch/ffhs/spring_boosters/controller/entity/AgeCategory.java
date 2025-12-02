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
@Table(name = "age_category", schema = "spring_boosters")
public class AgeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(name = "age_min_days", nullable = false)
    private Integer ageMinDays;

    @Column(name = "age_max_days")
    private Integer ageMaxDays; // NULL means open-ended

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "ageCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationPlan> immunizationPlans;

    // Constructors
    public AgeCategory() {}

    public AgeCategory(String name, Integer ageMinDays, Integer ageMaxDays) {
        this.name = name;
        this.ageMinDays = ageMinDays;
        this.ageMaxDays = ageMaxDays;
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

    public Integer getAgeMinDays() {
        return ageMinDays;
    }

    public void setAgeMinDays(Integer ageMinDays) {
        this.ageMinDays = ageMinDays;
    }

    public Integer getAgeMaxDays() {
        return ageMaxDays;
    }

    public void setAgeMaxDays(Integer ageMaxDays) {
        this.ageMaxDays = ageMaxDays;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<ImmunizationPlan> getImmunizationPlans() {
        return immunizationPlans;
    }

    public void setImmunizationPlans(List<ImmunizationPlan> immunizationPlans) {
        this.immunizationPlans = immunizationPlans;
    }
}
