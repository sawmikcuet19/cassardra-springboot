package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtlService {

    private final CqlSession cqlSession;

    public void insertWithTtl(String table, Map<String, Object> values, int ttlSeconds) {
        if (values.isEmpty()) return;

        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", values.keySet().stream().map(k -> "?").toList());
        String cql = String.format("INSERT INTO %s (%s) VALUES (%s) USING TTL %d", table, columns, placeholders, ttlSeconds);

        var statement = com.datastax.oss.driver.api.core.cql.SimpleStatement.newInstance(cql, values.values().toArray());
        cqlSession.execute(statement);
        log.info("Inserted into {} with TTL {}s", table, ttlSeconds);
    }

    public Map<String, Object> getWithTtl(String table, String idColumn, Object idValue) {
        String cql = String.format("SELECT *, TTL(%s) as ttl FROM %s WHERE %s = ?", idColumn, table, idColumn, idColumn);
        // Note: this is a simplified version
        var statement = com.datastax.oss.driver.api.core.cql.SimpleStatement.newInstance(
                String.format("SELECT *, TTL(id) as ttl FROM %s WHERE %s = ? LIMIT 1", table, idColumn),
                idValue
        );
        ResultSet rs = cqlSession.execute(statement);
        Row row = rs.one();
        if (row == null) return null;

        Map<String, Object> result = new HashMap<>();
        row.getColumnDefinitions().forEach(cd -> result.put(cd.getName().toString(), row.getObject(cd.getName())));
        return result;
    }

    public List<Map<String, Object>> findAllWithTtl(String table) {
        String cql = String.format("SELECT id, level, service, message, host, trace_id, response_code, duration_ms, timestamp, TTL(level) as ttl FROM %s", table);
        ResultSet rs = cqlSession.execute(cql);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Row row : rs) {
            Map<String, Object> map = new HashMap<>();
            row.getColumnDefinitions().forEach(cd -> {
                String name = cd.getName().toString();
                if (!"ttl".equals(name)) {
                    map.put(name, row.getObject(cd.getName()));
                }
            });
            map.put("ttl", row.getInt("ttl"));
            results.add(map);
        }
        return results;
    }

    public void updateTtl(String table, String idColumn, Object idValue, int newTtlSeconds) {
        // Read existing data
        Map<String, Object> data = getWithTtl(table, idColumn, idValue);
        if (data == null) return;

        // Re-insert with new TTL
        data.remove("ttl");
        insertWithTtl(table, data, newTtlSeconds);
        log.info("Updated TTL for {} in {} to {}s", idValue, table, newTtlSeconds);
    }
}
