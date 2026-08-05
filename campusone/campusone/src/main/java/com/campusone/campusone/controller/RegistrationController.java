package com.campusone.campusone.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campusone.campusone.dto.request.RegistrationRequest;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.service.RegistrationService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class RegistrationController {



    private final RegistrationService registrationService;



    // Register user for an event
    @PostMapping("/{eventId}/register")
    public ResponseEntity<EventRegistration> register(
            @PathVariable Long eventId,
            @RequestBody RegistrationRequest request
    ){

        return ResponseEntity.ok(
                registrationService.register(
                        eventId,
                        request
                )
        );

    }




    // Get all participants of an event
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<EventRegistration>> getParticipants(
            @PathVariable Long eventId
    ){

        return ResponseEntity.ok(
                registrationService.getParticipants(eventId)
        );

    }




    // Cancel registration
    @DeleteMapping("/{eventId}/register/{userId}")
    public ResponseEntity<String> cancelRegistration(
            @PathVariable Long eventId,
            @PathVariable Long userId
    ){

        registrationService.cancelRegistration(
                eventId,
                userId
        );


        return ResponseEntity.ok(
                "Registration cancelled successfully"
        );

    }

}