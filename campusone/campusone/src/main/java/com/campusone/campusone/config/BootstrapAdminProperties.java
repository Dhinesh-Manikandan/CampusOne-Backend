package com.campusone.campusone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.bootstrap-admin")
public record BootstrapAdminProperties(boolean enabled, String registrationNumber, String fullName, String email,
		String password, String department, Integer year, String phoneNumber, String profileImage) {
}
