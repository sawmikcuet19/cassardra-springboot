package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.TtlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ttl")
@RequiredArgsConstructor
public class TtlController {

    private final TtlService ttlService;

    @PostMapping("/{table}")
    public ResponseEntity<Map<String, String>> insertWithTtl(
            @PathVariable String table,
            @RequestBody Map<String, Object> body) {
        int ttlSeconds = (int) body.get("ttl");
        Map<String, Object> values = (Map<String, Object>) body.get("values");
        ttlService.insertWithTtl(table, values, ttlSeconds);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inserted with TTL " + ttlSeconds + "s"));
    }

    @GetMapping("/{table}")
    public ResponseEntity<List<Map<String, Object>>> getAllWithTtl(@PathVariable String table) {
        return ResponseEntity.ok(ttlService.findAllWithTtl(table));
    }

    @PutMapping("/{table}/{id}")
    public ResponseEntity<Map<String, String>> updateTtl(
            @PathVariable String table,
            @PathVariable String id,
            @RequestParam int ttlSeconds) {
        ttlService.updateTtl(table, "id", id, ttlSeconds);
        return ResponseEntity.ok(Map.of("message", "TTL updated to " + ttlSeconds + "s"));
    }
}
