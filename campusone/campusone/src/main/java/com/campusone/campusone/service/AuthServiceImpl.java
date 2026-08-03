package com.campusone.campusone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.Set;

import com.campusone.campusone.config.JwtProperties;
import com.campusone.campusone.dto.request.AdminCreateRequest;
import com.campusone.campusone.dto.request.LoginRequest;
import com.campusone.campusone.dto.request.RefreshTokenRequest;
import com.campusone.campusone.dto.request.SignupRequest;
import com.campusone.campusone.dto.response.AuthResponse;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.entity.RefreshToken;
import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.RefreshTokenRepository;
import com.campusone.campusone.repository.RoleRepository;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;

	public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder, JwtService jwtService, JwtProperties jwtProperties) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.jwtProperties = jwtProperties;
	}

	@Override
	public AuthResponse signup(SignupRequest request) {
		return registerUser(request.registrationNumber(), request.fullName(), request.email(), request.password(),
				request.department(), request.year(), request.phoneNumber(), request.profileImage(),
				RoleName.ROLE_STUDENT);
	}

	@Override
	public AuthResponse createAdminAccount(AdminCreateRequest request) {
		if (request.roleName() == RoleName.ROLE_STUDENT) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin account must use an admin role");
		}
		return registerUser(request.registrationNumber(), request.fullName(), request.email(), request.password(),
				request.department(), request.year(), request.phoneNumber(), request.profileImage(),
				request.roleName());
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.identifier(), request.password()));
		} catch (AuthenticationException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials", exception);
		}
		User user = findUser(request.identifier());
		return buildAuthResponse(user);
	}

	@Override
	public AuthResponse refreshToken(RefreshTokenRequest request) {
		RefreshToken existing = refreshTokenRepository.findByToken(request.refreshToken())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));
		if (!jwtService.isRefreshToken(existing.getToken()) || existing.isRevoked() || existing.isExpired()
				|| jwtService.isTokenExpired(existing.getToken())) {
			existing.setRevoked(true);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
		}
		existing.setRevoked(true);
		return buildAuthResponse(existing.getUser());
	}

	private AuthResponse registerUser(String registrationNumber, String fullName, String email, String password,
			String department, Integer year, String phoneNumber, String profileImage, RoleName roleName) {
		if (userRepository.existsByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(email, registrationNumber)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
		}
		User user = new User();
		user.setRegistrationNumber(registrationNumber);
		user.setFullName(fullName);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		user.setDepartment(department);
		user.setYear(year);
		user.setPhoneNumber(phoneNumber);
		user.setProfileImage(profileImage);
		user.setEnabled(true);
		user.setRoles(Set.of(ensureRole(roleName)));
		user = userRepository.save(user);
		return buildAuthResponse(user);
	}

	private AuthResponse buildAuthResponse(User user) {
		RefreshToken refreshToken = createRefreshToken(user);
		String accessToken = jwtService.generateAccessToken(user);
		return new AuthResponse(toUserResponse(user), accessToken, refreshToken.getToken(),
				jwtService.extractExpirationInstant(accessToken), refreshToken.getExpiresAt().atZone(ZoneId.systemDefault())
						.toInstant());
	}

	private RefreshToken createRefreshToken(User user) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		String token = jwtService.generateRefreshToken(user);
		refreshToken.setToken(token);
		refreshToken.setExpiresAt(LocalDateTime.now().plus(jwtProperties.refreshTokenTtl()));
		refreshToken.setRevoked(false);
		return refreshTokenRepository.save(refreshToken);
	}

	private User findUser(String identifier) {
		return userRepository.findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(identifier, identifier)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
	}

	private Role ensureRole(RoleName roleName) {
		return roleRepository.findByRoleName(roleName).orElseGet(() -> {
			Role role = new Role();
			role.setRoleName(roleName);
			return roleRepository.save(role);
		});
	}

	private UserResponse toUserResponse(User user) {
		Set<String> roles = new LinkedHashSet<>();
		user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
		return new UserResponse(user.getId(), user.getRegistrationNumber(), user.getFullName(), user.getEmail(),
				user.getDepartment(), user.getYear(), user.getPhoneNumber(), user.getProfileImage(), user.isEnabled(),
				roles);
	}
}
