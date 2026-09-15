package com.sawmik.cassandra.config;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@Slf4j
public class CassandraConfig {

    @Value("${spring.cassandra.contact-points:localhost}")
    private String contactPoints;

    @Value("${spring.cassandra.port:9042}")
    private int port;

    @Value("${spring.cassandra.local-datacenter:dc1}")
    private String localDatacenter;

    @Value("${spring.cassandra.username:cassandra}")
    private String username;

    @Value("${spring.cassandra.password:password}")
    private String password;

    @Value("${spring.cassandra.keyspace-name:cassandra_app}")
    private String keyspace;

    @Bean
    public CqlSession cassandraSession() {
        log.info("Creating CqlSession - keyspace '{}' will be created if missing", keyspace);

        // Connect without keyspace to create it + UDTs
        try (CqlSession adminSession = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .withAuthCredentials(username, password)
                .build()) {

            // Create keyspace
            adminSession.execute(String.format(
                    "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}",
                    keyspace));
            log.info("Keyspace '{}' ready", keyspace);

            // Create UDTs (must exist before tables that reference them)
            adminSession.execute("USE " + keyspace);
            createTypeIfNotExists(adminSession, "review",
                    "rating int, comment text, reviewer text, created_at timestamp");
            createTypeIfNotExists(adminSession, "address",
                    "street text, city text, state text, zip text, country text");
            createTypeIfNotExists(adminSession, "coordinate",
                    "latitude double, longitude double");
            log.info("UDTs ready");
        }

        // Connect WITH keyspace
        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(contactPoints, port))
                .withLocalDatacenter(localDatacenter)
                .withAuthCredentials(username, password)
                .withKeyspace(keyspace)
                .build();

        log.info("CqlSession connected to keyspace '{}'", keyspace);
        return session;
    }

    private void createTypeIfNotExists(CqlSession session, String typeName, String fields) {
        session.execute(String.format("CREATE TYPE IF NOT EXISTS %s (%s)", typeName, fields));
    }
}
