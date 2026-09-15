package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.LogEntry;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogEntryRepository extends CassandraRepository<LogEntry, UUID> {

    List<LogEntry> findByLevel(String level);

    List<LogEntry> findByService(String service);

    @AllowFiltering
    List<LogEntry> findByLevelAndService(String level, String service);

    @AllowFiltering
    List<LogEntry> findByResponseCode(Integer responseCode);

    @AllowFiltering
    List<LogEntry> findByTimestampBetween(Instant start, Instant end);

    @AllowFiltering
    List<LogEntry> findByTraceId(String traceId);

    @Query("SELECT * FROM log_entries WHERE message CONTAINS ?0 ALLOW FILTERING")
    List<LogEntry> searchByMessage(String message);

    @Query("SELECT * FROM log_entries WHERE level = ?0 AND timestamp >= ?1 AND timestamp <= ?2 ALLOW FILTERING")
    List<LogEntry> findByLevelAndTimestampRange(String level, Instant start, Instant end);

    @Query("SELECT * FROM log_entries WHERE response_code >= 400 ALLOW FILTERING")
    List<LogEntry> findErrorResponses();

    @Query("SELECT * FROM log_entries WHERE duration_ms >= ?0 ALLOW FILTERING")
    List<LogEntry> findSlowRequests(long minDurationMs);

    @AllowFiltering
    long countByLevel(String level);

    @AllowFiltering
    long countByService(String service);
}
