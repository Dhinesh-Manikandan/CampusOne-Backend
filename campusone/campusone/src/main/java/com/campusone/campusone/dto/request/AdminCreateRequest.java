package com.campusone.campusone.dto.request;

import com.campusone.campusone.entity.enums.RoleName;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminCreateRequest(@NotBlank @Pattern(regexp = "^[A-Za-z0-9]+$") @Size(max = 20) String registrationNumber,
		@NotBlank @Size(max = 150) String fullName, @NotBlank @Email @Size(max = 254) String email,
		@NotBlank @Size(min = 8, max = 255) String password, @NotBlank @Size(max = 100) String department,
		@NotNull @Min(1) Integer year, @NotBlank @Pattern(regexp = "^[0-9]{10,15}$") @Size(max = 15) String phoneNumber,
		@Size(max = 512) String profileImage, @NotNull RoleName roleName) {
}
