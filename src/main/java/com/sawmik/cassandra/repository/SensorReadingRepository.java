package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.SensorReading;
import com.sawmik.cassandra.entity.SensorReadingKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SensorReadingRepository extends CassandraRepository<SensorReading, SensorReadingKey> {

    List<SensorReading> findByIdSensorId(UUID sensorId);

    @Query("SELECT * FROM sensor_readings WHERE sensor_id = ?0 AND reading_time >= ?1 AND reading_time <= ?2")
    List<SensorReading> findByIdSensorIdAndIdReadingTimeBetween(UUID sensorId, Instant start, Instant end);

    @Query("SELECT * FROM sensor_readings WHERE sensor_id = ?0 ORDER BY reading_time DESC LIMIT ?1")
    List<SensorReading> findLatestReadings(UUID sensorId, int limit);
}
