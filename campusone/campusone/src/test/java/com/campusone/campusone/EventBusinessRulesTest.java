package com.campusone.campusone;

import com.campusone.campusone.dto.request.EventRequest;
import com.campusone.campusone.service.impl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EventBusinessRulesTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void shouldRejectEventWhenRegistrationDeadlineIsAfterEventDate() {
        EventRequest request = new EventRequest();
        request.setTitle("Test Event");
        request.setDescription("desc");
        request.setCategory("Tech");
        request.setVenue("Room 1");
        request.setEventDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setRegistrationDeadline(LocalDate.now().plusDays(2));
        request.setMaxParticipants(20);
        request.setCreatedBy(1L);
        request.setStatus("UPCOMING");

        assertThrows(IllegalArgumentException.class, () -> eventService.validateEventRequest(request));
    }

    @Test
    void shouldRejectEventWhenEndTimeIsBeforeStartTime() {
        EventRequest request = new EventRequest();
        request.setTitle("Test Event");
        request.setDescription("desc");
        request.setCategory("Tech");
        request.setVenue("Room 1");
        request.setEventDate(LocalDate.now().plusDays(1));
        request.setStartTime(LocalTime.of(14, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setRegistrationDeadline(LocalDate.now().plusDays(1));
        request.setMaxParticipants(20);
        request.setCreatedBy(1L);
        request.setStatus("UPCOMING");

        assertThrows(IllegalArgumentException.class, () -> eventService.validateEventRequest(request));
    }
}
