package com.campusone.campusone.config;

import java.util.Set;

import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.RoleRepository;
import com.campusone.campusone.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthDataSeeder implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final BootstrapAdminProperties bootstrapAdminProperties;

	public AuthDataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
			BootstrapAdminProperties bootstrapAdminProperties) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.bootstrapAdminProperties = bootstrapAdminProperties;
	}

	@Override
	@Transactional
	public void run(String... args) {
		ensureRole(RoleName.ROLE_STUDENT);
		ensureRole(RoleName.ROLE_EVENT_ADMIN);
		ensureRole(RoleName.ROLE_APP_ADMIN);
		if (bootstrapAdminProperties.enabled()) {
			seedAppAdmin();
		}
	}

	private void seedAppAdmin() {
		boolean exists = userRepository.existsByEmailIgnoreCaseOrRegistrationNumberIgnoreCase(
				bootstrapAdminProperties.email(), bootstrapAdminProperties.registrationNumber());
		if (exists) {
			return;
		}
		User admin = new User();
		admin.setRegistrationNumber(bootstrapAdminProperties.registrationNumber());
		admin.setFullName(bootstrapAdminProperties.fullName());
		admin.setEmail(bootstrapAdminProperties.email());
		admin.setPassword(passwordEncoder.encode(bootstrapAdminProperties.password()));
		admin.setDepartment(bootstrapAdminProperties.department());
		admin.setYear(bootstrapAdminProperties.year());
		admin.setPhoneNumber(bootstrapAdminProperties.phoneNumber());
		admin.setRoles(Set.of(ensureRole(RoleName.ROLE_APP_ADMIN)));
		admin.setEnabled(true);
		userRepository.save(admin);
	}

	private Role ensureRole(RoleName roleName) {
		return roleRepository.findByRoleName(roleName).orElseGet(() -> {
			Role role = new Role();
			role.setRoleName(roleName);
			return roleRepository.save(role);
		});
	}
}
