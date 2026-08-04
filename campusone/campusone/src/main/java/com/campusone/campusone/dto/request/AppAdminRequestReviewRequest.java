package com.campusone.campusone.dto.request;

import jakarta.validation.constraints.Size;

public record AppAdminRequestReviewRequest(@Size(max = 2000) String remarks) {
}
