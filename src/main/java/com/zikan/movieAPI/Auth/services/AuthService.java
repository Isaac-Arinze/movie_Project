package com.zikan.movieAPI.Auth.services;

import com.zikan.movieAPI.Auth.entities.User;
import com.zikan.movieAPI.Auth.entities.UserRole;
import com.zikan.movieAPI.Auth.repository.UserRepository;
import com.zikan.movieAPI.Auth.utils.AuthResponse;
import com.zikan.movieAPI.Auth.utils.LoginRequest;
import com.zikan.movieAPI.Auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email or username already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        // Create and save the user
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER) // Ensure UserRole is properly defined and assigned
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        logger.info("User registered successfully: {}", registerRequest.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }


//    public AuthResponse login(LoginRequest loginRequest) {
//        // Authenticate the user
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        // Retrieve user details
//        var user = userRepository.findByEmail(loginRequest.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // Generate tokens
//        var accessToken = jwtService.generateToken(user);
//        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());
//
//        logger.info("User logged in successfully: {}", loginRequest.getEmail());
//
//        return AuthResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken.getRefreshToken())
//                .build();
//    }

    public AuthResponse login(LoginRequest loginRequest) {
        logger.debug("Received login request: Email={}, Password={}", loginRequest.getEmail(), loginRequest.getPassword());
        try {
            // Validate input
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty() ||
                    loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Email and password must not be null or empty");
            }

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Check if authentication was successful
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new BadCredentialsException("Authentication failed");
            }

            // Retrieve user details
            var user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate tokens
            var accessToken = jwtService.generateToken(user);
            var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

            logger.info("User logged in successfully: {}", loginRequest.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getRefreshToken())
                    .build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input during login for user {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Invalid input: " + e.getMessage());
        } catch (BadCredentialsException e) {
            logger.error("Login failed for user {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            logger.error("User not found for email {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("User not found");
        } catch (Exception e) {
            logger.error("Unexpected error during login for user {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Unexpected error occurred");
        }
    }


}
