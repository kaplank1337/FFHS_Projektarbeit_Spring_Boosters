package ch.ffhs.spring_boosters.controller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "immunization_record", schema = "spring_boosters")
public class ImmunizationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @NotNull
    @Column(name = "vaccine_type_id", nullable = false)
    private UUID vaccineTypeId;

    @NotNull
    @Column(name = "immunization_plan_id", nullable = false)
    private UUID immunizationPlanId;

    @NotNull
    @Column(name = "administered_on", nullable = false)
    private LocalDate administeredOn;

    @Column(name = "dose_order_claimed")
    private Integer doseOrderClaimed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_type_id", insertable = false, updatable = false)
    private VaccineType vaccineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "immunization_plan_id", insertable = false, updatable = false)
    private ImmunizationPlan immunizationPlan;

    // Constructors
    public ImmunizationRecord() {}

    public ImmunizationRecord(UUID userId, UUID vaccineTypeId, UUID immunizationPlanId, LocalDate administeredOn) {
        this.userId = userId;
        this.vaccineTypeId = vaccineTypeId;
        this.immunizationPlanId = immunizationPlanId;
        this.administeredOn = administeredOn;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getVaccineTypeId() {
        return vaccineTypeId;
    }

    public void setVaccineTypeId(UUID vaccineTypeId) {
        this.vaccineTypeId = vaccineTypeId;
    }

    public UUID getImmunizationPlanId() {
        return immunizationPlanId;
    }

    public void setImmunizationPlanId(UUID immunizationPlanId) {
        this.immunizationPlanId = immunizationPlanId;
    }

    public LocalDate getAdministeredOn() {
        return administeredOn;
    }

    public void setAdministeredOn(LocalDate administeredOn) {
        this.administeredOn = administeredOn;
    }

    public Integer getDoseOrderClaimed() {
        return doseOrderClaimed;
    }

    public void setDoseOrderClaimed(Integer doseOrderClaimed) {
        this.doseOrderClaimed = doseOrderClaimed;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public ImmunizationPlan getImmunizationPlan() {
        return immunizationPlan;
    }

    public void setImmunizationPlan(ImmunizationPlan immunizationPlan) {
        this.immunizationPlan = immunizationPlan;
    }
}
