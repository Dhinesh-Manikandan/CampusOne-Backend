package com.campusone.campusone.dto.response;

import java.time.Instant;

public record AuthResponse(UserResponse user, String accessToken, String refreshToken, Instant accessTokenExpiresAt,
		Instant refreshTokenExpiresAt) {
}
