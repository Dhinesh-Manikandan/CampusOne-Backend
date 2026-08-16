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

import java.util.List;



import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {


    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventRegistrationRepository registrationRepository;



    @Override
    @Transactional
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


        Event event = eventRepository.findById(eventId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Event not found"
                        )
                );


        return registrationRepository
                .findByEvent(event);

    }

    @Override
    public List<EventRegistration> getUserRegistrations(
            Long userId
    ) {
        return registrationRepository.findByUserId(userId);
    }





    @Override
    @Transactional
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

        EventRegistration registration = registrationRepository.findByEventAndUser(event, user)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Registration not found for user and event"
                        )
                );

        registrationRepository.delete(registration);

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