package com.campusone.campusone.controller;

import com.campusone.campusone.dto.request.EventRequest;
import com.campusone.campusone.dto.response.DashboardSummaryResponse;
import com.campusone.campusone.dto.response.EventResponse;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> createEvent(@RequestBody EventRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            request.setCreatedBy(currentUserId);
            return ResponseEntity.ok(eventService.createEvent(request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Failed to create event"));
        }
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            return ResponseEntity.ok(eventService.updateEvent(id, currentUserId, request));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Failed to update event"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            eventService.deleteEvent(id, currentUserId);
            return ResponseEntity.ok(java.util.Map.of("message", "Event deleted successfully"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Failed to delete event"));
        }
    }

    @GetMapping("/admin/{createdBy}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<List<EventResponse>> getEventsByAdmin(@PathVariable Long createdBy) {
        return ResponseEntity.ok(eventService.getEventsByAdmin(createdBy));
    }

    @GetMapping("/admin/{createdBy}/dashboard")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(@PathVariable Long createdBy) {
        return ResponseEntity.ok(eventService.getDashboardSummary(createdBy));
    }

    @GetMapping("/{eventId}/participants/count")
    public ResponseEntity<Long> getParticipantCount(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getParticipantCount(eventId));
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