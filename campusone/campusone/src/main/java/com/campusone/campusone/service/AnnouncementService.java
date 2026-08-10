package com.campusone.campusone.service;

import com.campusone.campusone.dto.request.AnnouncementRequest;
import com.campusone.campusone.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(AnnouncementRequest request);
    List<AnnouncementResponse> getAnnouncementsByEvent(Long eventId);
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request);
    AnnouncementResponse updateAnnouncement(Long id, Long currentUserId, AnnouncementRequest request);
    void deleteAnnouncement(Long id);
    void deleteAnnouncement(Long id, Long currentUserId);
}
