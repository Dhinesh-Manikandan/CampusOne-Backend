package com.campusone.campusone.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import com.campusone.campusone.config.JwtProperties;
import com.campusone.campusone.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class JwtService {

	private final JwtProperties jwtProperties;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	@PostConstruct
	public void validateConfiguration() {
		Assert.hasText(jwtProperties.secret(), "JWT secret must be configured");
		Assert.isTrue(jwtProperties.secret().getBytes(StandardCharsets.UTF_8).length >= 32,
				"JWT secret must be at least 32 bytes");
	}

	public String generateAccessToken(User user) {
		return generateToken(user, jwtProperties.accessTokenTtl(), "access");
	}

	public String generateRefreshToken(User user) {
		return generateToken(user, jwtProperties.refreshTokenTtl(), "refresh");
	}

	public boolean isTokenExpired(String token) {
		try {
			return extractExpiration(token).toInstant().isBefore(Instant.now());
		} catch (JwtException exception) {
			return true;
		}
	}

	public boolean isAccessToken(String token) {
		return "access".equalsIgnoreCase(extractTokenType(token));
	}

	public boolean isRefreshToken(String token) {
		return "refresh".equalsIgnoreCase(extractTokenType(token));
	}

	public String extractSubject(String token) {
		return extractAllClaims(token).getSubject();
	}

	public Instant extractExpirationInstant(String token) {
		return extractExpiration(token).toInstant();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		return userDetails.getUsername().equalsIgnoreCase(extractSubject(token)) && !isTokenExpired(token)
				&& isAccessToken(token);
	}

	private String generateToken(User user, Duration ttl, String tokenType) {
		Date issuedAt = new Date();
		Date expiration = Date.from(Instant.now().plus(ttl));
		return Jwts.builder().subject(user.getEmail()).issuer(jwtProperties.issuer()).issuedAt(issuedAt)
				.expiration(expiration).claim("token_type", tokenType).claim("user_id", user.getId())
				.claim("roles", user.getRoles().stream().map(role -> role.getRoleName().name()).toList())
				.signWith(secretKey()).compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(secretKey()).build().parseSignedClaims(token).getPayload();
	}

	private Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private String extractTokenType(String token) {
		return extractAllClaims(token).get("token_type", String.class);
	}

	private SecretKey secretKey() {
		return Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
	}
}