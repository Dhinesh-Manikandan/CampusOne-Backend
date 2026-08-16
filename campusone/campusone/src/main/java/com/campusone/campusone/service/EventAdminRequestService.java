package com.campusone.campusone.service;

import java.util.List;

import com.campusone.campusone.dto.request.EventAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.EventAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.EventAdminRequestResponse;
import com.campusone.campusone.entity.enums.EventAdminRequestStatus;

public interface EventAdminRequestService {

	EventAdminRequestResponse createRequest(String username, EventAdminRequestCreateRequest request);

	List<EventAdminRequestResponse> listRequests(EventAdminRequestStatus status);

	List<EventAdminRequestResponse> getUserRequests(String username);

	EventAdminRequestResponse approveRequest(Long requestId, String reviewerUsername);

	EventAdminRequestResponse rejectRequest(Long requestId, String reviewerUsername, EventAdminRequestReviewRequest request);
}
