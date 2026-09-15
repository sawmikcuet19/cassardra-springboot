package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping("/products")
    public ResponseEntity<Map<String, String>> batchInsertProducts(@RequestBody List<Map<String, Object>> products) {
        batchService.batchInsertProducts(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Batch products inserted"));
    }

    @PostMapping("/logs")
    public ResponseEntity<Map<String, String>> batchInsertLogs(@RequestBody List<Map<String, Object>> logEntries) {
        batchService.batchInsertLogEntries(logEntries);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Batch log entries inserted"));
    }
}
