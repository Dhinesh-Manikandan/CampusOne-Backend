package com.campusone.campusone.config;

import java.util.Set;

import com.campusone.campusone.entity.Role;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.entity.enums.RoleName;
import com.campusone.campusone.repository.RoleRepository;
import com.campusone.campusone.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaultUsers() {
        try {
            // Seed App Admin User
            if (!userRepository.existsByEmail("admin@student.annauniv.edu")) {
                Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_APP_ADMIN)
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setRoleName(RoleName.ROLE_APP_ADMIN);
                            return roleRepository.save(role);
                        });

                User admin = User.builder()
                        .registrationNumber("ADMIN001")
                        .fullName("Campus Admin")
                        .email("admin@student.annauniv.edu")
                        .password(passwordEncoder.encode("Admin123!"))
                        .department("ADMIN")
                        .year(1)
                        .phoneNumber("9999999999")
                        .enabled(true)
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(admin);
            }

            // Helper to get or create student role
            Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setRoleName(RoleName.ROLE_STUDENT);
                        return roleRepository.save(r);
                    });

            // Seed Default Student User 1: student@student.annauniv.edu
            if (!userRepository.existsByEmail("student@student.annauniv.edu")) {
                User student = User.builder()
                        .registrationNumber("2026101001")
                        .fullName("Alex Student")
                        .email("student@student.annauniv.edu")
                        .password(passwordEncoder.encode("Student123!"))
                        .department("CSE")
                        .year(3)
                        .phoneNumber("9876543210")
                        .enabled(true)
                        .roles(Set.of(studentRole))
                        .build();

                userRepository.save(student);
            }

            // Seed Default Student User 2: jane@student.annauniv.edu
            if (!userRepository.existsByEmail("jane@student.annauniv.edu")) {
                User jane = User.builder()
                        .registrationNumber("2026101002")
                        .fullName("Jane Doe")
                        .email("jane@student.annauniv.edu")
                        .password(passwordEncoder.encode("Student123!"))
                        .department("CSE")
                        .year(2)
                        .phoneNumber("9876543211")
                        .enabled(true)
                        .roles(Set.of(studentRole))
                        .build();

                userRepository.save(jane);
            }
        } catch (Exception e) {
            // Ignore seeding exception if DB tables are initialized asynchronously
        }
    }
}
