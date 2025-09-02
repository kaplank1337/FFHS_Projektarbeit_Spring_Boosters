package ch.ffhs.spring_boosters.controller.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "active_substance")
public class ActiveSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "synonyms", columnDefinition = "TEXT[]")
    private String[] synonyms;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "activeSubstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VaccineTypeActiveSubstance> vaccineTypeActiveSubstances;

    // Constructors
    public ActiveSubstance() {}

    public ActiveSubstance(String name, String[] synonyms) {
        this.name = name;
        this.synonyms = synonyms;
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

    public String[] getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms;
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
}
