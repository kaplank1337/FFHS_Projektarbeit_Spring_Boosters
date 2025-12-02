package ch.ffhs.spring_boosters.controller.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "spring_boosters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"passwordHash", "immunizationRecords"})
@EqualsAndHashCode(exclude = {"immunizationRecords", "createdAt", "updatedAt"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Size(max = 255)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Builder.Default
    @Column(name = "role")
    private String role = "USER";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImmunizationRecord> immunizationRecords;

    @Transient
    @JsonProperty("ageYears")
    public Integer getAgeYears() {
        LocalDate bd = this.getBirthDate(); // oder directly: this.birthDate
        if (bd == null) {
            return null;
        }
        return Period.between(bd, LocalDate.now()).getYears();
    }

    public String getPassword() {
        return passwordHash;
    }

}
