package com.campusone.campusone.service.impl;

import java.util.stream.Collectors;

import com.campusone.campusone.dto.request.ChangePasswordRequest;
import com.campusone.campusone.dto.request.UpdateProfileRequest;
import com.campusone.campusone.dto.response.UserResponse;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.UserRepository;
import com.campusone.campusone.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Looks up a user by either email or registration number (case-insensitive),
     * maps every role they hold to its name string, and returns the full profile.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserDetails(String identifier) {
        User user = findUserByIdentifier(identifier);
        return mapToUserResponse(user);
    }

    /**
     * Updates profile fields (fullName, department, year, phoneNumber) for the given user.
     */
    @Override
    @Transactional
    public UserResponse updateProfile(String identifier, UpdateProfileRequest request) {
        User user = findUserByIdentifier(identifier);

        user.setFullName(request.fullName().trim());
        user.setDepartment(request.department().trim());
        user.setYear(request.year());
        user.setPhoneNumber(request.phoneNumber().trim());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    /**
     * Verifies the user's current password and sets a new encoded password.
     */
    @Override
    @Transactional
    public void changePassword(String identifier, ChangePasswordRequest request) {
        User user = findUserByIdentifier(identifier);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be the same as current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User findUserByIdentifier(String identifier) {
        return userRepository
                .findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(identifier, identifier)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found for identifier: " + identifier));
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getRegistrationNumber(),
                user.getFullName(),
                user.getEmail(),
                user.getDepartment(),
                user.getYear(),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getRoleName().name())
                        .collect(Collectors.toSet())
        );
    }
}
