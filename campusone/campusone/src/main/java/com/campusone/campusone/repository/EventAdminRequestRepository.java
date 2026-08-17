package com.campusone.campusone.repository;

import java.util.List;
import java.util.Optional;

import com.campusone.campusone.entity.EventAdminRequest;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.EventAdminRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventAdminRequestRepository extends JpaRepository<EventAdminRequest, Long> {

	List<EventAdminRequest> findAllByOrderByRequestedAtDesc();

	List<EventAdminRequest> findByStatusOrderByRequestedAtDesc(EventAdminRequestStatus status);

	Optional<EventAdminRequest> findByRequestedByAndStatus(User requestedBy, EventAdminRequestStatus status);

	List<EventAdminRequest> findByRequestedByOrderByRequestedAtDesc(User requestedBy);

	boolean existsByRequestedByAndStatus(User requestedBy, EventAdminRequestStatus status);
}
