package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.Location;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends CassandraRepository<Location, UUID> {

    List<Location> findByType(String type);

    List<Location> findByCountry(String country);

    List<Location> findByCity(String city);

    @AllowFiltering
    List<Location> findByCountryAndCity(String country, String city);

    @Query("SELECT * FROM locations WHERE name CONTAINS ?0 ALLOW FILTERING")
    List<Location> searchByName(String name);

    @AllowFiltering
    List<Location> findByTypeAndCountry(String type, String country);
}
