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
@Table(name = "immunization_plan_series")
public class ImmunizationPlanSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "immunization_plan_id", nullable = false)
    private UUID immunizationPlanId;

    @NotBlank
    @Column(name = "series_name", nullable = false)
    private String seriesName;

    @NotNull
    @Column(name = "required_doses", nullable = false)
    private Integer requiredDoses;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "immunization_plan_id", insertable = false, updatable = false)
    private ImmunizationPlan immunizationPlan;

    @OneToMany(mappedBy = "requiredSeries", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FollowUpRule> followUpRules;

    // Constructors
    public ImmunizationPlanSeries() {}

    public ImmunizationPlanSeries(UUID immunizationPlanId, String seriesName, Integer requiredDoses) {
        this.immunizationPlanId = immunizationPlanId;
        this.seriesName = seriesName;
        this.requiredDoses = requiredDoses;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getImmunizationPlanId() {
        return immunizationPlanId;
    }

    public void setImmunizationPlanId(UUID immunizationPlanId) {
        this.immunizationPlanId = immunizationPlanId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public Integer getRequiredDoses() {
        return requiredDoses;
    }

    public void setRequiredDoses(Integer requiredDoses) {
        this.requiredDoses = requiredDoses;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ImmunizationPlan getImmunizationPlan() {
        return immunizationPlan;
    }

    public void setImmunizationPlan(ImmunizationPlan immunizationPlan) {
        this.immunizationPlan = immunizationPlan;
    }

    public List<FollowUpRule> getFollowUpRules() {
        return followUpRules;
    }

    public void setFollowUpRules(List<FollowUpRule> followUpRules) {
        this.followUpRules = followUpRules;
    }
}
