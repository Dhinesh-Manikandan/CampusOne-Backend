package com.campusone.campusone.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleProtectedController {

	@GetMapping("/student/me")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public Map<String, Object> student(Principal principal) {
		return Map.of("message", "Student access granted", "principal", principal.getName());
	}

	@GetMapping("/event-admin/dashboard")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public Map<String, Object> eventAdmin(Principal principal) {
		return Map.of("message", "Event admin access granted", "principal", principal.getName());
	}

	@GetMapping("/admin/dashboard")
	@PreAuthorize("hasRole('APP_ADMIN')")
	public Map<String, Object> admin(Principal principal) {
		return Map.of("message", "App admin access granted", "principal", principal.getName());
	}
}
