package com.sawmik.cassandra.service;

import com.sawmik.cassandra.entity.LogEntry;
import com.sawmik.cassandra.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;

    public LogEntry save(LogEntry logEntry) {
        if (logEntry.getId() == null) {
            logEntry.setId(UUID.randomUUID());
        }
        if (logEntry.getTimestamp() == null) {
            logEntry.setTimestamp(Instant.now());
        }
        return logEntryRepository.save(logEntry);
    }

    public Optional<LogEntry> findById(UUID id) {
        return logEntryRepository.findById(id);
    }

    public List<LogEntry> findAll() {
        return logEntryRepository.findAll();
    }

    public void deleteById(UUID id) {
        logEntryRepository.deleteById(id);
    }

    public void deleteAll() {
        logEntryRepository.deleteAll();
    }

    public List<LogEntry> bulkSave(List<LogEntry> logEntries) {
        logEntries.forEach(l -> {
            if (l.getId() == null) l.setId(UUID.randomUUID());
            if (l.getTimestamp() == null) l.setTimestamp(Instant.now());
        });
        return logEntryRepository.saveAll(logEntries).stream().toList();
    }

    // Search
    public List<LogEntry> findByLevel(String level) {
        return logEntryRepository.findByLevel(level);
    }

    public List<LogEntry> findByService(String service) {
        return logEntryRepository.findByService(service);
    }

    public List<LogEntry> findByLevelAndService(String level, String service) {
        return logEntryRepository.findByLevelAndService(level, service);
    }

    public List<LogEntry> findByResponseCode(Integer responseCode) {
        return logEntryRepository.findByResponseCode(responseCode);
    }

    public List<LogEntry> findByTimestampBetween(Instant start, Instant end) {
        return logEntryRepository.findByTimestampBetween(start, end);
    }

    public List<LogEntry> findByTraceId(String traceId) {
        return logEntryRepository.findByTraceId(traceId);
    }

    public List<LogEntry> searchByMessage(String message) {
        return logEntryRepository.searchByMessage(message);
    }

    public List<LogEntry> findByLevelAndTimestampRange(String level, Instant start, Instant end) {
        return logEntryRepository.findByLevelAndTimestampRange(level, start, end);
    }

    public List<LogEntry> findErrorResponses() {
        return logEntryRepository.findErrorResponses();
    }

    public List<LogEntry> findSlowRequests(long minDurationMs) {
        return logEntryRepository.findSlowRequests(minDurationMs);
    }

    public long countByLevel(String level) {
        return logEntryRepository.countByLevel(level);
    }

    public long countByService(String service) {
        return logEntryRepository.countByService(service);
    }
}
