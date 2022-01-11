package es.vira.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "password")
@Schema(name = "Auth Data Transfer Object")
public class AuthDto {
    private String username;
    private String password;
}
