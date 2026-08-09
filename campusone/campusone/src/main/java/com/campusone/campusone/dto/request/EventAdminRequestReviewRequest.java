package com.campusone.campusone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EventAdminRequestReviewRequest(
        @NotBlank
        @Size(max = 2000)
        String remarks
) {
}