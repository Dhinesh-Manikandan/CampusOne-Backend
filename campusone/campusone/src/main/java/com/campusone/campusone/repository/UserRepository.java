package com.campusone.campusone.repository;

import java.util.Optional;

import com.campusone.campusone.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(String email, String registrationNumber);

	boolean existsByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(String email, String registrationNumber);
}
