package dev.dhev.realworld.profile;

import dev.dhev.realworld.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ProfileResponse getProfile(@PathVariable String username, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return profileService.getProfile(username, email);
    }

    @PostMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse follow(@PathVariable String username, Authentication authentication) {
        return profileService.follow(username, authentication.getName());
    }

    @DeleteMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    public ProfileResponse unfollow(@PathVariable String username, Authentication authentication) {
        return profileService.unfollow(username, authentication.getName());
    }
}
