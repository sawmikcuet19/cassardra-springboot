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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Table("events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("event_type")
    @Indexed
    private String eventType;

    @Column("event_name")
    private String eventName;

    @Column("source")
    private String source;

    @Column("data")
    private Map<String, String> data;

    @Column("tags")
    private Set<String> tags;

    @Column("related_ids")
    private List<UUID> relatedIds;

    @Column("event_time")
    private Instant eventTime;

    @Column("version")
    private Long version;
}
