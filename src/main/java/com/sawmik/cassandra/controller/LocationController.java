package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.dto.location.LocationRequest;
import com.sawmik.cassandra.entity.Location;
import com.sawmik.cassandra.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<Location> create(@RequestBody LocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.save(request));
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> update(@PathVariable UUID id, @RequestBody LocationRequest request) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        locationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(locationService.findByType(type));
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<List<Location>> getByCountry(@PathVariable String country) {
        return ResponseEntity.ok(locationService.findByCountry(country));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Location>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(locationService.findByCity(city));
    }

    @GetMapping("/country/{country}/city/{city}")
    public ResponseEntity<List<Location>> getByCountryAndCity(
            @PathVariable String country, @PathVariable String city) {
        return ResponseEntity.ok(locationService.findByCountryAndCity(country, city));
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Location>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(locationService.searchByName(name));
    }

    @GetMapping("/type/{type}/country/{country}")
    public ResponseEntity<List<Location>> getByTypeAndCountry(
            @PathVariable String type, @PathVariable String country) {
        return ResponseEntity.ok(locationService.findByTypeAndCountry(type, country));
    }
}
