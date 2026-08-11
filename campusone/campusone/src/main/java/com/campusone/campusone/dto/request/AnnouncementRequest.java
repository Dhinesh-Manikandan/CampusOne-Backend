package com.campusone.campusone.dto.request;

import lombok.Data;

@Data
public class AnnouncementRequest {
    private String title;
    private String content;
    private Long eventId;
    private Long createdBy;
}
