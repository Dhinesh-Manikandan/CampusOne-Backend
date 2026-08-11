package com.campusone.campusone.repository;

import java.util.List;
import java.util.Optional;

import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(String email, String registrationNumber);

	boolean existsByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(String email, String registrationNumber);

	List<User> findDistinctByRoles_RoleName(RoleName roleName);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

}
