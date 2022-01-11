package es.vira.application.controller.v1;

import es.vira.application.dto.UserDto;
import es.vira.domain.service.UserService;
import es.vira.application.exception.UserNotFoundException;
import es.vira.application.exception.UsernameAlreadyExistsException;
import es.vira.domain.validator.UserValidator;
import es.vira.infraestructure.constants.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "User Management")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserValidator validator;

    @Autowired
    public UserController(UserService userService, UserValidator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @Operation(summary = "Creates a new user.",
            description = "Creates a new user by provided information. Returns the ID of created user. ")
    @RolesAllowed(SecurityConstants.USER_ROLE)
    @PostMapping(value = "/create", consumes = APPLICATION_JSON_VALUE)
    public UserDto createUser(@Parameter(description = "The information about a new user.", required = true)
                           @RequestBody UserDto user) throws UsernameAlreadyExistsException {
        return userService.create(user);
    }

    @Operation(summary = "Returns list of users.",
            description = "Returns a list that contains information about existing users. " +
                    "Supports pagination. Uses pageNumber and pageSize to get the list. " +
                    "If pageSize isn't provided, will be used a default value.")
    @RolesAllowed(SecurityConstants.ADMIN_ROLE)
    @GetMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
    public List<UserDto> getUsersList(@Parameter(description = "The page number.", required = true)
                                                 @RequestParam Integer pageNumber,
                                                 @Parameter(description = "The page size.")
                                                 @RequestParam(required = false) Integer pageSize) {
        if (pageSize == null) {
            return userService.getList(pageNumber);
        }
        return userService.getList(pageNumber, pageSize);
    }

    @Operation(summary = "Returns user information by ID or by username.",
            description = "Returns user information by ID or by username. " +
                    "Both ID and username parameters are not required, but at least one of them must be. " +
                    "If both ID and username were specified, ID will be used to get a user.")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public UserDto getUser(@Parameter(description = "The ID of the user.")
                                      @RequestParam(required = false) Long id,
                                      @Parameter(description = "The username of the user.")
                                      @RequestParam(required = false) String username,
                                      Authentication authentication) throws UserNotFoundException {
        if (id == null && username == null) {
            throw new IllegalArgumentException("Can't get a user when both id and username null. Specify id or username parameter.");
        }
        validator.assertAccessToGetUser(id, username, authentication, userService);
        return id == null ? userService.get(username) : userService.get(id);
    }

    @Operation(summary = "Updates user information",
            description = "Updates user information by user ID. Fields that can be updated: firstName, lastName.")
    @PutMapping(value = "/updateInfo", consumes = APPLICATION_JSON_VALUE)
    public void updateUserInfo(@Parameter(description = "The ID of the user to update the password.", required = true)
                                @RequestParam Long id,
                                @Parameter(description = "The user information to update.", required = true)
                                @RequestBody UserDto user,
                                Authentication authentication) throws UserNotFoundException {
        validator.assertAccessToModify(id, authentication, userService);
        userService.updateInfo(user);
    }

    @Operation(summary = "Updates a user password.",
            description = "Updates a user password by user ID.")
    @PutMapping(value = "/updatePassword", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void updateUserPassword(@Parameter(description = "The ID of the user to update the password.", required = true)
                                   @RequestParam Long id,
                                   @Parameter(description = "A new password", required = true)
                                   @RequestBody String newPassword,
                                   Authentication authentication) throws UserNotFoundException {
        validator.assertAccessToModify(id, authentication, userService);
        userService.updatePassword(id, newPassword);
    }

    @Operation(summary = "Deletes a user by ID.")
    @RolesAllowed(SecurityConstants.ADMIN_ROLE)
    @DeleteMapping(value = "/delete")
    public void deleteUser(@Parameter(description = "The ID of the user to delete", required = true)
                           @RequestParam Long id) throws UserNotFoundException {
        userService.delete(id);
    }

    @Operation(summary = "Returns count of existing users.")
    @RolesAllowed(SecurityConstants.ADMIN_ROLE)
    @GetMapping(value = "/count", produces = APPLICATION_JSON_VALUE)
    public long getUsersCount() {
        return userService.getCount();
    }
}
