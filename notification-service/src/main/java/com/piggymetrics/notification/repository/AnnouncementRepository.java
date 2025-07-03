package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.Announcement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, String> {
    List<Announcement> findByCourseIdOrderByTimestampDesc(String courseId);
}