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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

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

        var user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    public UserResponse login(LoginUserRequest request) {
        var dto = request.user();
        if (isBlank(dto.email())) throw new ValidationException("email", "can't be blank");
        if (isBlank(dto.password())) throw new ValidationException("password", "can't be blank");

        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    public UserResponse getCurrentUser(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }

    public UserResponse updateUser(String email, UpdateUserRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        var dto = request.user();

        if (dto.email() != null) {
            String email2 = dto.email().orElse(null);
            if (isBlank(email2)) throw new ValidationException("email", "can't be blank");
            user.setEmail(email2);
        }
        if (dto.username() != null) {
            String username = dto.username().orElse(null);
            if (isBlank(username)) throw new ValidationException("username", "can't be blank");
            user.setUsername(username);
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
