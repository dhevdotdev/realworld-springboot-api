package dev.dhev.realworld.profile;

import dev.dhev.realworld.profile.dto.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    @Operation(summary = "Get a profile",
               description = "Returns the profile for the given username. `following` reflects the authenticated user's relationship — unauthenticated requests always return `false`.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile returned"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ProfileResponse getProfile(
            @Parameter(description = "Username of the profile to retrieve") @PathVariable String username,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return profileService.getProfile(username, email);
    }

    @PostMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Follow a user",
               description = "Follows the given user. Idempotent — following an already-followed user returns 200 without error.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Now following the user"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "422", description = "Cannot follow yourself")
    })
    public ProfileResponse follow(
            @Parameter(description = "Username to follow") @PathVariable String username,
            Authentication authentication) {
        return profileService.follow(username, authentication.getName());
    }

    @DeleteMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unfollow a user",
               description = "Unfollows the given user. Idempotent — unfollowing a non-followed user returns 200 without error.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unfollowed successfully"),
        @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ProfileResponse unfollow(
            @Parameter(description = "Username to unfollow") @PathVariable String username,
            Authentication authentication) {
        return profileService.unfollow(username, authentication.getName());
    }
}
