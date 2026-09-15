package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.entity.MetricsCounter;
import com.sawmik.cassandra.service.MetricsCounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsCounterController {

    private final MetricsCounterService metricsCounterService;

    @PostMapping
    public ResponseEntity<MetricsCounter> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        long initialValue = body.get("initialValue") != null ? ((Number) body.get("initialValue")).longValue() : 1L;
        metricsCounterService.increment(name, initialValue);
        return ResponseEntity.status(HttpStatus.CREATED).body(metricsCounterService.getOrCreate(name));
    }

    @GetMapping("/{name}")
    public ResponseEntity<MetricsCounter> get(@PathVariable String name) {
        return ResponseEntity.ok(metricsCounterService.getOrCreate(name));
    }

    @PatchMapping("/{name}/increment")
    public ResponseEntity<Void> increment(@PathVariable String name,
                                          @RequestParam(defaultValue = "1") long delta) {
        metricsCounterService.increment(name, delta);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{name}/decrement")
    public ResponseEntity<Void> decrement(@PathVariable String name,
                                          @RequestParam(defaultValue = "1") long delta) {
        metricsCounterService.decrement(name, delta);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        metricsCounterService.delete(name);
        return ResponseEntity.noContent().build();
    }
}
