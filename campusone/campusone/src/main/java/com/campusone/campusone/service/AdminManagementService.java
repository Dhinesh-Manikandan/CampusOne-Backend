package com.campusone.campusone.service;

import java.util.List;

import com.campusone.campusone.dto.response.MessageResponse;
import com.campusone.campusone.dto.response.UserResponse;

public interface AdminManagementService {

	List<UserResponse> listAppAdmins();

	MessageResponse removeAppAdmin(Long adminId, String currentUsername);

	List<UserResponse> listEventAdmins();

	MessageResponse removeEventAdmin(Long adminId);
}
