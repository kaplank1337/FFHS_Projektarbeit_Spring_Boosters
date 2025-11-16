package ch.ffhs.spring_boosters.controller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "follow_up_rule")
public class FollowUpRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "from_plan_id")
    private UUID fromPlanId;

    @Column(name = "to_plan_id")
    private UUID toPlanId;

    @Column(name = "required_series_id")
    private UUID requiredSeriesId;

    @NotNull
    @Column(name = "min_completed_doses", nullable = false)
    private Integer minCompletedDoses;

    @Column(name = "target_min_age_days")
    private Integer targetMinAgeDays;

    @Column(name = "target_max_age_days")
    private Integer targetMaxAgeDays;

    @Column(name = "min_interval_days_since_last")
    private Integer minIntervalDaysSinceLast;

    @Column(name = "preferred_age_days")
    private Integer preferredAgeDays;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_plan_id", insertable = false, updatable = false)
    private ImmunizationPlan fromPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_plan_id", insertable = false, updatable = false)
    private ImmunizationPlan toPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "required_series_id", insertable = false, updatable = false)
    private ImmunizationPlanSeries requiredSeries;

    // Constructors
    public FollowUpRule() {}

    public FollowUpRule(UUID fromPlanId, UUID toPlanId, UUID requiredSeriesId, Integer minCompletedDoses) {
        this.fromPlanId = fromPlanId;
        this.toPlanId = toPlanId;
        this.requiredSeriesId = requiredSeriesId;
        this.minCompletedDoses = minCompletedDoses;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFromPlanId() {
        return fromPlanId;
    }

    public void setFromPlanId(UUID fromPlanId) {
        this.fromPlanId = fromPlanId;
    }

    public UUID getToPlanId() {
        return toPlanId;
    }

    public void setToPlanId(UUID toPlanId) {
        this.toPlanId = toPlanId;
    }

    public UUID getRequiredSeriesId() {
        return requiredSeriesId;
    }

    public void setRequiredSeriesId(UUID requiredSeriesId) {
        this.requiredSeriesId = requiredSeriesId;
    }

    public Integer getMinCompletedDoses() {
        return minCompletedDoses;
    }

    public void setMinCompletedDoses(Integer minCompletedDoses) {
        this.minCompletedDoses = minCompletedDoses;
    }

    public Integer getTargetMinAgeDays() {
        return targetMinAgeDays;
    }

    public void setTargetMinAgeDays(Integer targetMinAgeDays) {
        this.targetMinAgeDays = targetMinAgeDays;
    }

    public Integer getTargetMaxAgeDays() {
        return targetMaxAgeDays;
    }

    public void setTargetMaxAgeDays(Integer targetMaxAgeDays) {
        this.targetMaxAgeDays = targetMaxAgeDays;
    }

    public Integer getMinIntervalDaysSinceLast() {
        return minIntervalDaysSinceLast;
    }

    public void setMinIntervalDaysSinceLast(Integer minIntervalDaysSinceLast) {
        this.minIntervalDaysSinceLast = minIntervalDaysSinceLast;
    }

    public Integer getPreferredAgeDays() {
        return preferredAgeDays;
    }

    public void setPreferredAgeDays(Integer preferredAgeDays) {
        this.preferredAgeDays = preferredAgeDays;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ImmunizationPlan getFromPlan() {
        return fromPlan;
    }

    public void setFromPlan(ImmunizationPlan fromPlan) {
        this.fromPlan = fromPlan;
    }

    public ImmunizationPlan getToPlan() {
        return toPlan;
    }

    public void setToPlan(ImmunizationPlan toPlan) {
        this.toPlan = toPlan;
    }

    public ImmunizationPlanSeries getRequiredSeries() {
        return requiredSeries;
    }

    public void setRequiredSeries(ImmunizationPlanSeries requiredSeries) {
        this.requiredSeries = requiredSeries;
    }
}
