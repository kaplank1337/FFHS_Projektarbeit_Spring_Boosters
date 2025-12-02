package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.UserDto;
import ch.ffhs.spring_boosters.controller.dto.UserRegistrationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    void userDtoToUser_nullInput_returnsNull() {
        User res = mapper.userDtoToUser(null);
        assertNull(res);
    }

    @Test
    void userDtoToUser_mapsFields_correctly() {
        UserRegistrationDto dto = new UserRegistrationDto("alice", "Alice", "Doe", LocalDate.of(1990,1,1), "alice@example.com", "secret");
        User entity = mapper.userDtoToUser(dto);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("alice", entity.getUsername());
        assertEquals("Alice", entity.getFirstName());
        assertEquals("Doe", entity.getLastName());
        assertEquals(LocalDate.of(1990,1,1), entity.getBirthDate());
        assertEquals("secret", entity.getPassword());
        assertEquals("alice@example.com", entity.getEmail());
    }

    @Test
    void userToDto_nullInput_returnsNull() {
        UserDto res = mapper.userToDto(null);
        assertNull(res);
    }

    @Test
    void userToDto_mapsFields_correctly() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .username("bob")
                .passwordHash("pw")
                .firstName("Bob")
                .lastName("Builder")
                .birthDate(LocalDate.of(1980,12,12))
                .role("USER")
                .email("bob@example.com")
                .build();

        UserDto dto = mapper.userToDto(user);
        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("bob", dto.username());
        assertEquals("Bob", dto.firstName());
        assertEquals("Builder", dto.lastName());
        assertEquals(LocalDate.of(1980,12,12), dto.birthDate());
        assertEquals("USER", dto.role());
        assertEquals("bob@example.com", dto.email());
    }
}
