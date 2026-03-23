package dev.dhev.realworld.user;

import dev.dhev.realworld.user.dto.LoginUserRequest;
import dev.dhev.realworld.user.dto.NewUserRequest;
import dev.dhev.realworld.user.dto.UpdateUserRequest;
import dev.dhev.realworld.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody NewUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/api/users/login")
    public UserResponse login(@RequestBody LoginUserRequest request) {
        return userService.login(request);
    }

    @GetMapping("/api/user")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getCurrentUser(userDetails.getUsername());
    }

    @PutMapping("/api/user")
    public UserResponse updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userDetails.getUsername(), request);
    }
}
