package com.campusone.campusone.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {


    private String title;


    private String description;


    private String category;


    private String venue;


    private LocalDate eventDate;


    private LocalTime startTime;


    private LocalTime endTime;


    private LocalDate registrationDeadline;


    private Integer maxParticipants;


    private String bannerImage;

    private String pdfFile;

    private String pdfFileName;


    private Long createdBy;


    private String status;

}