package dev.dhev.realworld.user;

import dev.dhev.realworld.security.JwtService;
import dev.dhev.realworld.shared.exception.EmailAlreadyTakenException;
import dev.dhev.realworld.shared.exception.InvalidCredentialsException;
import dev.dhev.realworld.shared.exception.ResourceNotFoundException;
import dev.dhev.realworld.shared.exception.UsernameAlreadyTakenException;
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

        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.bio() != null) user.setBio(dto.bio());
        if (dto.image() != null) user.setImage(dto.image());
        userRepository.save(user);

        return UserResponse.from(user, jwtService.generateToken(user.getEmail()));
    }
}
