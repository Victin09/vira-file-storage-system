package es.vira.infraestructure.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException();
    }

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String AUTHORITIES = "authorities";
    public static final String AUTHORITY = "authority";
}
