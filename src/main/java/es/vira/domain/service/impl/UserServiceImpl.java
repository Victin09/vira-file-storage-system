package es.vira.domain.service.impl;

import es.vira.application.dto.UserDto;
import es.vira.application.exception.UserNotFoundException;
import es.vira.application.exception.UsernameAlreadyExistsException;
import es.vira.domain.enums.UserRoleEnum;
import es.vira.domain.mapper.UserMapper;
import es.vira.domain.model.User;
import es.vira.domain.service.UserService;
import es.vira.domain.validator.UserValidator;
import es.vira.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class UserServiceImpl implements UserService {

    public final String USER_NOT_FOUND_BY_ID_TEMPLATE = "User with ID = %d wasn't found";
    public final String USER_NOT_FOUND_BY_USERNAME_TEMPLATE = "User with ID = %s wasn't found";
    private final UserRepository repository;
    private final UserMapper userMapper;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final int defaultPageSize;
    private final int maxPageSize;

    @Autowired
    public UserServiceImpl(UserRepository repository,
                           UserMapper userMapper,
                           UserValidator validator,
                           PasswordEncoder passwordEncoder,
                           @Value("${application.options.usersListDefaultPageSize}") int defaultPageSize,
                           @Value("${application.options.usersListMaxPageSize}") int maxPageSize) {
        this.repository = repository;
        this.userMapper = userMapper;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.defaultPageSize = defaultPageSize;
        this.maxPageSize = maxPageSize;
    }

    @Override
    public UserDto create(UserDto userDto) throws UsernameAlreadyExistsException {
        validator.assertUserDto(userDto, "Can't create a user info when user is null");
        User user = userMapper.convertDtoToUser(userDto);
        user.setRole(UserRoleEnum.USER);
        validator.validate(user);
        validator.validatePassword(userDto.getPassword());
        String username = user.getUsername();
        if (repository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(format("Username %s already exists", username));
        }
        return userMapper.convertUserToDto(repository.save(user));
    }

    @Override
    public UserDto get(long id) throws UserNotFoundException {
        validator.assertId(id, "Can't get a user by ID less than 1. ID = %d");
        User user = repository.findProjectionById(id)
                .orElseThrow(() -> new UserNotFoundException(format(USER_NOT_FOUND_BY_ID_TEMPLATE, id)));
        return userMapper.convertUserToDto(user);
    }

    @Override
    public UserDto get(String username) throws UserNotFoundException {
        validator.assertUsername(username);
        User user = repository.findProjectionByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(format(USER_NOT_FOUND_BY_USERNAME_TEMPLATE, username)));
        return userMapper.convertUserToDto(user);
    }

    @Override
    public Optional<User> getUser(String username) {
        validator.assertUsername(username);
        return repository.findByUsername(username);
    }

    @Override
    public List<UserDto> getList(int page) {
        return getList(page, defaultPageSize);
    }

    @Override
    public List<UserDto> getList(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number can't be less than zero. Page number = " + pageNumber);
        }
        if (pageSize < 0 || pageSize > maxPageSize) {
            throw new IllegalArgumentException(format("Page size can't be less than zero or greater than %d. Page size = %d", maxPageSize, pageSize));
        }
        return repository.findAllProjectionsBy(PageRequest.of(pageNumber, pageSize)).stream().map(userMapper::convertUserToDto).collect(Collectors.toList());
    }

    @Override
    public void updateInfo(UserDto user) throws UserNotFoundException {
        validator.assertUserDto(user, "Can't update a user info when user is null");
        Long id = userMapper.convertDtoToUser(user).getId();
        User persistedUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format(USER_NOT_FOUND_BY_ID_TEMPLATE, id)));

        Optional.ofNullable(user.getFirstName())
                .filter(s -> !s.isBlank())
                .ifPresent(persistedUser::setFirstName);
        Optional.ofNullable(user.getLastName())
                .filter(s -> !s.isBlank())
                .ifPresent(persistedUser::setLastName);
        validator.validate(persistedUser);
        repository.save(persistedUser);
    }

    @Override
    public void updatePassword(Long id, String newPassword) throws UserNotFoundException {
        validator.validatePassword(newPassword);
        User persistedUser = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(format(USER_NOT_FOUND_BY_ID_TEMPLATE, id)));
        persistedUser.setPassword(passwordEncoder.encode(newPassword));
        repository.save(persistedUser);
    }

    @Override
    public void delete(long id) throws UserNotFoundException {
        validator.assertId(id, "Can't delete a user by ID less than 1. ID = %d");
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(format(USER_NOT_FOUND_BY_ID_TEMPLATE, id));
        }
        repository.deleteById(id);
    }

    @Override
    public long getCount() {
        return repository.count();
    }
}
