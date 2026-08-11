package com.campusone.campusone.repository;

import java.util.Optional;

import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.enums.RoleName;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByRoleName(RoleName roleName);
}
