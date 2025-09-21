package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("test.user");
        user.setPasswordHash("plainPass");
        user.setFirstName("Test");
        user.setLastName("User");
    }

    @Test
    void loadUserByUsername_found() {
        when(userRepository.findByUsername("test.user")).thenReturn(Optional.of(user));

        var result = userService.loadUserByUsername("test.user");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test.user", result.getUsername());
        verify(userRepository, times(1)).findByUsername("test.user");
    }

    @Test
    void loadUserByUsername_notFound_throws() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing"));
        verify(userRepository, times(1)).findByUsername("missing");
    }

    @Test
    void registerUser_success() throws UserAlreadyExistException {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPasswordHash())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(user);

        Assertions.assertEquals("encoded", result.getPasswordHash());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(passwordEncoder, times(1)).encode("plainPass");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_alreadyExists_throws() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        Assertions.assertThrows(UserAlreadyExistException.class, () -> userService.registerUser(user));
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findByUsername_found() throws UserNotFoundException {
        when(userRepository.findByUsername("test.user")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("test.user");

        Assertions.assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findByUsername("test.user");
    }

    @Test
    void findByUsername_notFound_throws() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findByUsername("missing"));
        verify(userRepository, times(1)).findByUsername("missing");
    }

    @Test
    void findById_found() throws UserNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.findById(user.getId());

        Assertions.assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findById_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.findById(id));
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void deleteUser_success() throws UserNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_notFound_throws() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void existsByUsername_delegates() {
        when(userRepository.existsByUsername("test.user")).thenReturn(true);

        boolean exists = userService.existsByUsername("test.user");

        Assertions.assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername("test.user");
    }
}

