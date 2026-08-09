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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EventAdminRequestController {

    private final EventAdminRequestService eventAdminRequestService;

    public EventAdminRequestController(
            EventAdminRequestService eventAdminRequestService) {
        this.eventAdminRequestService = eventAdminRequestService;
    }

    // Student submits an Event Admin request
    @PostMapping("/api/student/event-admin-requests")
    @PreAuthorize("hasRole('STUDENT')")
    public EventAdminRequestResponse createRequest(
            @Valid @RequestBody EventAdminRequestCreateRequest request,
            Principal principal) {

        return eventAdminRequestService.createRequest(
                principal.getName(),
                request);
    }

    // Student views their own Event Admin requests
    @GetMapping("/api/student/event-admin-requests")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EventAdminRequestResponse> getMyRequests(
            Principal principal) {

        return eventAdminRequestService.getMyRequests(
                principal.getName());
    }

    // App Admin views all Event Admin requests
    @GetMapping("/api/admin/event-admin-requests")
    @PreAuthorize("hasRole('APP_ADMIN')")
    public List<EventAdminRequestResponse> listRequests(
            @RequestParam(required = false) EventAdminRequestStatus status) {

        return eventAdminRequestService.listRequests(status);
    }

    // App Admin approves a request
    @PostMapping("/api/admin/event-admin-requests/{requestId}/approve")
    @PreAuthorize("hasRole('APP_ADMIN')")
    public EventAdminRequestResponse approveRequest(
            @PathVariable Long requestId,
            Principal principal) {

        return eventAdminRequestService.approveRequest(
                requestId,
                principal.getName());
    }

    // App Admin rejects a request
    @PostMapping("/api/admin/event-admin-requests/{requestId}/reject")
    @PreAuthorize("hasRole('APP_ADMIN')")
    public EventAdminRequestResponse rejectRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody EventAdminRequestReviewRequest request,
            Principal principal) {

        return eventAdminRequestService.rejectRequest(
                requestId,
                principal.getName(),
                request);
    }
}