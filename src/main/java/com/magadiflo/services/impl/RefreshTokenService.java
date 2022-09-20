package com.magadiflo.services.impl;

import com.magadiflo.exceptions.TokenRefreshException;
import com.magadiflo.models.RefreshToken;
import com.magadiflo.repository.RefreshTokenRepository;
import com.magadiflo.repository.UserRepository;
import com.magadiflo.services.IRefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService implements IRefreshTokenService {

    //private final int jwtRefreshExpirationMs = 86400000; //24h

    //Para testear
    private final int jwtRefreshExpirationMs = 120000; //2 minutos
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return this.refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(this.userRepository.findById(userId).get());
        refreshToken.setExpirityDate(Instant.now().plusMillis(this.jwtRefreshExpirationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = this.refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if(refreshToken.getExpirityDate().compareTo(Instant.now()) < 0) {
            this.refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshToken.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return this.refreshTokenRepository.deleteByUser(this.userRepository.findById(userId).get());
    }
}
