package com.campusone.campusone.config;

import com.campusone.campusone.entity.Announcement;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.EventRegistration;
import com.campusone.campusone.entity.User;
import com.campusone.campusone.repository.AnnouncementRepository;
import com.campusone.campusone.repository.EventRegistrationRepository;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedInitialData() {
        // 1. Seed Admin & Event Admin Users
        User admin = userRepository.findByEmail("admin@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Campus Admin")
                        .email("admin@campusone.com")
                        .password(passwordEncoder.encode("Admin123!"))
                        .role("ADMIN")
                        .build()));

        User eventAdmin = userRepository.findByEmail("eventadmin@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Sarah Connor (Event Lead)")
                        .email("eventadmin@campusone.com")
                        .password(passwordEncoder.encode("Admin123!"))
                        .role("EVENT_ADMIN")
                        .build()));

        // 2. Seed Student Participants
        User student1 = userRepository.findByEmail("alice.smith@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Alice Smith")
                        .email("alice.smith@campusone.com")
                        .password(passwordEncoder.encode("Student123!"))
                        .role("STUDENT")
                        .build()));

        User student2 = userRepository.findByEmail("bob.jones@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Bob Jones")
                        .email("bob.jones@campusone.com")
                        .password(passwordEncoder.encode("Student123!"))
                        .role("STUDENT")
                        .build()));

        User student3 = userRepository.findByEmail("charlie.brown@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Charlie Brown")
                        .email("charlie.brown@campusone.com")
                        .password(passwordEncoder.encode("Student123!"))
                        .role("STUDENT")
                        .build()));

        User student4 = userRepository.findByEmail("diana.prince@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Diana Prince")
                        .email("diana.prince@campusone.com")
                        .password(passwordEncoder.encode("Student123!"))
                        .role("STUDENT")
                        .build()));

        User student5 = userRepository.findByEmail("evan.wright@campusone.com")
                .orElseGet(() -> userRepository.save(User.builder()
                        .name("Evan Wright")
                        .email("evan.wright@campusone.com")
                        .password(passwordEncoder.encode("Student123!"))
                        .role("STUDENT")
                        .build()));

        // 3. Seed Sample Events for Admin
        List<Event> existingEvents = eventRepository.findByCreatedBy(admin.getId());
        if (existingEvents.isEmpty()) {
            Event event1 = eventRepository.save(Event.builder()
                    .title("Annual Tech Symposium 2026")
                    .description("Campus-wide technical conference featuring DevOps, AI, and Cloud Architecture keynote sessions.")
                    .category("Technical")
                    .venue("Main Campus Auditorium, Hall A")
                    .eventDate(LocalDate.now().plusDays(15))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(16, 0))
                    .registrationDeadline(LocalDate.now().plusDays(10))
                    .maxParticipants(150)
                    .registeredCount(0)
                    .bannerImage("https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=800&q=80")
                    .createdBy(admin.getId())
                    .status("PUBLISHED")
                    .createdAt(LocalDateTime.now())
                    .build());

            Event event2 = eventRepository.save(Event.builder()
                    .title("DevOps & Cloud Native Workshop")
                    .description("Hands-on workshop on Kubernetes, CI/CD pipelines, and microservice deployment strategies.")
                    .category("Workshop")
                    .venue("Science Lab 3B")
                    .eventDate(LocalDate.now().plusDays(20))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(15, 30))
                    .registrationDeadline(LocalDate.now().plusDays(18))
                    .maxParticipants(50)
                    .registeredCount(0)
                    .bannerImage("https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=800&q=80")
                    .createdBy(admin.getId())
                    .status("PUBLISHED")
                    .createdAt(LocalDateTime.now())
                    .build());

            Event event3 = eventRepository.save(Event.builder()
                    .title("Campus Cultural Night & Music Fest")
                    .description("An evening of music, dance, food stalls, and student performances.")
                    .category("Cultural")
                    .venue("Open Air Amphitheatre")
                    .eventDate(LocalDate.now().plusDays(30))
                    .startTime(LocalTime.of(18, 0))
                    .endTime(LocalTime.of(22, 0))
                    .registrationDeadline(LocalDate.now().plusDays(25))
                    .maxParticipants(300)
                    .registeredCount(0)
                    .bannerImage("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80")
                    .createdBy(admin.getId())
                    .status("PUBLISHED")
                    .createdAt(LocalDateTime.now())
                    .build());

            // 4. Seed Registrations for Event 1
            List<User> participantsForEvent1 = Arrays.asList(student1, student2, student3, student4);
            for (User student : participantsForEvent1) {
                if (registrationRepository.findByEventAndUser(event1, student).isEmpty()) {
                    registrationRepository.save(EventRegistration.builder()
                            .event(event1)
                            .user(student)
                            .status("REGISTERED")
                            .registeredAt(LocalDateTime.now().minusDays(1))
                            .build());
                }
            }
            event1.setRegisteredCount(participantsForEvent1.size());
            eventRepository.save(event1);

            // Seed Registrations for Event 2
            List<User> participantsForEvent2 = Arrays.asList(student2, student5);
            for (User student : participantsForEvent2) {
                if (registrationRepository.findByEventAndUser(event2, student).isEmpty()) {
                    registrationRepository.save(EventRegistration.builder()
                            .event(event2)
                            .user(student)
                            .status("REGISTERED")
                            .registeredAt(LocalDateTime.now())
                            .build());
                }
            }
            event2.setRegisteredCount(participantsForEvent2.size());
            eventRepository.save(event2);

            // 5. Seed Announcements
            announcementRepository.save(Announcement.builder()
                    .event(event1)
                    .createdBy(admin)
                    .title("Keynote Speaker Announced!")
                    .content("We are thrilled to welcome Senior Cloud Architect Dr. Marcus Vance as our keynote speaker for Tech Symposium 2026.")
                    .createdAt(LocalDateTime.now().minusHours(5))
                    .updatedAt(LocalDateTime.now().minusHours(5))
                    .build());

            announcementRepository.save(Announcement.builder()
                    .event(event1)
                    .createdBy(admin)
                    .title("Laptop Prerequisite Notice")
                    .content("All registered participants are requested to bring a laptop with Docker Desktop pre-installed.")
                    .createdAt(LocalDateTime.now().minusHours(2))
                    .updatedAt(LocalDateTime.now().minusHours(2))
                    .build());
        }
    }
}
