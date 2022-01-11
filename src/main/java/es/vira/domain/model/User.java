package es.vira.domain.model;

import es.vira.domain.enums.UserRoleEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(name = "User Model",
        description = "Information about a user")
@Entity(name = "vira_user")
@Builder
@Getter @Setter
@ToString(exclude = {"password"})
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {

    @NotBlank
    @Column(name = "username", unique = true)
    @Size(min = 3, max = 30)
    @Pattern(regexp = "[a-zA-Z0-9]+")
    private String username;

    @NotBlank
    @Column(name = "password")
    @Size(min = 3, max = 68)
    private String password;

    @NotBlank
    @Column(name = "first_name")
    @Size(min = 3, max = 30)
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    @Size(min = 3, max = 30)
    private String lastName;

    @NotNull
    @Column(name = "role")
    private UserRoleEnum role;

    @Version
    private Integer version;
}
