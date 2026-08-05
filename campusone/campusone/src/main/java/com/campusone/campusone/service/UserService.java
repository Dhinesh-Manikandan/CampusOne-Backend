package com.campusone.campusone.service;

import com.campusone.campusone.dto.request.LoginRequest;
import com.campusone.campusone.dto.request.RegisterRequest;
import com.campusone.campusone.entity.User;

public interface UserService {

    User registerUser(RegisterRequest request);

    String loginUser(LoginRequest request);
}