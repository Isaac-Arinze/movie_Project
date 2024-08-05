package com.zikan.movieAPI.controller;

import com.zikan.movieAPI.Auth.entities.RefreshToken;
import com.zikan.movieAPI.Auth.entities.User;
import com.zikan.movieAPI.Auth.services.AuthService;
import com.zikan.movieAPI.Auth.services.JwtService;
import com.zikan.movieAPI.Auth.services.RefreshTokenService;
import com.zikan.movieAPI.Auth.utils.AuthResponse;
import com.zikan.movieAPI.Auth.utils.LoginRequest;
import com.zikan.movieAPI.Auth.utils.RefreshTokenRequest;
import com.zikan.movieAPI.Auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth/")
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));

    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken (@RequestBody RefreshTokenRequest refreshTokenRequest) {
         RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
       User user = refreshToken.getUser();

       String accessToken =  jwtService.generateToken(user);

       return ResponseEntity.ok(AuthResponse.builder()
                       .accessToken(accessToken)
                       .refreshToken(refreshToken.getRefreshToken())
                        .build());
    }



}
