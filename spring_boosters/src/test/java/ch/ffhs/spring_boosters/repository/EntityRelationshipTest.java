package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.TestcontainersConfiguration;
import ch.ffhs.spring_boosters.controller.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class EntityRelationshipTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateCompleteImmunizationScenario() {
        // Given: Create a user
        User user = new User(
                "patient1",
                "hashedpassword",
                "Jane",
                "Patient",
                LocalDate.of(1995, 8, 10)
        );
        user = userRepository.save(user);

        // Verify user was created with proper UUID and timestamps
        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(user.getUsername()).isEqualTo("patient1");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Patient");
        assertThat(user.getBirthDate()).isEqualTo(LocalDate.of(1995, 8, 10));
        assertThat(user.getRole()).isEqualTo("USER");

        // Additional validation for UserDetails interface
        assertThat(user.getPassword()).isEqualTo("hashedpassword");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldValidateUserConstraints() {
        // Test username uniqueness
        User user1 = new User("uniqueuser", "pass1", "First", "User", LocalDate.of(1990, 1, 1));
        userRepository.save(user1);

        User user2 = new User("uniqueuser", "pass2", "Second", "User", LocalDate.of(1991, 1, 1));

        // Should throw exception due to unique constraint on username
        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> userRepository.saveAndFlush(user2)
        );
    }

    @Test
    void shouldHandleNullableFieldsCorrectly() {
        // Test that nullable fields (firstName, lastName) can be null
        User userWithNulls = new User(
                "nulluser",
                "password",
                null, // firstName can be null
                null, // lastName can be null
                LocalDate.of(2000, 6, 15)
        );

        User saved = userRepository.save(userWithNulls);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isNull();
        assertThat(saved.getLastName()).isNull();
        assertThat(saved.getBirthDate()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("nulluser");
    }

    @Test
    void shouldUpdateTimestampsCorrectly() throws InterruptedException {
        // Create user
        User user = new User("timeuser", "pass", "Time", "User", LocalDate.of(1988, 12, 25));
        user = userRepository.save(user);

        var originalCreatedAt = user.getCreatedAt();
        var originalUpdatedAt = user.getUpdatedAt();

        // Wait a bit to ensure timestamp difference
        Thread.sleep(10);

        // Update user
        user.setFirstName("Updated Time");
        user = userRepository.save(user);

        // Verify timestamps
        assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt); // Should not change
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt); // Should be updated
    }

    @Test
    void shouldFindUsersByUsername() {
        // Create test users
        User user1 = userRepository.save(new User("finduser1", "pass", "Find", "User1", LocalDate.of(1985, 3, 15)));
        User user2 = userRepository.save(new User("finduser2", "pass", "Find", "User2", LocalDate.of(1987, 7, 20)));

        // Test finding existing user
        var found = userRepository.findByUsername("finduser1");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Find");
        assertThat(found.get().getLastName()).isEqualTo("User1");

        // Test finding non-existing user
        var notFound = userRepository.findByUsername("nonexistent");
        assertThat(notFound).isEmpty();

        // Test existence check
        assertThat(userRepository.existsByUsername("finduser1")).isTrue();
        assertThat(userRepository.existsByUsername("finduser2")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }
}
