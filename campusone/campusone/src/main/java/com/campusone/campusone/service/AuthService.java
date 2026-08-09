package com.campusone.campusone.service;

import com.campusone.campusone.dto.request.LoginRequest;
import com.campusone.campusone.dto.request.RefreshTokenRequest;
import com.campusone.campusone.dto.request.SignupRequest;
import com.campusone.campusone.dto.response.AuthResponse;

public interface AuthService {

	AuthResponse signup(SignupRequest request);

	AuthResponse login(LoginRequest request);

	AuthResponse refreshToken(RefreshTokenRequest request);
}
