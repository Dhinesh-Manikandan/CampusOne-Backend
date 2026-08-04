package com.campusone.campusone.service;

import java.util.List;

import com.campusone.campusone.dto.request.AppAdminRequestCreateRequest;
import com.campusone.campusone.dto.request.AppAdminRequestReviewRequest;
import com.campusone.campusone.dto.response.AppAdminRequestResponse;
import com.campusone.campusone.entity.enums.AppAdminRequestStatus;

public interface AppAdminRequestService {

	AppAdminRequestResponse createRequest(String username, AppAdminRequestCreateRequest request);

	List<AppAdminRequestResponse> listRequests(AppAdminRequestStatus status);

	AppAdminRequestResponse approveRequest(Long requestId, String reviewerUsername);

	AppAdminRequestResponse rejectRequest(Long requestId, String reviewerUsername, AppAdminRequestReviewRequest request);
}
