package ch.ffhs.spring_boosters.service.Exception;

public class UserAlreadyExistException extends Exception {

    public UserAlreadyExistException() {
        super("User already exists");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

    public UserAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserAlreadyExistException forUsername(String username) {
        return new UserAlreadyExistException(
            String.format("User with username '%s' already exists", username)
        );
    }
}
