package com.campusone.campusone.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		@NotBlank @Size(max = 254) @JsonAlias({"email", "username", "registrationNumber", "userIdentifier"}) String identifier,
		@NotBlank @Size(min = 8, max = 255) String password) {
}
