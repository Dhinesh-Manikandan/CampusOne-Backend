package com.campusone.campusone.repository;

import com.campusone.campusone.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatedBy(Long createdBy);

    List<Event> findByCategory(String category);

    List<Event> findByStatus(String status);

}