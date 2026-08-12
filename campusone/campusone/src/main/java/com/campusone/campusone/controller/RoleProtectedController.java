package com.campusone.campusone.controller;

import java.security.Principal;
import java.util.Map;

import com.campusone.campusone.dto.request.ChangePasswordRequest;
import com.campusone.campusone.dto.request.UpdateProfileRequest;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.entity.enums.AppAdminRequestStatus;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.AppAdminRequestRepository;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoleProtectedController {

	private final UserService userService;
	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final AppAdminRequestRepository appAdminRequestRepository;

	/**
	 * Returns the full profile (including all assigned roles) for the currently
	 * authenticated user.
	 */
	@GetMapping("/student/me")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public UserResponse student(Principal principal) {
		return userService.getUserDetails(principal.getName());
	}

	/**
	 * Update editable profile details (fullName, department, year, phoneNumber).
	 */
	@PutMapping({"/student/profile", "/user/profile"})
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public UserResponse updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(principal.getName(), request);
	}

	/**
	 * Change password for the currently authenticated user after verifying current password.
	 */
	@PutMapping("/user/change-password")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public ResponseEntity<Map<String, String>> changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
		userService.changePassword(principal.getName(), request);
		return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
	}

	@GetMapping("/event-admin/dashboard")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public Map<String, Object> eventAdmin(Principal principal) {
		return Map.of("message", "Event admin access granted", "principal", principal.getName());
	}

	@GetMapping("/admin/dashboard")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public Map<String, Object> admin(Principal principal) {
		long totalAppAdmins = userRepository.countDistinctByRoles_RoleName(RoleName.ROLE_APP_ADMIN);
		long totalEventAdmins = userRepository.countDistinctByRoles_RoleName(RoleName.ROLE_EVENT_ADMIN);
		long totalStudents = userRepository.findAll().stream()
				.filter(u -> u.getRoles().stream().allMatch(r -> r.getRoleName() == RoleName.ROLE_STUDENT))
				.count();
		long pendingRequests = appAdminRequestRepository.findByStatusOrderByRequestedAtDesc(AppAdminRequestStatus.PENDING).size();

		return Map.of(
			"totalAppAdmins", totalAppAdmins,
			"totalEventAdmins", totalEventAdmins,
			"totalStudents", totalStudents,
			"pendingRequests", pendingRequests
		);
	}
}
