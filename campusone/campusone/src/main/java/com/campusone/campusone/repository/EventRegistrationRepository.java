package com.campusone.campusone.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.entity.User;



public interface EventRegistrationRepository
        extends JpaRepository<EventRegistration, Long> {


    Optional<EventRegistration> findByEventAndUser(
            Event event,
            User user
    );


    List<EventRegistration> findByEvent(
            Event event
    );


    void deleteByEventAndUser(
            Event event,
            User user
    );

}