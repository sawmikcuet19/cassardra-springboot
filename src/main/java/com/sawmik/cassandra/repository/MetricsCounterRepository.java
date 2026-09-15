package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.MetricsCounter;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricsCounterRepository extends CassandraRepository<MetricsCounter, String> {

    @Query("UPDATE metrics_counters SET counter_value = counter_value + ?1 WHERE name = ?0")
    void incrementCounter(String name, long delta);

    @Query("UPDATE metrics_counters SET counter_value = counter_value - ?1 WHERE name = ?0")
    void decrementCounter(String name, long delta);
}
