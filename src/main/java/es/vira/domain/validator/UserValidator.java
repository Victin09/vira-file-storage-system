package es.vira.domain.validator;

import es.vira.application.dto.UserDto;
import es.vira.application.exception.UserNotFoundException;
import es.vira.domain.model.User;
import es.vira.domain.service.UserService;
import org.springframework.security.core.Authentication;

public interface UserValidator {
    void assertAccessToModify(Long id, Authentication authentication, UserService userService) throws UserNotFoundException;
    void assertAccessToGetUser(Long id, String requestedUsername, Authentication authentication, UserService userService) throws UserNotFoundException;
    void assertId(long id, String msg);
    void assertUsername(String username);

    void assertUserDto(UserDto user, String msg);
    void validate(User user);
    void validatePassword(String password);
}
