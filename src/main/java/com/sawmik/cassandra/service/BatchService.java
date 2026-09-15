package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchService {

    private final CqlSession cqlSession;

    public void batchInsertProducts(List<Map<String, Object>> products) {
        PreparedStatement insert = cqlSession.prepare(
                "INSERT INTO products (id, name, description, category, tags, price, stock_quantity, available, seller_id, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, toTimestamp(now()), toTimestamp(now()))"
        );

        BatchStatement batch = BatchStatement.builder(BatchType.UNLOGGED)
                .build();

        for (Map<String, Object> product : products) {
            Object priceVal = product.get("price");
            BigDecimal price = priceVal instanceof Number ? BigDecimal.valueOf(((Number) priceVal).doubleValue()) : BigDecimal.ZERO;
            BoundStatement bound = insert.bind(
                    UUID.randomUUID(),
                    product.get("name"),
                    product.get("description"),
                    product.get("category"),
                    product.get("tags"),
                    price,
                    product.get("stockQuantity"),
                    product.get("available"),
                    product.get("sellerId")
            );
            batch = batch.add(bound);
        }

        cqlSession.execute(batch);
        log.info("Batch inserted {} products", products.size());
    }

    public void batchInsertLogEntries(List<Map<String, Object>> logEntries) {
        PreparedStatement insert = cqlSession.prepare(
                "INSERT INTO log_entries (id, level, service, message, trace_id, response_code, duration_ms, host, timestamp) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, toTimestamp(now()))"
        );

        BatchStatement batch = BatchStatement.builder(BatchType.UNLOGGED)
                .build();

        for (Map<String, Object> entry : logEntries) {
            BoundStatement bound = insert.bind(
                    UUID.randomUUID(),
                    entry.get("level"),
                    entry.get("service"),
                    entry.get("message"),
                    entry.get("traceId"),
                    entry.get("responseCode"),
                    entry.get("durationMs"),
                    entry.get("host")
            );
            batch = batch.add(bound);
        }

        cqlSession.execute(batch);
        log.info("Batch inserted {} log entries", logEntries.size());
    }
}
