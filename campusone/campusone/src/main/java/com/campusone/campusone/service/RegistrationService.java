package com.campusone.campusone.service;


import java.util.List;

import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.EventRegistration;


public interface RegistrationService {


    EventRegistration register(
            Long eventId,
            RegistrationRequest request
    );


    List<EventRegistration> getParticipants(
            Long eventId
    );


    void cancelRegistration(
            Long eventId,
            Long userId
    );

}