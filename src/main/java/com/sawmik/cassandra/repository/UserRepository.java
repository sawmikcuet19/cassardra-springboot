package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.User;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CassandraRepository<User, String> {

    @AllowFiltering
    Optional<User> findByUsername(String username);

    @AllowFiltering
    boolean existsByUsername(String username);

    @AllowFiltering
    boolean existsByEmail(String email);

    @AllowFiltering
    Optional<User> findByEmail(String email);
}
