package com.campusone.campusone.controller;

import java.security.Principal;
import java.util.List;

import com.campusone.campusone.dto.request.AppAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.AppAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.AppAdminRequestResponse;
import com.campusone.campusone.entity.enums.AppAdminRequestStatus;
import com.campusone.campusone.service.AppAdminRequestService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppAdminRequestController {

	private final AppAdminRequestService appAdminRequestService;

	public AppAdminRequestController(AppAdminRequestService appAdminRequestService) {
		this.appAdminRequestService = appAdminRequestService;
	}

	@PostMapping("/api/app-admin-requests")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN')")
	public AppAdminRequestResponse request(@Valid @RequestBody AppAdminRequestCreateRequest request,
			Principal principal) {
		return appAdminRequestService.createRequest(principal.getName(), request);
	}

	@GetMapping("/api/app-admin-requests/my")
	@PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN')")
	public List<AppAdminRequestResponse> myRequests(Principal principal) {
		return appAdminRequestService.getUserRequests(principal.getName());
	}

	@GetMapping("/api/admin/app-admin-requests")
	@PreAuthorize("hasRole('APP_ADMIN')")
	public List<AppAdminRequestResponse> list(@RequestParam(required = false) AppAdminRequestStatus status) {
		return appAdminRequestService.listRequests(status);
	}

	@PostMapping("/api/admin/app-admin-requests/{requestId}/approve")
	@PreAuthorize("hasRole('APP_ADMIN')")
	public AppAdminRequestResponse approve(@PathVariable Long requestId, Principal principal) {
		return appAdminRequestService.approveRequest(requestId, principal.getName());
	}

	@PostMapping("/api/admin/app-admin-requests/{requestId}/reject")
	@PreAuthorize("hasRole('APP_ADMIN')")
	public AppAdminRequestResponse reject(@PathVariable Long requestId,
			@Valid @RequestBody AppAdminRequestReviewRequest request, Principal principal) {
		return appAdminRequestService.rejectRequest(requestId, principal.getName(), request);
	}
}
