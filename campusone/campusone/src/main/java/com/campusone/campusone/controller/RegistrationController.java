package com.campusone.campusone.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.service.RegistrationService;

import lombok.RequiredArgsConstructor;



import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    // Register user for an event
    @PostMapping("/{eventId}/register")
    @PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<EventRegistration> register(
            @PathVariable Long eventId,
            @RequestBody RegistrationRequest request
    ){
        return ResponseEntity.ok(
                registrationService.register(
                        eventId,
                        request
                )
        );
    }

    // Get all participants of an event with optional search query
    @GetMapping("/{eventId}/participants")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<List<EventRegistration>> getParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) String search
    ){
        return ResponseEntity.ok(
                registrationService.getParticipants(eventId, search)
        );
    }

    // Get all registrations for a specific student / user
    @GetMapping("/user/{userId}/registrations")
    @PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'ADMIN', 'APP_ADMIN')")
    public ResponseEntity<List<EventRegistration>> getUserRegistrations(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(
                registrationService.getUserRegistrations(userId)
        );
    }

    // Cancel registration
    @DeleteMapping("/{eventId}/register/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<String> cancelRegistration(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ){

        registrationService.cancelRegistration(
                eventId,
                userId
        );


        return ResponseEntity.ok(
                "Registration cancelled successfully"
        );

    }

}