package com.campusone.campusone.service;

import com.campusone.campusone.dto.request.ChangePasswordRequest;
import com.campusone.campusone.dto.request.UpdateProfileRequest;
import com.campusone.campusone.dto.response.UserResponse;

public interface UserService {

    /**
     * Fetch full user details (including all roles) by email or registration number.
     *
     * @param identifier email address or registration number of the user
     * @return {@link UserResponse} containing all user fields and their assigned roles
     */
    UserResponse getUserDetails(String identifier);

    /**
     * Update editable profile fields (fullName, department, year, phoneNumber).
     *
     * @param identifier email address or registration number of the user
     * @param request profile fields to update
     * @return updated {@link UserResponse}
     */
    UserResponse updateProfile(String identifier, UpdateProfileRequest request);

    /**
     * Change current user password after verifying current password.
     *
     * @param identifier email address or registration number of the user
     * @param request current password and new password
     */
    void changePassword(String identifier, ChangePasswordRequest request);
}
