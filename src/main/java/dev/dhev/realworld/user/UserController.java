package dev.dhev.realworld.user;

import dev.dhev.realworld.user.dto.LoginUserRequest;
import dev.dhev.realworld.user.dto.NewUserRequest;
import dev.dhev.realworld.user.dto.UpdateUserRequest;
import dev.dhev.realworld.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User and Authentication")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user",
               description = "Creates a new account. Returns the user with a JWT token.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "422", description = "Validation failed — username or email already taken, or fields are blank")
    })
    public UserResponse register(@RequestBody NewUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/api/users/login")
    @Operation(summary = "Login",
               description = "Authenticates with email and password. Returns the user with a fresh JWT token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password"),
        @ApiResponse(responseCode = "422", description = "Validation failed — missing fields")
    })
    public UserResponse login(@RequestBody LoginUserRequest request) {
        return userService.login(request);
    }

    @GetMapping("/api/user")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user",
               description = "Returns the currently authenticated user. Requires a valid JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current user returned"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token")
    })
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getCurrentUser(userDetails.getUsername());
    }

    @PutMapping("/api/user")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user",
               description = "Updates email, username, password, bio, or image. All fields are optional — only provided fields are updated.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "422", description = "Validation failed — blank field or email/username already taken")
    })
    public UserResponse updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestBody UpdateUserRequest request) {
        return userService.updateUser(userDetails.getUsername(), request);
    }
}
