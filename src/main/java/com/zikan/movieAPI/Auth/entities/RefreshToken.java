package com.zikan.movieAPI.Auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(nullable = false, length = 500)
    @NotBlank(message = "Please Enter refresh token value")
    private String refreshToken;
    @Column(nullable = false)
    private Instant expirationTime;

    @OneToOne
     private User user;


}
