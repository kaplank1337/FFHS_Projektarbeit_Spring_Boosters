package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;

import java.util.UUID;

public interface UserService {

    User registerUser(User user) throws UserAlreadyExistException;

    User findByUsernameAndPassword(String username, String password) throws UserNotFoundException;

    User findByUsername(String username) throws UserNotFoundException;

    User findById(UUID userId) throws UserNotFoundException;

    void deleteUser(UUID userId) throws UserNotFoundException;

    boolean existsByUsername(String username);

    String generateToken(User user);
}
