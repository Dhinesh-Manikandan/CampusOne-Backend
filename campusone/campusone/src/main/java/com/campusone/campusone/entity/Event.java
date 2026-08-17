package com.campusone.campusone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;


    @Column(nullable = false)
    private String category;


    @Column(columnDefinition = "TEXT")
    private String venue;


    @Column(nullable = false)
    private LocalDate eventDate;


    private LocalTime startTime;


    private LocalTime endTime;


    private LocalDate registrationDeadline;


    private Integer maxParticipants;


    @Builder.Default
    private Integer registeredCount = 0;


    @Column(columnDefinition = "TEXT")
    private String bannerImage;


    @Column(nullable = false)
    private Long createdBy;


    private String status;


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();


    private LocalDateTime updatedAt;
}