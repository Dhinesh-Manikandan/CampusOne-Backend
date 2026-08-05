package com.campusone.campusone.service.impl;

import com.campusone.campusone.dto.request.EventRequest;
import com.campusone.campusone.dto.response.DashboardSummaryResponse;
import com.campusone.campusone.dto.response.EventResponse;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.repository.EventRepository;
import com.campusone.campusone.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventResponse createEvent(EventRequest request) {
        validateEventRequest(request);

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .venue(request.getVenue())
                .eventDate(request.getEventDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .registrationDeadline(request.getRegistrationDeadline())
                .maxParticipants(request.getMaxParticipants())
                .bannerImage(request.getBannerImage())
                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : 0L)
                .status(request.getStatus())
                .registeredCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return mapToResponse(event);
    }

    @Override
    public EventResponse updateEvent(Long id, Long currentUserId, EventRequest request) {
        validateEventRequest(request);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreatedBy().equals(currentUserId)) {
            throw new SecurityException("You can only update your own events");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setRegistrationDeadline(request.getRegistrationDeadline());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setBannerImage(request.getBannerImage());
        event.setStatus(request.getStatus());
        event.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(eventRepository.save(event));
    }

    @Override
    public void deleteEvent(Long id, Long currentUserId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getCreatedBy().equals(currentUserId)) {
            throw new SecurityException("You can only delete your own events");
        }

        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponse> getEventsByAdmin(Long createdBy) {
        return eventRepository.findByCreatedBy(createdBy)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary(Long createdBy) {
        List<Event> events = eventRepository.findByCreatedBy(createdBy);
        LocalDate today = LocalDate.now();

        long totalEvents = events.size();
        long upcomingEvents = events.stream()
                .filter(event -> event.getEventDate() != null && !event.getEventDate().isBefore(today))
                .count();
        long totalRegistrations = events.stream()
                .mapToLong(event -> event.getRegisteredCount() == null ? 0 : event.getRegisteredCount())
                .sum();
        long completedEvents = events.stream()
                .filter(event -> "COMPLETED".equalsIgnoreCase(event.getStatus()))
                .count();
        long cancelledEvents = events.stream()
                .filter(event -> "CANCELLED".equalsIgnoreCase(event.getStatus()))
                .count();

        return DashboardSummaryResponse.builder()
                .totalEvents(totalEvents)
                .upcomingEvents(upcomingEvents)
                .totalRegistrations(totalRegistrations)
                .completedEvents(completedEvents)
                .cancelledEvents(cancelledEvents)
                .build();
    }

    @Override
    public long getParticipantCount(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return event.getRegisteredCount() == null ? 0 : event.getRegisteredCount();
    }

    public void validateEventRequest(EventRequest request) {
        if (request.getEventDate() != null && request.getRegistrationDeadline() != null
                && request.getRegistrationDeadline().isAfter(request.getEventDate())) {
            throw new IllegalArgumentException("Registration deadline must be on or before the event date");
        }

        if (request.getStartTime() != null && request.getEndTime() != null
                && request.getEndTime().isBefore(request.getStartTime())) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }

        if (request.getMaxParticipants() != null && request.getMaxParticipants() <= 0) {
            throw new IllegalArgumentException("Maximum participants must be greater than zero");
        }
    }

    private EventResponse mapToResponse(Event event) {
        EventResponse response = new EventResponse();
        response.setId(event.getId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setCategory(event.getCategory());
        response.setVenue(event.getVenue());
        response.setEventDate(event.getEventDate());
        response.setStartTime(event.getStartTime());
        response.setEndTime(event.getEndTime());
        response.setRegistrationDeadline(event.getRegistrationDeadline());
        response.setMaxParticipants(event.getMaxParticipants());
        response.setRegisteredCount(event.getRegisteredCount());
        response.setBannerImage(event.getBannerImage());
        response.setCreatedBy(event.getCreatedBy());
        response.setStatus(event.getStatus());
        response.setCreatedAt(event.getCreatedAt());
        response.setUpdatedAt(event.getUpdatedAt());
        return response;
    }
}
