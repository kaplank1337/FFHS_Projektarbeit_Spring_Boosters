package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.TestcontainersConfiguration;
import ch.ffhs.spring_boosters.controller.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up and create test data
        userRepository.deleteAll();

        testUser = User.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 5, 15))
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given user is saved in setUp()

        // When retrieving by username
        var foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
        assertThat(foundUser.get().getLastName()).isEqualTo("Doe");
        assertThat(foundUser.get().getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(foundUser.get().getId()).isNotNull();
        assertThat(foundUser.get().getCreatedAt()).isNotNull();
        assertThat(foundUser.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldGenerateUuidPrimaryKey() {
        // Given user is saved in setUp()

        // When checking the ID
        UUID userId = testUser.getId();

        // Then
        assertThat(userId).isNotNull();
        assertThat(userId.toString()).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    void shouldEnforceUniqueUsername() {
        // Given first user exists

        // When trying to save another user with same username
        User duplicateUser = User.builder()
                .username("testuser") // Same username
                .passwordHash("anotherpassword")
                .firstName("Jane")
                .lastName("Smith")
                .birthDate(LocalDate.of(1985, 3, 20))
                .build();

        // Then should throw exception due to unique constraint
        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> userRepository.saveAndFlush(duplicateUser)
        );
    }

    @Test
    void shouldCheckUserExists() {
        // Given user exists

        // When checking existence
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldHandleNullableFields() {
        // Given user with minimal required fields
        User minimalUser = User.builder()
                .username("minimal")
                .passwordHash("password")
                .firstName(null) // firstName is nullable
                .lastName(null) // lastName is nullable
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        // When saving
        User saved = userRepository.save(minimalUser);

        // Then should save successfully
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isNull();
        assertThat(saved.getLastName()).isNull();
        assertThat(saved.getBirthDate()).isNotNull();
    }
}
