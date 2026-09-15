package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.SchemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/schema")
@RequiredArgsConstructor
public class SchemaController {

    private final SchemaService schemaService;

    @PostMapping("/keyspace")
    public ResponseEntity<Map<String, String>> createKeyspace(@RequestBody Map<String, Object> body) {
        String keyspace = (String) body.get("keyspace");
        String strategy = (String) body.getOrDefault("strategy", "SimpleStrategy");
        int replicationFactor = body.get("replicationFactor") != null ? ((Number) body.get("replicationFactor")).intValue() : 1;
        schemaService.createKeyspaceIfNotExists(keyspace, strategy, replicationFactor);
        return ResponseEntity.ok(Map.of("message", "Keyspace " + keyspace + " created/verified"));
    }

    @PostMapping("/table/{keyspace}")
    public ResponseEntity<Map<String, String>> createTable(
            @PathVariable String keyspace,
            @RequestBody Map<String, Object> body) {
        String tableName = (String) body.get("tableName");
        List<String> columnDefs = (List<String>) body.get("columns");
        String primaryKey = (String) body.get("primaryKey");
        schemaService.createTable(keyspace, tableName, columnDefs, primaryKey);
        return ResponseEntity.ok(Map.of("message", "Table " + tableName + " created/verified"));
    }

    @PostMapping("/index/{keyspace}/{tableName}")
    public ResponseEntity<Map<String, String>> createIndex(
            @PathVariable String keyspace,
            @PathVariable String tableName,
            @RequestBody Map<String, String> body) {
        String columnName = body.get("column");
        schemaService.createIndex(keyspace, tableName, columnName);
        return ResponseEntity.ok(Map.of("message", "Index created on " + tableName + "." + columnName));
    }

    @PostMapping("/materialized-view/{keyspace}")
    public ResponseEntity<Map<String, String>> createMaterializedView(
            @PathVariable String keyspace,
            @RequestBody Map<String, String> body) {
        schemaService.createMaterializedView(
                keyspace, body.get("viewName"), body.get("selectColumns"),
                body.get("fromTable"), body.get("whereClause"), body.get("primaryKeys"));
        return ResponseEntity.ok(Map.of("message", "Materialized view created"));
    }

    @DeleteMapping("/table/{keyspace}/{tableName}")
    public ResponseEntity<Map<String, String>> dropTable(
            @PathVariable String keyspace, @PathVariable String tableName) {
        schemaService.dropTable(keyspace, tableName);
        return ResponseEntity.ok(Map.of("message", "Table " + tableName + " dropped"));
    }

    @DeleteMapping("/keyspace/{keyspace}")
    public ResponseEntity<Map<String, String>> dropKeyspace(@PathVariable String keyspace) {
        schemaService.dropKeyspace(keyspace);
        return ResponseEntity.ok(Map.of("message", "Keyspace " + keyspace + " dropped"));
    }

    @GetMapping("/describe/{keyspace}")
    public ResponseEntity<List<String>> describeKeyspace(@PathVariable String keyspace) {
        return ResponseEntity.ok(schemaService.describeKeyspace(keyspace));
    }
}
