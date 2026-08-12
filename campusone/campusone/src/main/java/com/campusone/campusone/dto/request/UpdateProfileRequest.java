package com.campusone.campusone.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
		@NotBlank @Size(max = 150) String fullName,
		@NotBlank @Size(max = 100) String department,
		@NotNull @Min(1) Integer year,
		@NotBlank @Pattern(regexp = "^[0-9]{10,15}$") @Size(max = 15) String phoneNumber
) {
}
