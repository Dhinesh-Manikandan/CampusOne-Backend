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
    public void seedAdminUser() {

        if (userRepository.existsByEmail("admin@student.annauniv.edu")) {
            return;
        }

        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_APP_ADMIN  )
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

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
}
