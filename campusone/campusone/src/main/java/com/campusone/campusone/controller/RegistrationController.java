package com.campusone.campusone.controller;

import java.util.List;

import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.service.RegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;

    // Register user for an event
    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> register(
            @PathVariable Long eventId,
            @RequestBody RegistrationRequest request
    ){
        try {
            if (request.getUserId() == null) {
                request.setUserId(getCurrentUserId());
            }

            return ResponseEntity.ok(
                    registrationService.register(
                            eventId,
                            request
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Registration failed")
            );
        }
    }

    // Get participants of an event with optional search filter
    @GetMapping("/{eventId}/participants")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<List<EventRegistration>> getParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false) String search
    ){
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(
                registrationService.getParticipants(eventId, currentUserId, search)
        );
    }

    // Dedicated search endpoint for participants filtering
    @GetMapping("/{eventId}/participants/search")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<List<EventRegistration>> searchParticipants(
            @PathVariable Long eventId,
            @RequestParam(required = false, name = "query") String query,
            @RequestParam(required = false, name = "search") String search
    ){
        Long currentUserId = getCurrentUserId();
        String searchTerm = (query != null && !query.trim().isEmpty()) ? query : search;
        return ResponseEntity.ok(
                registrationService.getParticipants(eventId, currentUserId, searchTerm)
        );
    }

    // Cancel registration
    @DeleteMapping("/{eventId}/register/{userId}")
    public ResponseEntity<?> cancelRegistration(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ){
        try {
            registrationService.cancelRegistration(
                    eventId,
                    userId
            );

            return ResponseEntity.ok(
                    java.util.Map.of("message", "Registration cancelled successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Failed to cancel registration")
            );
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Authenticated user is required");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        return user.getId();
    }
}