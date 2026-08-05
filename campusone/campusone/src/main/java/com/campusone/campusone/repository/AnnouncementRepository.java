package com.campusone.campusone.repository;

import com.campusone.campusone.entity.Announcement;
import com.campusone.campusone.entity.Event;
import com.campusone.campusone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByEvent(Event event);
    List<Announcement> findByCreatedBy(User createdBy);
}
