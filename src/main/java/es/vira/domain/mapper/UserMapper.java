package es.vira.domain.mapper;

import es.vira.application.dto.UserDto;
import es.vira.domain.model.User;

public interface UserMapper {
    User convertDtoToUser(UserDto userDto);

    UserDto convertUserToDto(User user);
}
