package com.sawmik.cassandra.service;

import com.sawmik.cassandra.entity.SensorReading;
import com.sawmik.cassandra.entity.SensorReadingKey;
import com.sawmik.cassandra.repository.SensorReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SensorReadingService {

    private final SensorReadingRepository sensorReadingRepository;

    public SensorReading save(UUID sensorId, Double value, String unit, String metadata) {
        SensorReading reading = new SensorReading();
        SensorReadingKey key = new SensorReadingKey(sensorId, Instant.now());
        reading.setId(key);
        reading.setValue(value);
        reading.setUnit(unit);
        reading.setMetadata(metadata);
        return sensorReadingRepository.save(reading);
    }

    public List<SensorReading> findBySensorId(UUID sensorId) {
        return sensorReadingRepository.findByIdSensorId(sensorId);
    }

    public List<SensorReading> findBySensorIdAndTimeRange(UUID sensorId, Instant start, Instant end) {
        return sensorReadingRepository.findByIdSensorIdAndIdReadingTimeBetween(sensorId, start, end);
    }

    public List<SensorReading> findLatestReadings(UUID sensorId, int limit) {
        return sensorReadingRepository.findLatestReadings(sensorId, limit);
    }

    public List<SensorReading> saveBatch(List<SensorReading> readings) {
        return sensorReadingRepository.saveAll(readings).stream().toList();
    }

    public void deleteAll() {
        sensorReadingRepository.deleteAll();
    }
}
