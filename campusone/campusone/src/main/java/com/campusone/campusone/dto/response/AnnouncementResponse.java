package com.campusone.campusone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private Long eventId;
    private Long createdBy;
    private String title;
    private String content;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
