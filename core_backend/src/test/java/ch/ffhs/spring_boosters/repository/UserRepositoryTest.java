package ch.ffhs.spring_boosters.repository;

import ch.ffhs.spring_boosters.controller.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void saveFindByUsernameAndExists() {
        User u = User.builder()
                .username("jdoe")
                .passwordHash("pw")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990,1,1))
                .build();

        User saved = repository.save(u);
        assertNotNull(saved.getId());

        Optional<User> found = repository.findByUsername("jdoe");
        assertTrue(found.isPresent());
        assertEquals("jdoe", found.get().getUsername());

        assertTrue(repository.existsByUsername("jdoe"));
    }
}
