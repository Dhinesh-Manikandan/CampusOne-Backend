package com.campusone.campusone.controller;

import java.security.Principal;
import java.util.List;

import com.campusone.campusone.dto.request.EventAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.EventAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.EventAdminRequestResponse;
import com.campusone.campusone.entity.enums.EventAdminRequestStatus;
import com.campusone.campusone.service.EventAdminRequestService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.service.AdminManagementServiceImpl;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventAdminRequestController {

	private final EventAdminRequestService eventAdminRequestService;
	private final AdminManagementServiceImpl adminManagementService;

	public EventAdminRequestController(EventAdminRequestService eventAdminRequestService,
			AdminManagementServiceImpl adminManagementService) {
		this.eventAdminRequestService = eventAdminRequestService;
		this.adminManagementService = adminManagementService;
	}

	@GetMapping("/api/event-admin/event-admins")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public List<UserResponse> getEventAdmins() {
		return adminManagementService.listEventAdmins();
	}

	@PostMapping("/api/event-admin-requests")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public EventAdminRequestResponse request(@Valid @RequestBody EventAdminRequestCreateRequest request,
			Principal principal) {
		return eventAdminRequestService.createRequest(principal.getName(), request);
	}

	@GetMapping("/api/event-admin-requests/my")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
	public List<EventAdminRequestResponse> myRequests(Principal principal) {
		return eventAdminRequestService.getUserRequests(principal.getName());
	}

	@GetMapping("/api/admin/event-admin-requests")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public List<EventAdminRequestResponse> list(@RequestParam(required = false) EventAdminRequestStatus status) {
		return eventAdminRequestService.listRequests(status);
	}

	@PostMapping("/api/admin/event-admin-requests/{requestId}/approve")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public EventAdminRequestResponse approve(@PathVariable Long requestId, Principal principal) {
		return eventAdminRequestService.approveRequest(requestId, principal.getName());
	}

	@PostMapping("/api/admin/event-admin-requests/{requestId}/reject")
	@PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
	public EventAdminRequestResponse reject(@PathVariable Long requestId,
			@Valid @RequestBody EventAdminRequestReviewRequest request, Principal principal) {
		return eventAdminRequestService.rejectRequest(requestId, principal.getName(), request);
	}
}
