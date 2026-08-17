package com.campusone.campusone.controller;

import java.security.Principal;
import java.util.List;

import com.campusone.campusone.dto.response.MessageResponse;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.service.AdminManagementServiceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/application-admins")
@PreAuthorize("hasRole('APP_ADMIN')")
public class AdminManagementController {

	private final AdminManagementServiceImpl adminManagementService;

	public AdminManagementController(AdminManagementServiceImpl adminManagementService) {
		this.adminManagementService = adminManagementService;
	}

	@GetMapping
	public List<UserResponse> list() {
		return adminManagementService.listAppAdmins();
	}

	@DeleteMapping("/{adminId}")
	public MessageResponse delete(@PathVariable Long adminId, Principal principal) {
		return adminManagementService.removeAppAdmin(adminId, principal.getName());
	}

	@GetMapping("/event-admins")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public List<UserResponse> listEventAdmins() {
		return adminManagementService.listEventAdmins();
	}

	@DeleteMapping("/event-admins/{adminId}")
	public MessageResponse deleteEventAdmin(@PathVariable Long adminId) {
		return adminManagementService.removeEventAdmin(adminId);
	}
}
