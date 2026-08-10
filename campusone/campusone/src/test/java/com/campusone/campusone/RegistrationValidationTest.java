package com.campusone.campusone;

import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.EventRegistrationRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationValidationTest {

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRegistrationRepository registrationRepository;

    @Test
    void shouldRejectCreatorSelfRegistration() {
        Event event = Event.builder().id(10L).createdBy(1L).build();
        User creator = User.builder().id(1L).email("creator@campus.com").build();

        RegistrationRequest request = new RegistrationRequest();
        request.setUserId(1L);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> registrationService.register(10L, request));
        assertTrue(exception.getMessage().contains("Event creators cannot register for their own events"));
    }

    @Test
    void shouldRejectRegistrationWhenDeadlineHasPassed() {
        Event event = Event.builder()
                .id(10L)
                .createdBy(1L)
                .registrationDeadline(LocalDate.now().minusDays(1))
                .build();
        User student = User.builder().id(2L).email("student@campus.com").build();

        RegistrationRequest request = new RegistrationRequest();
        request.setUserId(2L);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(userRepository.findById(2L)).thenReturn(Optional.of(student));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> registrationService.register(10L, request));
        assertTrue(exception.getMessage().contains("Registration deadline has passed"));
    }

    @Test
    void shouldRejectGetParticipantsWhenUserIsNotEventCreator() {
        Event event = Event.builder().id(10L).createdBy(1L).build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        assertThrows(SecurityException.class, () -> registrationService.getParticipants(10L, 2L));
    }

    @Test
    void shouldFilterParticipantsByNameOrEmail() {
        Event event = Event.builder().id(10L).createdBy(1L).build();
        User alice = User.builder().id(2L).name("Alice Smith").email("alice@campus.com").build();
        User bob = User.builder().id(3L).name("Bob Jones").email("bob@campus.com").build();

        EventRegistration reg1 = EventRegistration.builder().id(101L).event(event).user(alice).build();
        EventRegistration reg2 = EventRegistration.builder().id(102L).event(event).user(bob).build();

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(registrationRepository.findByEvent(event)).thenReturn(List.of(reg1, reg2));

        List<EventRegistration> resultByName = registrationService.getParticipants(10L, 1L, "alice");
        assertEquals(1, resultByName.size());
        assertEquals("Alice Smith", resultByName.get(0).getUser().getName());

        List<EventRegistration> resultByEmail = registrationService.getParticipants(10L, 1L, "bob@campus");
        assertEquals(1, resultByEmail.size());
        assertEquals("Bob Jones", resultByEmail.get(0).getUser().getName());
    }
}
