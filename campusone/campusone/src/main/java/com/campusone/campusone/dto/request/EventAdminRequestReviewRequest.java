package com.campusone.campusone.dto.request;

import jakarta.validation.constraints.Size;

public record EventAdminRequestReviewRequest(@Size(max = 2000) String remarks) {
}
