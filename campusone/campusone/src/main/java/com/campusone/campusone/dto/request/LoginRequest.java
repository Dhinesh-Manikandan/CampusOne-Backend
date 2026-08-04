package com.campusone.campusone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(@NotBlank @Size(max = 254) String identifier,
		@NotBlank @Size(min = 8, max = 255) String password) {
}
