package com.sawmik.cassandra.controller;

import com.datastax.oss.driver.api.core.cql.Row;
import com.sawmik.cassandra.service.AdvancedQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/advanced")
@RequiredArgsConstructor
public class AdvancedQueryController {

    private final AdvancedQueryService advancedQueryService;

    @GetMapping("/count/{table}")
    public ResponseEntity<Map<String, Object>> count(@PathVariable String table) {
        return ResponseEntity.ok(Map.of("table", table, "count", advancedQueryService.countWithCql(table)));
    }

    @GetMapping("/min/{table}/{column}")
    public ResponseEntity<Map<String, Object>> min(@PathVariable String table, @PathVariable String column) {
        return ResponseEntity.ok(Map.of("table", table, "column", column, "min", advancedQueryService.minWithCql(table, column)));
    }

    @GetMapping("/max/{table}/{column}")
    public ResponseEntity<Map<String, Object>> max(@PathVariable String table, @PathVariable String column) {
        return ResponseEntity.ok(Map.of("table", table, "column", column, "max", advancedQueryService.maxWithCql(table, column)));
    }

    @GetMapping("/sum/{table}/{column}")
    public ResponseEntity<Map<String, Object>> sum(@PathVariable String table, @PathVariable String column) {
        return ResponseEntity.ok(Map.of("table", table, "column", column, "sum", advancedQueryService.sumWithCql(table, column)));
    }

    @GetMapping("/avg/{table}/{column}")
    public ResponseEntity<Map<String, Object>> avg(@PathVariable String table, @PathVariable String column) {
        return ResponseEntity.ok(Map.of("table", table, "column", column, "avg", advancedQueryService.avgWithCql(table, column)));
    }

    @GetMapping("/token-range/{table}/{partitionKey}")
    public ResponseEntity<List<Map<String, Object>>> tokenRange(
            @PathVariable String table, @PathVariable String partitionKey,
            @RequestParam long startToken, @RequestParam long endToken) {
        List<Row> rows = advancedQueryService.findByTokenRange(table, partitionKey, startToken, endToken);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Row row : rows) {
            Map<String, Object> map = new HashMap<>();
            row.getColumnDefinitions().forEach(cd -> map.put(cd.getName().toString(), row.getObject(cd.getName())));
            results.add(map);
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/truncate/{table}")
    public ResponseEntity<Map<String, String>> truncate(@PathVariable String table) {
        advancedQueryService.truncateTable(table);
        return ResponseEntity.ok(Map.of("message", "Table " + table + " truncated"));
    }

    @PostMapping("/alter/add/{table}")
    public ResponseEntity<Map<String, String>> addColumn(
            @PathVariable String table, @RequestBody Map<String, String> body) {
        advancedQueryService.addColumn(table, body.get("column"), body.get("type"));
        return ResponseEntity.ok(Map.of("message", "Column added"));
    }

    @PostMapping("/alter/drop/{table}")
    public ResponseEntity<Map<String, String>> dropColumn(
            @PathVariable String table, @RequestBody Map<String, String> body) {
        advancedQueryService.dropColumn(table, body.get("column"));
        return ResponseEntity.ok(Map.of("message", "Column dropped"));
    }

    @PostMapping("/alter/rename/{table}")
    public ResponseEntity<Map<String, String>> renameColumn(
            @PathVariable String table, @RequestBody Map<String, String> body) {
        advancedQueryService.renameColumn(table, body.get("oldName"), body.get("newName"));
        return ResponseEntity.ok(Map.of("message", "Column renamed"));
    }

    @PostMapping("/alter/comment/{table}")
    public ResponseEntity<Map<String, String>> alterComment(
            @PathVariable String table, @RequestBody Map<String, String> body) {
        advancedQueryService.alterTableComment(table, body.get("comment"));
        return ResponseEntity.ok(Map.of("message", "Comment set"));
    }

    @PostMapping("/delete-range/{table}")
    public ResponseEntity<Map<String, String>> deleteRange(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        advancedQueryService.deleteRange(table,
                (String) body.get("partitionKey"), body.get("partitionValue"),
                (String) body.get("clusteringColumn"), body.get("startValue"), body.get("endValue"));
        return ResponseEntity.ok(Map.of("message", "Range deleted"));
    }

    @PostMapping("/query/consistency/{level}")
    public ResponseEntity<List<Map<String, Object>>> queryWithConsistency(
            @PathVariable String level, @RequestBody Map<String, String> body) {
        var consistencyLevel = com.datastax.oss.driver.api.core.DefaultConsistencyLevel.valueOf(level);
        List<Row> rows = advancedQueryService.queryWithConsistency(body.get("cql"), consistencyLevel);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Row row : rows) {
            Map<String, Object> map = new HashMap<>();
            row.getColumnDefinitions().forEach(cd -> map.put(cd.getName().toString(), row.getObject(cd.getName())));
            results.add(map);
        }
        return ResponseEntity.ok(results);
    }
}
