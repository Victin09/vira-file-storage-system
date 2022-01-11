package es.vira.infraestructure.security;

import es.vira.domain.model.User;
import es.vira.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return getUserDetails(userService.getUser(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s wasn't found", username))));
    }

    private UserDetailsImpl getUserDetails(User user) {
        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isEnabled(true)
                .isCredentialsNonExpired(true)
                .grantedAuthorities(user.getRole().getGrantedAuthorities())
                .build();
    }
}
