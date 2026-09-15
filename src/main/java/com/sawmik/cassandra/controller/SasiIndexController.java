package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.SasiIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/sasi")
@RequiredArgsConstructor
public class SasiIndexController {

    private final SasiIndexService sasiIndexService;

    @PostMapping("/{keyspace}/{table}")
    public ResponseEntity<Map<String, String>> createSasiIndex(
            @PathVariable String keyspace, @PathVariable String table,
            @RequestBody Map<String, String> body) {
        sasiIndexService.createSasiIndex(keyspace, table, body.get("column"));
        return ResponseEntity.ok(Map.of("message", "SASI index created"));
    }

    @PostMapping("/{keyspace}/{table}/prefix")
    public ResponseEntity<Map<String, String>> createSasiPrefixIndex(
            @PathVariable String keyspace, @PathVariable String table,
            @RequestBody Map<String, String> body) {
        sasiIndexService.createSasiPrefixIndex(keyspace, table, body.get("column"));
        return ResponseEntity.ok(Map.of("message", "SASI PREFIX index created"));
    }

    @PostMapping("/{keyspace}/{table}/sparse")
    public ResponseEntity<Map<String, String>> createSasiSparseIndex(
            @PathVariable String keyspace, @PathVariable String table,
            @RequestBody Map<String, String> body) {
        sasiIndexService.createSasiSparseIndex(keyspace, table, body.get("column"));
        return ResponseEntity.ok(Map.of("message", "SASI SPARSE index created"));
    }

    @DeleteMapping("/{keyspace}/{indexName}")
    public ResponseEntity<Map<String, String>> dropIndex(
            @PathVariable String keyspace, @PathVariable String indexName) {
        sasiIndexService.dropIndex(keyspace, indexName);
        return ResponseEntity.ok(Map.of("message", "Index dropped"));
    }
}
