package com.campusone.campusone.repository;

import java.util.List;
import java.util.Optional;

import com.campusone.campusone.entity.AppAdminRequest;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.AppAdminRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppAdminRequestRepository extends JpaRepository<AppAdminRequest, Long> {

	List<AppAdminRequest> findAllByOrderByRequestedAtDesc();

	List<AppAdminRequest> findByStatusOrderByRequestedAtDesc(AppAdminRequestStatus status);

	Optional<AppAdminRequest> findByRequestedByAndStatus(User requestedBy, AppAdminRequestStatus status);

	List<AppAdminRequest> findByRequestedByOrderByRequestedAtDesc(User requestedBy);

	boolean existsByRequestedByAndStatus(User requestedBy, AppAdminRequestStatus status);
}
