package es.vira.domain.validator.impl;

import es.vira.application.dto.UserDto;
import es.vira.application.exception.UserNotFoundException;
import es.vira.domain.enums.UserRoleEnum;
import es.vira.domain.mapper.UserMapper;
import es.vira.domain.model.User;
import es.vira.domain.service.UserService;
import es.vira.domain.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Component
public class UserValidatorImpl implements UserValidator {

    public static final String USERNAME_AND_PASSWORD_REGEX = "[a-zA-Z0-9]+";
    public final int minPasswordLength;
    public final int maxPasswordLength;
    private final Validator validator;
    private final UserMapper userMapper;

    @Autowired
    public UserValidatorImpl(@Value("${application.options.minPasswordLength}") int minPasswordLength,
                             @Value("${application.options.maxPasswordLength}") int maxPasswordLength,
                             Validator validator, UserMapper userMapper) {
        this.minPasswordLength = minPasswordLength;
        this.maxPasswordLength = maxPasswordLength;
        this.validator = validator;
        this.userMapper = userMapper;
    }

    @Override
    public void assertAccessToModify(Long id, Authentication authentication, UserService userService) throws UserNotFoundException {
        User user = userMapper.convertDtoToUser(userService.get((String) authentication.getPrincipal()));
        if (user.getRole() == UserRoleEnum.ADMIN) {
            return;
        }
        Long currentUserId = user.getId();
        if (!currentUserId.equals(id)) {
            throw new AccessDeniedException(
                    format("User with ID = %s and username = %s doesn't have permission to modify other users information.",
                            currentUserId, user.getUsername())
            );
        }
    }

    @Override
    public void assertAccessToGetUser(Long id, String requestedUsername, Authentication authentication, UserService userService) throws UserNotFoundException {
        User user = userMapper.convertDtoToUser(userService.get((String) authentication.getPrincipal()));
        if (user.getRole() == UserRoleEnum.ADMIN) {
            return;
        }
        Long currentUserId = user.getId();
        String username = user.getUsername();
        if ((id != null && !currentUserId.equals(id)) ||
                (requestedUsername != null && !username.equals(requestedUsername))) {
            throw new AccessDeniedException(
                    format("User with ID = %s and username = %s doesn't have permission to view other users information.",
                            currentUserId, username)
            );
        }
    }

    @Override
    public void assertId(long id, String msg) {
        if (id < 1) {
            throw new IllegalArgumentException(format(msg, id));
        }
    }

    @Override
    public void assertUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Can't get a user by null or blank username. Username = " + username);
        }
    }

    @Override
    public void assertUserDto(UserDto user, String msg) {
        if (user == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void validate(User user) {
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Optional<ConstraintViolation<User>> constraintViolationOpt = constraintViolations.stream().findFirst();
        if (constraintViolationOpt.isPresent()) {
            ConstraintViolation<User> constraintViolation = constraintViolationOpt.get();
            String msg = String.format("%s field %s", constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password field can't be null or blank");
        }

        int passwordLength = password.length();
        if (passwordLength < minPasswordLength || passwordLength > maxPasswordLength) {
            throw new IllegalArgumentException(String.format("password field must be greater than %d and less than %d",
                    minPasswordLength, maxPasswordLength));
        }

        if (!password.matches(USERNAME_AND_PASSWORD_REGEX)) {
            throw new IllegalArgumentException(String.format("password field must match %s", USERNAME_AND_PASSWORD_REGEX));
        }
    }
}
