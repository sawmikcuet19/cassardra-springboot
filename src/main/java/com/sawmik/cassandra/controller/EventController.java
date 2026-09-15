package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.entity.Event;
import com.sawmik.cassandra.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(event));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<Event>> getByType(@PathVariable String eventType) {
        return ResponseEntity.ok(eventService.findByEventType(eventType));
    }

    @GetMapping("/type/{eventType}/range")
    public ResponseEntity<List<Event>> getByTypeAndRange(
            @PathVariable String eventType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(eventService.findByEventTypeAndTimeRange(eventType, start, end));
    }

    @GetMapping("/type/{eventType}/latest")
    public ResponseEntity<List<Event>> getLatestByType(
            @PathVariable String eventType,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(eventService.findLatestByType(eventType, limit));
    }

    @GetMapping("/type/{eventType}/page")
    public ResponseEntity<List<Event>> getByTypePaged(
            @PathVariable String eventType,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.findByEventTypePaged(eventType, size));
    }

    @GetMapping("/count/type/{eventType}")
    public ResponseEntity<Map<String, Long>> countByType(@PathVariable String eventType) {
        return ResponseEntity.ok(Map.of("count", eventService.countByEventType(eventType)));
    }
}
