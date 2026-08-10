package com.campusone.campusone.service.impl;


import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.EventRegistrationRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.RegistrationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {


    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventRegistrationRepository registrationRepository;



    @Override
    public EventRegistration register(
            Long eventId,
            RegistrationRequest request
    ) {


        Event event = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Event not found"
                        )
                );


        User user = userRepository.findById(
                request.getUserId()
        )
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        // Creator self-registration restriction
        if (event.getCreatedBy() != null && event.getCreatedBy().equals(user.getId())) {
            throw new RuntimeException("Event creators cannot register for their own events");
        }

        // Registration deadline check
        if (event.getRegistrationDeadline() != null && LocalDate.now().isAfter(event.getRegistrationDeadline())) {
            throw new RuntimeException("Registration deadline has passed");
        }



        if(registrationRepository
                .findByEventAndUser(event,user)
                .isPresent()) {

            throw new RuntimeException(
                    "Already registered"
            );
        }



        // Check participant limit
        if(event.getMaxParticipants() != null
                &&
           event.getRegisteredCount() >= event.getMaxParticipants()) {


            throw new RuntimeException(
                    "Event is full"
            );
        }



        EventRegistration registration =
                EventRegistration.builder()
                        .event(event)
                        .user(user)
                        .status("REGISTERED")
                        .build();



        // Increase registered count

        if(event.getRegisteredCount() == null) {

            event.setRegisteredCount(1);

        } else {

            event.setRegisteredCount(
                    event.getRegisteredCount() + 1
            );
        }



        eventRepository.save(event);



        return registrationRepository.save(
                registration
        );

    }





    @Override
    public List<EventRegistration> getParticipants(
            Long eventId
    ) {
        return getParticipants(eventId, null, null);
    }

    @Override
    public List<EventRegistration> getParticipants(
            Long eventId,
            Long currentUserId
    ) {
        return getParticipants(eventId, currentUserId, null);
    }

    @Override
    public List<EventRegistration> getParticipants(
            Long eventId,
            Long currentUserId,
            String search
    ) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Event not found"
                        )
                );

        if (currentUserId != null) {
            User caller = userRepository.findById(currentUserId).orElse(null);
            boolean isAdmin = caller != null && "ADMIN".equalsIgnoreCase(caller.getRole());
            if (!isAdmin && !event.getCreatedBy().equals(currentUserId)) {
                throw new SecurityException("You can only view participant lists for events you created");
            }
        }

        List<EventRegistration> registrations = registrationRepository.findByEvent(event);

        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            registrations = registrations.stream()
                    .filter(r -> (r.getUser().getName() != null && r.getUser().getName().toLowerCase().contains(keyword))
                            || (r.getUser().getEmail() != null && r.getUser().getEmail().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }

        return registrations;
    }





    @Override
    public void cancelRegistration(
            Long eventId,
            Long userId
    ) {


        Event event = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Event not found"
                        )
                );


        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );



        registrationRepository
                .deleteByEventAndUser(
                        event,
                        user
                );



        if(event.getRegisteredCount() != null
                &&
           event.getRegisteredCount() > 0) {


            event.setRegisteredCount(
                    event.getRegisteredCount() - 1
            );


            eventRepository.save(event);
        }

    }

}