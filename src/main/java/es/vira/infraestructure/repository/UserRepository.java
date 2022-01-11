package es.vira.infraestructure.repository;

import es.vira.application.dto.UserDto;
import es.vira.domain.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllProjectionsBy(Pageable pageable);

    Optional<User> findProjectionById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findProjectionByUsername(String username);

    boolean existsByUsername(String username);
}
