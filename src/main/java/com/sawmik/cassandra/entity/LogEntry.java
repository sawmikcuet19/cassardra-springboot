package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Indexed;

import java.time.Instant;
import java.util.UUID;

@Table("log_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("level")
    @Indexed
    private String level;

    @Column("service")
    @Indexed
    private String service;

    @Column("message")
    private String message;

    @Column("trace_id")
    private String traceId;

    @Column("response_code")
    @Indexed
    private Integer responseCode;

    @Column("duration_ms")
    private Long durationMs;

    @Column("host")
    private String host;

    @Column("timestamp")
    @Indexed
    private Instant timestamp;
}
