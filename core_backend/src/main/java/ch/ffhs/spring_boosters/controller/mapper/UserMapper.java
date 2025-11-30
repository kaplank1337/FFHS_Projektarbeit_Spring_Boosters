package ch.ffhs.spring_boosters.controller.mapper;

import ch.ffhs.spring_boosters.controller.dto.UserDto;
import ch.ffhs.spring_boosters.controller.dto.UserRegistrationDto;
import ch.ffhs.spring_boosters.controller.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User userDtoToUser(UserRegistrationDto dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .username(dto.username())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .birthDate(dto.birthDate())
                .passwordHash(dto.password())
                .email(dto.email())
                .build();
    }

    public UserDto userToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getRole()
        );
    }
}
