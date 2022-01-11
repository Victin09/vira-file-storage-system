package es.vira.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.vira.domain.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "password")
@Schema(name = "User Data Transfer Object")
public class UserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
