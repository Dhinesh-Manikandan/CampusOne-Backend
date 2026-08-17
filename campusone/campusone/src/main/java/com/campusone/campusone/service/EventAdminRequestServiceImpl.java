package com.campusone.campusone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.campusone.campusone.dto.request.EventAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.EventAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.EventAdminRequestResponse;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.entity.EventAdminRequest;
import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.EventAdminRequestStatus;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.EventAdminRequestRepository;
import com.campusone.campusone.repository.RoleRepository;
import com.campusone.campusone.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class EventAdminRequestServiceImpl implements EventAdminRequestService {

	private final EventAdminRequestRepository eventAdminRequestRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public EventAdminRequestServiceImpl(EventAdminRequestRepository eventAdminRequestRepository, UserRepository userRepository,
			RoleRepository roleRepository) {
		this.eventAdminRequestRepository = eventAdminRequestRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public EventAdminRequestResponse createRequest(String username, EventAdminRequestCreateRequest request) {
		User user = findUser(username);
		if (hasRole(user, RoleName.ROLE_EVENT_ADMIN) || hasRole(user, RoleName.ROLE_APP_ADMIN)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already an event admin");
		}
		if (eventAdminRequestRepository.existsByRequestedByAndStatus(user, EventAdminRequestStatus.PENDING)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Event admin request already pending");
		}
		EventAdminRequest eventAdminRequest = new EventAdminRequest();
		eventAdminRequest.setRequestedBy(user);
		eventAdminRequest.setRequestReason(request.requestReason());
		eventAdminRequest.setStatus(EventAdminRequestStatus.PENDING);
		return toResponse(eventAdminRequestRepository.save(eventAdminRequest));
	}

	@Override
	public List<EventAdminRequestResponse> listRequests(EventAdminRequestStatus status) {
		List<EventAdminRequest> requests = status == null ? eventAdminRequestRepository.findAllByOrderByRequestedAtDesc()
				: eventAdminRequestRepository.findByStatusOrderByRequestedAtDesc(status);
		return requests.stream().map(this::toResponse).toList();
	}

	@Override
	public List<EventAdminRequestResponse> getUserRequests(String username) {
		User user = findUser(username);
		return eventAdminRequestRepository.findByRequestedByOrderByRequestedAtDesc(user)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public EventAdminRequestResponse approveRequest(Long requestId, String reviewerUsername) {
		EventAdminRequest request = findRequest(requestId);
		if (request.getStatus() != EventAdminRequestStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already reviewed");
		}
		User reviewer = findUser(reviewerUsername);
		User user = request.getRequestedBy();
		if (!hasRole(user, RoleName.ROLE_EVENT_ADMIN)) {
			user.getRoles().add(ensureRole(RoleName.ROLE_EVENT_ADMIN));
			userRepository.save(user);
		}
		request.setStatus(EventAdminRequestStatus.APPROVED);
		request.setReviewedBy(reviewer);
		request.setReviewedAt(LocalDateTime.now());
		if (request.getRemarks() == null || request.getRemarks().isBlank()) {
			request.setRemarks("Approved");
		}
		return toResponse(eventAdminRequestRepository.save(request));
	}

	@Override
	public EventAdminRequestResponse rejectRequest(Long requestId, String reviewerUsername,
			EventAdminRequestReviewRequest reviewRequest) {
		EventAdminRequest request = findRequest(requestId);
		if (request.getStatus() != EventAdminRequestStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already reviewed");
		}
		User reviewer = findUser(reviewerUsername);
		request.setStatus(EventAdminRequestStatus.REJECTED);
		request.setReviewedBy(reviewer);
		request.setReviewedAt(LocalDateTime.now());
		request.setRemarks(reviewRequest.remarks());
		return toResponse(eventAdminRequestRepository.save(request));
	}

	private EventAdminRequest findRequest(Long requestId) {
		return eventAdminRequestRepository.findById(requestId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));
	}

	private User findUser(String username) {
		return userRepository.findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(username, username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
	}

	private boolean hasRole(User user, RoleName roleName) {
		return user.getRoles().stream().anyMatch(role -> role.getRoleName() == roleName);
	}

	private Role ensureRole(RoleName roleName) {
		return roleRepository.findByRoleName(roleName).orElseGet(() -> {
			Role role = new Role();
			role.setRoleName(roleName);
			return roleRepository.save(role);
		});
	}

	private EventAdminRequestResponse toResponse(EventAdminRequest request) {
		return new EventAdminRequestResponse(request.getId(), request.getRequestReason(), request.getStatus().name(),
				toUserResponse(request.getRequestedBy()), request.getReviewedBy() != null
						? toUserResponse(request.getReviewedBy()) : null,
				toInstant(request.getRequestedAt()), toInstant(request.getReviewedAt()), request.getRemarks());
	}

	private UserResponse toUserResponse(User user) {
		Set<String> roles = new LinkedHashSet<>();
		user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
		return new UserResponse(user.getId(), user.getRegistrationNumber(), user.getFullName(), user.getEmail(),
				user.getDepartment(), user.getYear(), user.getPhoneNumber(), user.isEnabled(), roles);
	}

	private Instant toInstant(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
	}
}
