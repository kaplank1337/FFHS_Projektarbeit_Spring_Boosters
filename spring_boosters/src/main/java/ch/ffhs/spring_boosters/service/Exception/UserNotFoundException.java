package ch.ffhs.spring_boosters.service.Exception;

import java.util.UUID;

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

    public static UserNotFoundException forId(UUID userId){
        return new UserNotFoundException(
                String.format("User with id '%s' not exists", userId));
    }
}
