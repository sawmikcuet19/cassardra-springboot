package com.sawmik.cassandra.service;

import com.sawmik.cassandra.dto.location.LocationRequest;
import com.sawmik.cassandra.entity.Location;
import com.sawmik.cassandra.repository.LocationRepository;
import com.sawmik.cassandra.udt.Coordinate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location save(LocationRequest request) {
        Location location = new Location();
        location.setId(UUID.randomUUID());
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setType(request.getType());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            location.setCoordinates(new Coordinate(request.getLatitude(), request.getLongitude()));
        }
        location.setCountry(request.getCountry());
        location.setCity(request.getCity());
        location.setCreatedAt(Instant.now());
        return locationRepository.save(location);
    }

    public Location update(UUID id, LocationRequest request) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found: " + id));
        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setType(request.getType());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            existing.setCoordinates(new Coordinate(request.getLatitude(), request.getLongitude()));
        }
        existing.setCountry(request.getCountry());
        existing.setCity(request.getCity());
        return locationRepository.save(existing);
    }

    public Optional<Location> findById(UUID id) {
        return locationRepository.findById(id);
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public void deleteById(UUID id) {
        locationRepository.deleteById(id);
    }

    public List<Location> findByType(String type) {
        return locationRepository.findByType(type);
    }

    public List<Location> findByCountry(String country) {
        return locationRepository.findByCountry(country);
    }

    public List<Location> findByCity(String city) {
        return locationRepository.findByCity(city);
    }

    public List<Location> findByCountryAndCity(String country, String city) {
        return locationRepository.findByCountryAndCity(country, city);
    }

    public List<Location> searchByName(String name) {
        return locationRepository.searchByName(name);
    }

    public List<Location> findByTypeAndCountry(String type, String country) {
        return locationRepository.findByTypeAndCountry(type, country);
    }
}
