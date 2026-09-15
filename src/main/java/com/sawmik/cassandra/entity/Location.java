package com.sawmik.cassandra.entity;

import com.sawmik.cassandra.udt.Coordinate;
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

@Table("locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("name")
    @Indexed
    private String name;

    @Column("address")
    private String address;

    @Column("type")
    @Indexed
    private String type;

    @Column("coordinates")
    private Coordinate coordinates;

    @Column("country")
    @Indexed
    private String country;

    @Column("city")
    @Indexed
    private String city;

    @Column("created_at")
    private Instant createdAt;
}
