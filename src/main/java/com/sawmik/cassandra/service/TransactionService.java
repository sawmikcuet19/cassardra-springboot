package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final CqlSession cqlSession;

    public Optional<UUID> insertIfNotExists(String table, String keyColumn, Object keyValue, String valueColumn, Object valueValue) {
        String cql = String.format("INSERT INTO %s (%s, %s) VALUES (?, ?) IF NOT EXISTS", table, keyColumn, valueColumn);
        ResultSet rs = cqlSession.execute(SimpleStatement.newInstance(cql, keyValue, valueValue));
        Row row = rs.one();
        if (row != null && row.getBoolean("[applied]")) {
            log.info("Lightweight transaction applied: inserted into {}", table);
            return Optional.of(UUID.randomUUID());
        }
        log.info("Lightweight transaction NOT applied: entry already exists in {}", table);
        return Optional.empty();
    }

    public boolean compareAndSet(String table, String keyColumn, Object keyValue,
                                  String valueColumn, Object expectedValue, Object newValue) {
        String cql = String.format("UPDATE %s SET %s = ? WHERE %s = ? IF %s = ?", table, valueColumn, keyColumn, valueColumn);
        ResultSet rs = cqlSession.execute(SimpleStatement.newInstance(cql, newValue, keyValue, expectedValue));
        Row row = rs.one();
        boolean applied = row != null && row.getBoolean("[applied]");
        log.info("Compare-and-set on {}: applied={}", table, applied);
        return applied;
    }

    public boolean deleteIfExists(String table, String keyColumn, Object keyValue) {
        String cql = String.format("DELETE FROM %s WHERE %s = ? IF EXISTS", table, keyColumn);
        ResultSet rs = cqlSession.execute(SimpleStatement.newInstance(cql, keyValue));
        Row row = rs.one();
        boolean applied = row != null && row.getBoolean("[applied]");
        log.info("Delete-if-exists on {}: applied={}", table, applied);
        return applied;
    }
}
