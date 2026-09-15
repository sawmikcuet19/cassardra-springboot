package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/insert-if-not-exists/{table}")
    public ResponseEntity<Map<String, Object>> insertIfNotExists(
            @PathVariable String table,
            @RequestBody Map<String, Object> body) {
        String keyColumn = (String) body.get("keyColumn");
        Object keyValue = body.get("keyValue");
        String valueColumn = (String) body.get("valueColumn");
        Object valueValue = body.get("valueValue");
        Optional<UUID> result = transactionService.insertIfNotExists(table, keyColumn, keyValue, valueColumn, valueValue);
        return ResponseEntity.ok(Map.of("applied", result.isPresent(), "id", result.orElse(null)));
    }

    @PutMapping("/compare-and-set/{table}")
    public ResponseEntity<Map<String, Boolean>> compareAndSet(
            @PathVariable String table,
            @RequestBody Map<String, Object> body) {
        String keyColumn = (String) body.get("keyColumn");
        Object keyValue = body.get("keyValue");
        String valueColumn = (String) body.get("valueColumn");
        Object expectedValue = body.get("expectedValue");
        Object newValue = body.get("newValue");
        boolean applied = transactionService.compareAndSet(table, keyColumn, keyValue, valueColumn, expectedValue, newValue);
        return ResponseEntity.ok(Map.of("applied", applied));
    }

    @DeleteMapping("/delete-if-exists/{table}/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteIfExists(
            @PathVariable String table, @PathVariable String id) {
        boolean applied = transactionService.deleteIfExists(table, "id", id);
        return ResponseEntity.ok(Map.of("applied", applied));
    }
}
