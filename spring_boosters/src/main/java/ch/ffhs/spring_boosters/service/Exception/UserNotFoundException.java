package ch.ffhs.spring_boosters.service.Exception;

public class UserNotFoundException extends Exception{

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static UserNotFoundException forUsername(String username) {
        return new UserNotFoundException(
                String.format("User with username '%s' not exists", username)
        );
    }
}
