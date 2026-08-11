package com.campusone.campusone.service;

import com.campusone.campusone.dto.request.EventRequest;
import com.campusone.campusone.dto.response.DashboardSummaryResponse;
import com.campusone.campusone.dto.response.EventResponse;

import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request);

    List<EventResponse> getAllEvents();

    EventResponse getEventById(Long id);

    EventResponse updateEvent(Long id, Long currentUserId, EventRequest request);

    void deleteEvent(Long id, Long currentUserId);

    List<EventResponse> getEventsByAdmin(Long createdBy);

    DashboardSummaryResponse getDashboardSummary(Long createdBy);

    long getParticipantCount(Long eventId);
}