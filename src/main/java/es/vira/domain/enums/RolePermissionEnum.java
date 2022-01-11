package es.vira.domain.enums;

public enum RolePermissionEnum {

    READ_OWN_DATA("user:read"),
    WRITE_OWN_DATA("user:write"),
    READ_USERS_DATA("users:read"),
    WRITE_USERS_DATA("users:write");

    private final String permission;

    RolePermissionEnum(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
