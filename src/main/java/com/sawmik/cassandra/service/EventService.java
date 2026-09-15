package com.sawmik.cassandra.service;

import com.sawmik.cassandra.entity.Event;
import com.sawmik.cassandra.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event save(Event event) {
        if (event.getId() == null) event.setId(UUID.randomUUID());
        if (event.getEventTime() == null) event.setEventTime(Instant.now());
        if (event.getVersion() == null) event.setVersion(1L);
        return eventRepository.save(event);
    }

    public Optional<Event> findById(UUID id) {
        return eventRepository.findById(id);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public void deleteById(UUID id) {
        eventRepository.deleteById(id);
    }

    public List<Event> findByEventType(String eventType) {
        return eventRepository.findByEventType(eventType);
    }

    public List<Event> findByEventTypeAndTimeRange(String eventType, Instant start, Instant end) {
        return eventRepository.findByEventTypeAndTimeRange(eventType, start, end);
    }

    public List<Event> findLatestByType(String eventType, int limit) {
        return eventRepository.findLatestByType(eventType, limit);
    }

    public List<Event> findByEventTypePaged(String eventType, int limit) {
        return eventRepository.findLatestByType(eventType, limit);
    }

    public long countByEventType(String eventType) {
        return eventRepository.countByEventType(eventType);
    }
}
