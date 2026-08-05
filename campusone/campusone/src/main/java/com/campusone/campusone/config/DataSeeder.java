package com.campusone.campusone.config;

import com.campusone.campusone.entity.User;
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
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdminUser() {
        if (userRepository.existsByEmail("admin@campusone.com")) {
            return;
        }

        User admin = User.builder()
                .name("Campus Admin")
                .email("admin@campusone.com")
                .password(passwordEncoder.encode("Admin123!"))
                .role("ADMIN")
                .build();

        userRepository.save(admin);
    }
}
