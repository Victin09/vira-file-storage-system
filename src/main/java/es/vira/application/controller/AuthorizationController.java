package es.vira.application.controller;

import es.vira.application.dto.AuthDto;
import es.vira.application.dto.UserDto;
import es.vira.application.exception.UsernameAlreadyExistsException;
import es.vira.domain.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Authorization")
@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final UserService userService;

    @Autowired
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Login endpoint.",
            description = "Returns Authorization header with a JWT in case of successful authorization.")
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public void login(@Parameter(description = "Username and password to authorize.", required = true)
                         @RequestBody AuthDto authenticationRequest) { }

    @Operation(summary = "register endpoint.",
            description = "Returns new User data in case of successful authorization.")
    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public UserDto register(@Parameter(description = "User data.", required = true)
                      @RequestBody UserDto userData) throws UsernameAlreadyExistsException {
        return userService.create(userData);
    }
}
