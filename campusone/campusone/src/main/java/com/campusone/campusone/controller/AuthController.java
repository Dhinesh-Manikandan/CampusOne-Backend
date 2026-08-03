package com.campusone.campusone.controller;

import com.campusone.campusone.dto.request.AdminCreateRequest;
import com.campusone.campusone.dto.request.LoginRequest;
import com.campusone.campusone.dto.request.RefreshTokenRequest;
import com.campusone.campusone.dto.request.SignupRequest;
import com.campusone.campusone.dto.response.AuthResponse;
import com.campusone.campusone.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
		return authService.signup(request);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		return authService.refreshToken(request);
	}

	@PostMapping("/admin")
	@PreAuthorize("hasRole('APP_ADMIN')")
	public AuthResponse createAdmin(@Valid @RequestBody AdminCreateRequest request) {
		return authService.createAdminAccount(request);
	}
}
