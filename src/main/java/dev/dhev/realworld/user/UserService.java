package dev.dhev.realworld.user;

import dev.dhev.realworld.security.JwtService;
import dev.dhev.realworld.shared.exception.EmailAlreadyTakenException;
import dev.dhev.realworld.shared.exception.InvalidCredentialsException;
import dev.dhev.realworld.shared.exception.ResourceNotFoundException;
import dev.dhev.realworld.shared.exception.UsernameAlreadyTakenException;
import dev.dhev.realworld.shared.exception.ValidationException;
import dev.dhev.realworld.user.dto.LoginUserRequest;
import dev.dhev.realworld.user.dto.NewUserRequest;
import dev.dhev.realworld.user.dto.UpdateUserRequest;
import dev.dhev.realworld.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(NewUserRequest request) {
        var dto = request.user();
        if (isBlank(dto.username())) throw new ValidationException("username", "can't be blank");
        if (isBlank(dto.email())) throw new ValidationException("email", "can't be blank");
        if (isBlank(dto.password())) throw new ValidationException("password", "can't be blank");
        if (userRepository.existsByEmail(dto.email())) throw new EmailAlreadyTakenException();
        if (userRepository.existsByUsername(dto.username())) throw new UsernameAlreadyTakenException();

        log.info("Registering user: {}", dto.email());
        var user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    public UserResponse login(LoginUserRequest request) {
        var dto = request.user();
        log.info("Login attempt: {}", dto.email());
        if (isBlank(dto.email())) throw new ValidationException("email", "can't be blank");
        if (isBlank(dto.password())) throw new ValidationException("password", "can't be blank");

        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("Failed login attempt for email: {}", dto.email());
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    public UserResponse getCurrentUser(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    @SuppressWarnings("OptionalAssignedToNull") // null = field absent (see OptionalStringDeserializer)
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        var dto = request.user();

        if (dto.email() != null) {
            String newEmail = dto.email().orElse(null);
            if (isBlank(newEmail)) throw new ValidationException("email", "can't be blank");
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail))
                throw new EmailAlreadyTakenException();
            user.setEmail(newEmail);
        }
        if (dto.username() != null) {
            String newUsername = dto.username().orElse(null);
            if (isBlank(newUsername)) throw new ValidationException("username", "can't be blank");
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername))
                throw new UsernameAlreadyTakenException();
            user.setUsername(newUsername);
        }
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.bio() != null) user.setBio(dto.bio().filter(s -> !s.isEmpty()).orElse(null));
        if (dto.image() != null) user.setImage(dto.image().filter(s -> !s.isEmpty()).orElse(null));
        userRepository.save(user);

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
