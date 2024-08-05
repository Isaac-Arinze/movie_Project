package com.zikan.movieAPI.Auth.repository;

import com.zikan.movieAPI.Auth.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface
RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {


    Optional<RefreshToken> findByRefreshToken (String refreshToken);
}
