package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SasiIndexService {

    private final CqlSession cqlSession;

    public void createSasiIndex(String keyspace, String table, String columnName) {
        String indexName = String.format("sasi_idx_%s_%s", table, columnName);
        String cql = String.format(
                "CREATE CUSTOM INDEX IF NOT EXISTS %s ON %s.%s (%s) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'CONTAINS', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer'}",
                indexName, keyspace, table, columnName);
        cqlSession.execute(cql);
        log.info("SASI index {} created on {}.{}", indexName, keyspace, table);
    }

    public void createSasiPrefixIndex(String keyspace, String table, String columnName) {
        String indexName = String.format("sasi_prefix_%s_%s", table, columnName);
        String cql = String.format(
                "CREATE CUSTOM INDEX IF NOT EXISTS %s ON %s.%s (%s) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'PREFIX', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.NonTokenizingAnalyzer'}",
                indexName, keyspace, table, columnName);
        cqlSession.execute(cql);
        log.info("SASI PREFIX index {} created on {}.{}", indexName, keyspace, table);
    }

    public void createSasiSparseIndex(String keyspace, String table, String columnName) {
        String indexName = String.format("sasi_sp_%s_%s", table, columnName);
        String cql = String.format(
                "CREATE CUSTOM INDEX IF NOT EXISTS %s ON %s.%s (%s) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'SPARSE'}",
                indexName, keyspace, table, columnName);
        cqlSession.execute(cql);
        log.info("SASI SPARSE index {} created on {}.{}", indexName, keyspace, table);
    }

    public void dropIndex(String keyspace, String indexName) {
        cqlSession.execute(String.format("DROP INDEX IF EXISTS %s.%s", keyspace, indexName));
        log.info("Dropped index {}.{}", keyspace, indexName);
    }
}
