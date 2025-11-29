package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.security.JwtService;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void register_whenExists_throws() {
        User u = new User();
        u.setUsername("john");
        u.setPasswordHash("pwd");

        when(repository.existsByUsername("john")).thenReturn(true);
        assertThrows(UserAlreadyExistException.class, () -> service.registerUser(u));
    }

    @Test
    void register_hashesPassword_andSaves() throws Exception {
        User u = new User();
        u.setUsername("mike");
        u.setPasswordHash("secret");

        when(repository.existsByUsername("mike")).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        var saved = service.registerUser(u);
        assertNotNull(saved.getPasswordHash());
        assertNotEquals("secret", saved.getPasswordHash());
        assertTrue(BCrypt.checkpw("secret", saved.getPasswordHash()));
    }

    @Test
    void findByUsernameAndPassword_success() throws Exception {
        User u = new User();
        u.setUsername("tom");
        String hashed = BCrypt.hashpw("pw", BCrypt.gensalt(10));
        u.setPasswordHash(hashed);

        when(repository.findByUsername("tom")).thenReturn(Optional.of(u));

        var res = service.findByUsernameAndPassword("tom", "pw");
        assertEquals("tom", res.getUsername());
    }

    @Test
    void findByUsernameAndPassword_badPassword_throws() {
        User u = new User();
        u.setUsername("tom");
        u.setPasswordHash(BCrypt.hashpw("pw", BCrypt.gensalt(10)));

        when(repository.findByUsername("tom")).thenReturn(Optional.of(u));
        assertThrows(UserNotFoundException.class, () -> service.findByUsernameAndPassword("tom", "wrong"));
    }

    @Test
    void findByUsername_notFound_throws() {
        when(repository.findByUsername("nope")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.findByUsername("nope"));
    }

    @Test
    void findById_notFound_throws() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.findById(UUID.randomUUID()));
    }

    @Test
    void deleteUser_deletes() throws Exception {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername("del");

        when(repository.findById(u.getId())).thenReturn(Optional.of(u));
        service.deleteUser(u.getId());
        verify(repository).delete(u);
    }

    @Test
    void generateToken_usesJwtService() {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setUsername("g1");
        when(jwtService.generateToken("g1", u.getId())).thenReturn("tok");

        var token = service.generateToken(u);
        assertEquals("tok", token);
    }
}

