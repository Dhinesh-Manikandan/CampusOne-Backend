package com.campusone.campusone.repository;

import java.util.List;

import com.campusone.campusone.entity.EventAdminRequest;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.EventAdminRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventAdminRequestRepository extends JpaRepository<EventAdminRequest, Long> {

    boolean existsByRequestedByAndStatus(
            User requestedBy,
            EventAdminRequestStatus status);

    List<EventAdminRequest> findByRequestedByOrderByRequestedAtDesc(
            User requestedBy);

    List<EventAdminRequest> findAllByOrderByRequestedAtDesc();

    List<EventAdminRequest> findByStatusOrderByRequestedAtDesc(
            EventAdminRequestStatus status);
}