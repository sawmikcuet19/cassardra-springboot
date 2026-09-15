package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("metrics_counters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsCounter {

    @PrimaryKey
    @Id
    private String name;

    @Column("counter_value")
    private Long counterValue;
}
