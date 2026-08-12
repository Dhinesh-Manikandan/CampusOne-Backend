package com.campusone.campusone.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.campusone.campusone.dto.request.AppAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.AppAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.AppAdminRequestResponse;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.entity.AppAdminRequest;
import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.AppAdminRequestStatus;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.AppAdminRequestRepository;
import com.campusone.campusone.repository.RoleRepository;
import com.campusone.campusone.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AppAdminRequestServiceImpl implements AppAdminRequestService {

	private final AppAdminRequestRepository appAdminRequestRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	public AppAdminRequestServiceImpl(AppAdminRequestRepository appAdminRequestRepository, UserRepository userRepository,
			RoleRepository roleRepository) {
		this.appAdminRequestRepository = appAdminRequestRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public AppAdminRequestResponse createRequest(String username, AppAdminRequestCreateRequest request) {
		User user = findUser(username);
		if (hasRole(user, RoleName.ROLE_APP_ADMIN)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already an app admin");
		}
		if (appAdminRequestRepository.existsByRequestedByAndStatus(user, AppAdminRequestStatus.PENDING)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "App admin request already pending");
		}
		AppAdminRequest appAdminRequest = new AppAdminRequest();
		appAdminRequest.setRequestedBy(user);
		appAdminRequest.setRequestReason(request.requestReason());
		appAdminRequest.setStatus(AppAdminRequestStatus.PENDING);
		return toResponse(appAdminRequestRepository.save(appAdminRequest));
	}

	@Override
	public List<AppAdminRequestResponse> listRequests(AppAdminRequestStatus status) {
		List<AppAdminRequest> requests = status == null ? appAdminRequestRepository.findAllByOrderByRequestedAtDesc()
				: appAdminRequestRepository.findByStatusOrderByRequestedAtDesc(status);
		return requests.stream().map(this::toResponse).toList();
	}

	@Override
	public List<AppAdminRequestResponse> getUserRequests(String username) {
		User user = findUser(username);
		return appAdminRequestRepository.findByRequestedByOrderByRequestedAtDesc(user)
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Override
	public AppAdminRequestResponse approveRequest(Long requestId, String reviewerUsername) {
		AppAdminRequest request = findRequest(requestId);
		if (request.getStatus() != AppAdminRequestStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already reviewed");
		}
		User reviewer = findUser(reviewerUsername);
		User user = request.getRequestedBy();
		if (hasRole(user, RoleName.ROLE_APP_ADMIN)) {
			request.setStatus(AppAdminRequestStatus.APPROVED);
			request.setReviewedBy(reviewer);
			request.setReviewedAt(LocalDateTime.now());
			request.setRemarks("Approved");
			return toResponse(appAdminRequestRepository.save(request));
		}
		user.getRoles().add(ensureRole(RoleName.ROLE_APP_ADMIN));
		userRepository.save(user);
		request.setStatus(AppAdminRequestStatus.APPROVED);
		request.setReviewedBy(reviewer);
		request.setReviewedAt(LocalDateTime.now());
		if (request.getRemarks() == null || request.getRemarks().isBlank()) {
			request.setRemarks("Approved");
		}
		return toResponse(appAdminRequestRepository.save(request));
	}

	@Override
	public AppAdminRequestResponse rejectRequest(Long requestId, String reviewerUsername,
			AppAdminRequestReviewRequest reviewRequest) {
		AppAdminRequest request = findRequest(requestId);
		if (request.getStatus() != AppAdminRequestStatus.PENDING) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Request already reviewed");
		}
		User reviewer = findUser(reviewerUsername);
		request.setStatus(AppAdminRequestStatus.REJECTED);
		request.setReviewedBy(reviewer);
		request.setReviewedAt(LocalDateTime.now());
		request.setRemarks(reviewRequest.remarks());
		return toResponse(appAdminRequestRepository.save(request));
	}

	private AppAdminRequest findRequest(Long requestId) {
		return appAdminRequestRepository.findById(requestId)
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

	private AppAdminRequestResponse toResponse(AppAdminRequest request) {
		return new AppAdminRequestResponse(request.getId(), request.getRequestReason(), request.getStatus().name(),
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
