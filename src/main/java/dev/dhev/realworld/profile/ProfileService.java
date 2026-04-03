package dev.dhev.realworld.profile;

import dev.dhev.realworld.profile.dto.ProfileResponse;
import dev.dhev.realworld.shared.exception.ResourceNotFoundException;
import dev.dhev.realworld.shared.exception.ValidationException;
import dev.dhev.realworld.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileResponse getProfile(String username, String currentUserEmail) {
        var profile = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("profile"));
        boolean following = currentUserEmail != null && userRepository.findByEmail(currentUserEmail)
                .map(u -> followRepository.existsById(new FollowId(u.getId(), profile.getId())))
                .orElse(false);
        return ProfileResponse.from(profile, following);
    }

    @Transactional
    public ProfileResponse follow(String username, String currentUserEmail) {
        var follower = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("profile"));
        if (follower.getId().equals(following.getId()))
            throw new ValidationException("username", "cannot follow yourself");
        try {
            followRepository.saveAndFlush(new Follow(follower, following));
            log.info("User {} followed {}", follower.getUsername(), following.getUsername());
        } catch (DataIntegrityViolationException ignored) {
            // already following — idempotent
        }
        return ProfileResponse.from(following, true);
    }

    @Transactional
    public ProfileResponse unfollow(String username, String currentUserEmail) {
        var follower = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        var following = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("profile"));
        followRepository.deleteById(new FollowId(follower.getId(), following.getId()));
        log.info("User {} unfollowed {}", follower.getUsername(), following.getUsername());
        return ProfileResponse.from(following, false);
    }
}
