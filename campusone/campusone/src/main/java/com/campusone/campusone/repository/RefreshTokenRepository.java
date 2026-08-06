package com.campusone.campusone.repository;

import java.util.Optional;

import com.campusone.campusone.entity.RefreshToken;
import com.campusone.campusone.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	void deleteByUser(User user);
}
