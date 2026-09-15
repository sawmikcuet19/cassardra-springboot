package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.Event;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends CassandraRepository<Event, UUID> {

    List<Event> findByEventType(String eventType);

    @Query("SELECT * FROM events WHERE event_type = ?0 AND event_time >= ?1 AND event_time <= ?2 ALLOW FILTERING")
    List<Event> findByEventTypeAndTimeRange(String eventType, Instant start, Instant end);

    @Query("SELECT * FROM events WHERE event_type = ?0 ORDER BY event_time DESC LIMIT ?1")
    List<Event> findLatestByType(String eventType, int limit);

    @Query("SELECT COUNT(*) FROM events WHERE event_type = ?0")
    long countByEventType(String eventType);

    @Query("SELECT MIN(event_time) FROM events WHERE event_type = ?0")
    Instant minEventTime(String eventType);

    @Query("SELECT MAX(event_time) FROM events WHERE event_type = ?0")
    Instant maxEventTime(String eventType);
}
