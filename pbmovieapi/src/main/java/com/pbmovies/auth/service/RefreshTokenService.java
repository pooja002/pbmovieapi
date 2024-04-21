package com.pbmovies.auth.service;

import com.pbmovies.auth.entities.RefreshToken;
import com.pbmovies.auth.entities.User;
import com.pbmovies.auth.repositories.RefreshTokenRepository;
import com.pbmovies.auth.repositories.UserRepository;
import lombok.Getter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Getter
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username)
    {
       User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found "+username));
       RefreshToken refreshToken = user.getRefreshToken();


       if(refreshToken == null)
       {
           long refreshTokenValidity = 5*60*60*10000;
           refreshToken = RefreshToken.builder().refreshToken(UUID.randomUUID().toString()).expirationTime(Instant.now().plusMillis(refreshTokenValidity)).user(user).build();
       }

       refreshTokenRepository.save(refreshToken);
       return refreshToken;
    }

    /*
    Verify Refresh Token* */

    public RefreshToken verifyRefreshToken(String refreshToken)
    {
       RefreshToken refreshToken1 = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new RuntimeException("Token not found"));

       if(refreshToken1.getExpirationTime().compareTo(Instant.now())<0)
       {
           refreshTokenRepository.delete(refreshToken1);
           throw new RuntimeException("Refresh Token is expired");
       }

       return refreshToken1;
    }

}
