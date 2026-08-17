package com.campusone.campusone.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.campusone.campusone.dto.response.MessageResponse;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AdminManagementServiceImpl {

	private final UserRepository userRepository;

	public AdminManagementServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<UserResponse> listAppAdmins() {
		return userRepository.findDistinctByRoles_RoleName(RoleName.ROLE_APP_ADMIN).stream().map(this::toUserResponse)
				.toList();
	}

	public MessageResponse removeAppAdmin(Long adminId, String currentUsername) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
		boolean isAppAdmin = admin.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.ROLE_APP_ADMIN);
		if (!isAppAdmin) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
		}
		User currentUser = userRepository.findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(currentUsername,
				currentUsername).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
		if (admin.getId().equals(currentUser.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot remove your own admin role");
		}
		long remainingAppAdmins = userRepository.findDistinctByRoles_RoleName(RoleName.ROLE_APP_ADMIN).stream()
				.filter(user -> !user.getId().equals(admin.getId())).count();
		if (remainingAppAdmins == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one app admin must remain");
		}
		admin.getRoles().removeIf(role -> role.getRoleName() == RoleName.ROLE_APP_ADMIN);
		if (admin.getRoles().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"App admin must retain a student or event admin role");
		}
		userRepository.save(admin);
		return new MessageResponse("Admin role removed successfully");
	}

	public List<UserResponse> listEventAdmins() {
		return userRepository.findDistinctByRoles_RoleName(RoleName.ROLE_EVENT_ADMIN).stream().map(this::toUserResponse)
				.toList();
	}

	public MessageResponse removeEventAdmin(Long adminId) {
		User admin = userRepository.findById(adminId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event Admin not found"));
		admin.getRoles().removeIf(role -> role.getRoleName() == RoleName.ROLE_EVENT_ADMIN);
		userRepository.save(admin);
		return new MessageResponse("Event Admin role removed successfully");
	}

	private UserResponse toUserResponse(User user) {
		Set<String> roles = new LinkedHashSet<>();
		user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
		return new UserResponse(user.getId(), user.getRegistrationNumber(), user.getFullName(), user.getEmail(),
				user.getDepartment(), user.getYear(), user.getPhoneNumber(), user.isEnabled(), roles);
	}
}
