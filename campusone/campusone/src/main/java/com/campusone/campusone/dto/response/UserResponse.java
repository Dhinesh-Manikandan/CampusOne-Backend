package com.campusone.campusone.dto.response;

import java.util.Set;

public record UserResponse(Long id, String registrationNumber, String fullName, String email, String department,
		Integer year, String phoneNumber, boolean enabled, Set<String> roles) {
}
