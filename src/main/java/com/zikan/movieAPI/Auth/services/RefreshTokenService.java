package com.zikan.movieAPI.Auth.services;

import com.zikan.movieAPI.Auth.entities.RefreshToken;
import com.zikan.movieAPI.Auth.entities.User;
import com.zikan.movieAPI.Auth.repository.RefreshTokenRepository;
import com.zikan.movieAPI.Auth.repository.UserRepository;
import com.zikan.movieAPI.exceptions.RefreshTokenExpiredException;
import com.zikan.movieAPI.exceptions.RefreshTokenNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public RefreshToken createRefreshToken(String username){
         User user = userRepository.findByEmail(username)
                 .orElseThrow(() -> new UsernameNotFoundException("Username not found with this email " + username));

        // Extract refresh token from a user

         RefreshToken refreshToken = user.getRefreshToken();

         if (refreshToken == null) {
             long refreshTokenValidity = 30 * 10000;
             refreshToken = RefreshToken.builder()
                     .refreshToken(UUID.randomUUID().toString())
                     .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                     .user(user)
                     .build();
             refreshTokenRepository.save(refreshToken);
         }
         return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new RefreshTokenNotFoundException("Refresh token Not found"));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);
            throw  new RefreshTokenExpiredException("Refresh Token Expired");
        }

        return refToken;
    }

}
