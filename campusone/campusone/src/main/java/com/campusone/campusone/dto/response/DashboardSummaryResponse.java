package com.campusone.campusone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalEvents;
    private long upcomingEvents;
    private long totalRegistrations;
    private long completedEvents;
    private long cancelledEvents;
}
