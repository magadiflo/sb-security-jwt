package com.magadiflo.services;

import com.magadiflo.models.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

    int deleteByUserId(Long userId);

}
