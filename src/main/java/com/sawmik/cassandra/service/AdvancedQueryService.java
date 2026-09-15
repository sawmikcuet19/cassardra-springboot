package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedQueryService {

    private final CqlSession cqlSession;
    private final CassandraTemplate cassandraTemplate;

    // --- Consistency Level Switching ---
    public List<Row> queryWithConsistency(String cql, DefaultConsistencyLevel level, Object... args) {
        SimpleStatement stmt = SimpleStatement.newInstance(cql, args)
                .setConsistencyLevel(level);
        ResultSet rs = cqlSession.execute(stmt);
        return rs.all();
    }

    public List<Row> queryWithConsistencyOne(String cql, Object... args) {
        return queryWithConsistency(cql, DefaultConsistencyLevel.ONE, args);
    }

    public List<Row> queryWithConsistencyQuorum(String cql, Object... args) {
        return queryWithConsistency(cql, DefaultConsistencyLevel.QUORUM, args);
    }

    public List<Row> queryWithConsistencyAll(String cql, Object... args) {
        return queryWithConsistency(cql, DefaultConsistencyLevel.ALL, args);
    }

    public List<Row> queryWithConsistencyLocalQuorum(String cql, Object... args) {
        return queryWithConsistency(cql, DefaultConsistencyLevel.LOCAL_QUORUM, args);
    }

    // --- Token Range Queries ---
    public List<Row> findByTokenRange(String table, String partitionKey, long startToken, long endToken) {
        String cql = String.format("SELECT * FROM %s WHERE token(%s) > token(?) AND token(%s) <= token(?)",
                table, partitionKey, partitionKey);
        return cqlSession.execute(SimpleStatement.newInstance(cql, startToken, endToken)).all();
    }

    public List<Row> findByToken(String table, String partitionKey, Object partitionValue) {
        String cql = String.format("SELECT * FROM %s WHERE token(%s) = token(?)", table, partitionKey);
        return cqlSession.execute(SimpleStatement.newInstance(cql, partitionValue)).all();
    }

    // --- Aggregation Functions ---
    public long countWithCql(String table) {
        ResultSet rs = cqlSession.execute("SELECT COUNT(*) FROM " + table);
        return rs.one().getLong(0);
    }

    public Object minWithCql(String table, String column) {
        String cql = String.format("SELECT MIN(%s) FROM %s", column, table);
        Row row = cqlSession.execute(cql).one();
        return row != null ? row.getObject(0) : null;
    }

    public Object maxWithCql(String table, String column) {
        String cql = String.format("SELECT MAX(%s) FROM %s", column, table);
        Row row = cqlSession.execute(cql).one();
        return row != null ? row.getObject(0) : null;
    }

    public Object sumWithCql(String table, String column) {
        String cql = String.format("SELECT SUM(%s) FROM %s", column, table);
        Row row = cqlSession.execute(cql).one();
        return row != null ? row.getObject(0) : null;
    }

    public Double avgWithCql(String table, String column) {
        String cql = String.format("SELECT AVG(%s) FROM %s", column, table);
        Row row = cqlSession.execute(cql).one();
        return row != null ? row.getDouble(0) : null;
    }

    // --- Range Tombstones ---
    public void deleteRange(String table, String partitionKey, Object partitionValue,
                            String clusteringColumn, Object startValue, Object endValue) {
        String cql = String.format("DELETE FROM %s WHERE %s = ? AND %s >= ? AND %s <= ?",
                table, partitionKey, clusteringColumn, clusteringColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, partitionValue, startValue, endValue));
        log.info("Range deleted from {} where {}={} and {} between {} and {}",
                table, partitionKey, partitionValue, clusteringColumn, startValue, endValue);
    }

    // --- TRUNCATE ---
    public void truncateTable(String table) {
        cqlSession.execute("TRUNCATE TABLE " + table);
        log.info("Truncated table {}", table);
    }

    // --- ALTER TABLE ---
    public void addColumn(String table, String columnName, String columnType) {
        String cql = String.format("ALTER TABLE %s ADD %s %s", table, columnName, columnType);
        cqlSession.execute(cql);
        log.info("Added column {} ({}) to {}", columnName, columnType, table);
    }

    public void dropColumn(String table, String columnName) {
        String cql = String.format("ALTER TABLE %s DROP %s", table, columnName);
        cqlSession.execute(cql);
        log.info("Dropped column {} from {}", columnName, table);
    }

    public void renameColumn(String table, String oldName, String newName) {
        String cql = String.format("ALTER TABLE %s RENAME %s TO %s", table, oldName, newName);
        cqlSession.execute(cql);
        log.info("Renamed column {} to {} in {}", oldName, newName, table);
    }

    public void alterTableComment(String table, String comment) {
        String cql = String.format("ALTER TABLE %s WITH comment = '%s'", table, comment);
        cqlSession.execute(cql);
        log.info("Set comment on {}", table);
    }

    // --- USING TIMESTAMP ---
    public void insertWithTimestamp(String table, Map<String, Object> values, long timestampMicros) {
        if (values.isEmpty()) return;
        String columns = String.join(", ", values.keySet());
        String placeholders = values.keySet().stream().map(k -> "?").reduce((a, b) -> a + ", " + b).orElse("");
        String cql = String.format("INSERT INTO %s (%s) VALUES (%s) USING TIMESTAMP %d",
                table, columns, placeholders, timestampMicros);
        cqlSession.execute(SimpleStatement.newInstance(cql, values.values().toArray()));
        log.info("Inserted into {} with TIMESTAMP {}", table, timestampMicros);
    }

    public void updateWithTimestamp(String table, String setClause, String whereClause,
                                    long timestampMicros, Object... args) {
        String cql = String.format("UPDATE %s USING TIMESTAMP %d SET %s WHERE %s",
                table, timestampMicros, setClause, whereClause);
        cqlSession.execute(SimpleStatement.newInstance(cql, args));
        log.info("Updated {} with TIMESTAMP {}", table, timestampMicros);
    }

    // --- Pagination with List ---
    public List<Map<String, Object>> paginatedQuery(String table, String orderBy, int limit) {
        String cql = String.format("SELECT * FROM %s ORDER BY %s LIMIT %d",
                table, orderBy, limit);
        ResultSet rs = cqlSession.execute(cql);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Row row : rs) {
            Map<String, Object> map = new HashMap<>();
            row.getColumnDefinitions().forEach(cd -> map.put(cd.getName().toString(), row.getObject(cd.getName())));
            results.add(map);
        }
        return results;
    }
}
