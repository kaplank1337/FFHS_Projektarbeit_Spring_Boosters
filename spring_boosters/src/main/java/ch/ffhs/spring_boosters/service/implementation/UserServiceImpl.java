package ch.ffhs.spring_boosters.service.implementation;

import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.repository.UserRepository;
import ch.ffhs.spring_boosters.service.UserService;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User registerUser(User user) throws UserAlreadyExistException {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw UserAlreadyExistException.forUsername(user.getUsername());
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.forUsername(username));
    }

    @Override
    public void deleteUser(String username) throws UserNotFoundException {
        User user = findByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
