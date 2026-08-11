package com.campusone.campusone.controller;

import com.campusone.campusone.dto.request.AnnouncementRequest;
import com.campusone.campusone.dto.response.AnnouncementResponse;
import com.campusone.campusone.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<AnnouncementResponse> createAnnouncement(@RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.createAnnouncement(request));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByEvent(eventId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EVENT_ADMIN', 'APP_ADMIN')")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok("Announcement deleted successfully");
    }
}
