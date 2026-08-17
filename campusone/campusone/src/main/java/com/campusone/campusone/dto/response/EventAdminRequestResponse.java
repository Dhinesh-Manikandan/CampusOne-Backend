package com.campusone.campusone.dto.response;

import java.time.Instant;

public record EventAdminRequestResponse(Long id, String requestReason, String status, UserResponse requestedBy,
		UserResponse reviewedBy, Instant requestedAt, Instant reviewedAt, String remarks) {
}
