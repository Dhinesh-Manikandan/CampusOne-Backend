package com.campusone.campusone;

import com.campusone.campusone.dto.request.AnnouncementRequest;
import com.campusone.campusone.entity.Announcement;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.AnnouncementRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.impl.AnnouncementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementOwnershipTest {

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldRejectCreateAnnouncementWhenUserIsNotEventCreator() {
        Event event = Event.builder().id(100L).createdBy(1L).build();
        User user = User.builder().id(2L).email("other@campus.com").build();

        AnnouncementRequest request = new AnnouncementRequest();
        request.setEventId(100L);
        request.setCreatedBy(2L);
        request.setTitle("Unauthorized Announcement");
        request.setContent("Testing");

        when(eventRepository.findById(100L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(SecurityException.class, () -> announcementService.createAnnouncement(request));
    }

    @Test
    void shouldRejectUpdateAnnouncementWhenUserIsNotEventCreator() {
        Event event = Event.builder().id(100L).createdBy(1L).build();
        Announcement announcement = Announcement.builder().id(50L).event(event).build();

        AnnouncementRequest request = new AnnouncementRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        when(announcementRepository.findById(50L)).thenReturn(Optional.of(announcement));

        assertThrows(SecurityException.class, () -> announcementService.updateAnnouncement(50L, 2L, request));
    }

    @Test
    void shouldRejectDeleteAnnouncementWhenUserIsNotEventCreator() {
        Event event = Event.builder().id(100L).createdBy(1L).build();
        Announcement announcement = Announcement.builder().id(50L).event(event).build();

        when(announcementRepository.findById(50L)).thenReturn(Optional.of(announcement));

        assertThrows(SecurityException.class, () -> announcementService.deleteAnnouncement(50L, 2L));
    }
}
