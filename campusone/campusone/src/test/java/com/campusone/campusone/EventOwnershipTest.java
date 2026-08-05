package com.campusone.campusone;

import com.campusone.campusone.dto.request.EventRequest;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.service.impl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventOwnershipTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @Test
    void shouldRejectUpdateWhenUserIsNotTheCreator() {
        Event event = new Event();
        event.setId(1L);
        event.setCreatedBy(10L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventRequest request = new EventRequest();
        request.setTitle("New title");
        request.setMaxParticipants(20);
        request.setStatus("UPCOMING");

        assertThrows(SecurityException.class, () -> eventService.updateEvent(1L, 5L, request));
    }
}
