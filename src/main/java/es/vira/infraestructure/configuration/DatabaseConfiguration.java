package es.vira.infraestructure.configuration;

import es.vira.domain.enums.UserRoleEnum;
import es.vira.domain.model.User;
import es.vira.infraestructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfiguration {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseConfiguration(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    private void initializeDatabase() {
        final String USERNAME = "admin";
        if (!userRepository.existsByUsername(USERNAME)) {
            User user = new User(USERNAME, passwordEncoder.encode(USERNAME), USERNAME, USERNAME, UserRoleEnum.ADMIN, 1);
            userRepository.save(user);
        }
    }
}
