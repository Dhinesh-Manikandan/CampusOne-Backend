package com.campusone.campusone.service.impl;

import com.campusone.campusone.dto.request.AnnouncementRequest;
import com.campusone.campusone.dto.response.AnnouncementResponse;
import com.campusone.campusone.entity.Announcement;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.AnnouncementRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        Long creatorId = request.getCreatedBy() != null ? request.getCreatedBy() : event.getCreatedBy();
        User user = (creatorId != null ? userRepository.findById(creatorId) : java.util.Optional.<User>empty())
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElseThrow(() -> new RuntimeException("No valid user found to post announcement")));

        Announcement announcement = Announcement.builder()
                .event(event)
                .createdBy(user)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToResponse(announcementRepository.save(announcement));
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return announcementRepository.findByEvent(event)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(announcementRepository.save(announcement));
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .eventId(announcement.getEvent().getId())
                .createdBy(announcement.getCreatedBy().getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }
}
