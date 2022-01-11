package es.vira.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum UserRoleEnum {
    ADMIN(Set.of(RolePermissionEnum.READ_OWN_DATA, RolePermissionEnum.WRITE_OWN_DATA, RolePermissionEnum.READ_USERS_DATA, RolePermissionEnum.WRITE_USERS_DATA)),
    USER(Set.of(RolePermissionEnum.READ_OWN_DATA, RolePermissionEnum.WRITE_OWN_DATA));

    private final Set<RolePermissionEnum> permissions;

    UserRoleEnum(Set<RolePermissionEnum> permissions) {
        this.permissions = permissions;
    }

    public Set<RolePermissionEnum> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(RolePermissionEnum::getPermission)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name().toUpperCase()));
        return permissions;
    }

    @JsonCreator
    public static UserRoleEnum forValue(String value) {
        return valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
