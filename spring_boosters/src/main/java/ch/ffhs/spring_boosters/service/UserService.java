package ch.ffhs.spring_boosters.service;

import ch.ffhs.spring_boosters.controller.entity.User;
import ch.ffhs.spring_boosters.service.Exception.UserAlreadyExistException;
import ch.ffhs.spring_boosters.service.Exception.UserNotFoundException;

public interface UserService {

    User registerUser(User user) throws UserAlreadyExistException;

    User findByUsername(String username) throws UserNotFoundException;

    void deleteUser(String username) throws UserNotFoundException;

    boolean existsByUsername(String username);
}
