package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.entity.SensorReading;
import com.sawmik.cassandra.service.SensorReadingService;
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
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    @PostMapping("/{sensorId}/readings")
    public ResponseEntity<SensorReading> createReading(
            @PathVariable UUID sensorId,
            @RequestBody Map<String, Object> body) {
        Double value = (Double) body.get("value");
        String unit = (String) body.get("unit");
        String metadata = (String) body.get("metadata");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sensorReadingService.save(sensorId, value, unit, metadata));
    }

    @GetMapping("/{sensorId}/readings")
    public ResponseEntity<List<SensorReading>> getBySensorId(@PathVariable UUID sensorId) {
        return ResponseEntity.ok(sensorReadingService.findBySensorId(sensorId));
    }

    @GetMapping("/{sensorId}/readings/range")
    public ResponseEntity<List<SensorReading>> getBySensorIdAndTimeRange(
            @PathVariable UUID sensorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(sensorReadingService.findBySensorIdAndTimeRange(sensorId, start, end));
    }

    @GetMapping("/{sensorId}/readings/latest")
    public ResponseEntity<List<SensorReading>> getLatestReadings(
            @PathVariable UUID sensorId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(sensorReadingService.findLatestReadings(sensorId, limit));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SensorReading>> batchCreate(@RequestBody List<Map<String, Object>> readings) {
        List<SensorReading> saved = readings.stream()
                .map(r -> sensorReadingService.save(
                        UUID.fromString((String) r.get("sensorId")),
                        ((Number) r.get("value")).doubleValue(),
                        (String) r.get("unit"),
                        (String) r.get("metadata")))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/readings")
    public ResponseEntity<Void> deleteAll() {
        sensorReadingService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
