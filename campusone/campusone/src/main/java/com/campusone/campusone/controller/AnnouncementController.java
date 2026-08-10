package com.campusone.campusone.controller;

import com.campusone.campusone.dto.request.AnnouncementRequest;
import com.campusone.campusone.dto.response.AnnouncementResponse;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> createAnnouncement(@RequestBody AnnouncementRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            request.setCreatedBy(currentUserId);
            return ResponseEntity.ok(announcementService.createAnnouncement(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Failed to create announcement"));
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByEvent(eventId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        try {
            Long currentUserId = getCurrentUserId();
            return ResponseEntity.ok(announcementService.updateAnnouncement(id, currentUserId, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Failed to update announcement"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'ADMIN')")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Long id) {
        try {
            Long currentUserId = getCurrentUserId();
            announcementService.deleteAnnouncement(id, currentUserId);
            return ResponseEntity.ok(java.util.Map.of("message", "Announcement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage() != null ? e.getMessage() : "Failed to delete announcement"));
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
