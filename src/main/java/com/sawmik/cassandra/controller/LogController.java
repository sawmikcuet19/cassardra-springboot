package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.entity.LogEntry;
import com.sawmik.cassandra.service.LogService;
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
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<LogEntry> create(@RequestBody LogEntry logEntry) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logService.save(logEntry));
    }

    @GetMapping
    public ResponseEntity<List<LogEntry>> getAll() {
        return ResponseEntity.ok(logService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntry> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(logService.findById(id)
                .orElseThrow(() -> new RuntimeException("Log entry not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        logService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<LogEntry>> bulkCreate(@RequestBody List<LogEntry> logEntries) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logService.bulkSave(logEntries));
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<List<LogEntry>> getByLevel(@PathVariable String level) {
        return ResponseEntity.ok(logService.findByLevel(level));
    }

    @GetMapping("/service/{service}")
    public ResponseEntity<List<LogEntry>> getByService(@PathVariable String service) {
        return ResponseEntity.ok(logService.findByService(service));
    }

    @GetMapping("/level/{level}/service/{service}")
    public ResponseEntity<List<LogEntry>> getByLevelAndService(
            @PathVariable String level, @PathVariable String service) {
        return ResponseEntity.ok(logService.findByLevelAndService(level, service));
    }

    @GetMapping("/response-code/{responseCode}")
    public ResponseEntity<List<LogEntry>> getByResponseCode(@PathVariable Integer responseCode) {
        return ResponseEntity.ok(logService.findByResponseCode(responseCode));
    }

    @GetMapping("/trace/{traceId}")
    public ResponseEntity<List<LogEntry>> getByTraceId(@PathVariable String traceId) {
        return ResponseEntity.ok(logService.findByTraceId(traceId));
    }

    @GetMapping("/search/message/{message}")
    public ResponseEntity<List<LogEntry>> searchByMessage(@PathVariable String message) {
        return ResponseEntity.ok(logService.searchByMessage(message));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LogEntry>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(logService.findByTimestampBetween(start, end));
    }

    @GetMapping("/level/{level}/date-range")
    public ResponseEntity<List<LogEntry>> getByLevelAndDateRange(
            @PathVariable String level,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(logService.findByLevelAndTimestampRange(level, start, end));
    }

    @GetMapping("/errors")
    public ResponseEntity<List<LogEntry>> getErrors() {
        return ResponseEntity.ok(logService.findErrorResponses());
    }

    @GetMapping("/slow")
    public ResponseEntity<List<LogEntry>> getSlowRequests(
            @RequestParam(defaultValue = "1000") long minDurationMs) {
        return ResponseEntity.ok(logService.findSlowRequests(minDurationMs));
    }

    @GetMapping("/count/level/{level}")
    public ResponseEntity<Map<String, Long>> countByLevel(@PathVariable String level) {
        return ResponseEntity.ok(Map.of("count", logService.countByLevel(level)));
    }

    @GetMapping("/count/service/{service}")
    public ResponseEntity<Map<String, Long>> countByService(@PathVariable String service) {
        return ResponseEntity.ok(Map.of("count", logService.countByService(service)));
    }
}
