package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchemaService {

    private final CqlSession cqlSession;

    public void createKeyspaceIfNotExists(String keyspace, String replicationStrategy, int replicationFactor) {
        String cql = String.format(
                "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class': '%s', 'replication_factor': %d}",
                keyspace, replicationStrategy, replicationFactor
        );
        cqlSession.execute(cql);
        log.info("Keyspace {} created/verified", keyspace);
    }

    public void createType(String keyspace, String typeName, List<String> fieldDefs) {
        String fields = String.join(", ", fieldDefs);
        String cql = String.format("CREATE TYPE IF NOT EXISTS %s.%s (%s)", keyspace, typeName, fields);
        cqlSession.execute(cql);
        log.info("Type {}.{} created/verified", keyspace, typeName);
    }

    public void createTable(String keyspace, String tableName, List<String> columnDefs, String primaryKey) {
        String columns = String.join(", ", columnDefs);
        String cql = String.format("CREATE TABLE IF NOT EXISTS %s.%s (%s, PRIMARY KEY (%s))",
                keyspace, tableName, columns, primaryKey);
        cqlSession.execute(cql);
        log.info("Table {}.{} created/verified", keyspace, tableName);
    }

    public void createIndex(String keyspace, String tableName, String columnName) {
        String indexName = String.format("idx_%s_%s", tableName, columnName);
        String cql = String.format("CREATE INDEX IF NOT EXISTS %s ON %s.%s (%s)",
                indexName, keyspace, tableName, columnName);
        cqlSession.execute(cql);
        log.info("Index {} created on {}.{}", indexName, keyspace, tableName);
    }

    public void createMaterializedView(String keyspace, String viewName, String selectColumns,
                                        String fromTable, String whereClause, String primaryKeys) {
        String cql = String.format("CREATE MATERIALIZED VIEW IF NOT EXISTS %s.%s AS SELECT %s FROM %s.%s WHERE %s PRIMARY KEY (%s)",
                keyspace, viewName, selectColumns, keyspace, fromTable, whereClause, primaryKeys);
        cqlSession.execute(cql);
        log.info("Materialized view {}.{}/{} created", keyspace, fromTable, viewName);
    }

    public void dropTable(String keyspace, String tableName) {
        cqlSession.execute(String.format("DROP TABLE IF EXISTS %s.%s", keyspace, tableName));
        log.info("Table {}.{} dropped", keyspace, tableName);
    }

    public void dropKeyspace(String keyspace) {
        cqlSession.execute(String.format("DROP KEYSPACE IF EXISTS %s", keyspace));
        log.info("Keyspace {} dropped", keyspace);
    }

    public List<String> describeKeyspace(String keyspace) {
        var rs = cqlSession.execute(String.format("DESCRIBE KEYSPACE %s", keyspace));
        return rs.all().stream().map(row -> row.getString(0)).toList();
    }
}
