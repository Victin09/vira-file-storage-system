package es.vira.domain.service;

import es.vira.application.dto.UserDto;
import es.vira.application.exception.UserNotFoundException;
import es.vira.application.exception.UsernameAlreadyExistsException;
import es.vira.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto create(UserDto user) throws UsernameAlreadyExistsException;

    UserDto get(long id) throws UserNotFoundException;

    UserDto get(String username) throws UserNotFoundException;

    Optional<User> getUser(String username);

    List<UserDto> getList(int page);

    List<UserDto> getList(int page, int pageSize);

    void updateInfo(UserDto user) throws UserNotFoundException;

    void updatePassword(Long id, String newPassword) throws UserNotFoundException;

    void delete(long id) throws UserNotFoundException;

    long getCount();
}
