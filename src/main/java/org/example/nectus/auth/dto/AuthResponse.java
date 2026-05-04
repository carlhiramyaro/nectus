package org.example.nectus.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
