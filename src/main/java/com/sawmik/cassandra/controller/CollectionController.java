package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/{table}/list/add")
    public ResponseEntity<Map<String, String>> addToList(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.addToList(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("listColumn"), (java.util.List<Object>) body.get("values"));
        return ResponseEntity.ok(Map.of("message", "Values added to list"));
    }

    @PostMapping("/{table}/list/remove")
    public ResponseEntity<Map<String, String>> removeFromList(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.removeFromList(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("listColumn"), (java.util.List<Object>) body.get("values"));
        return ResponseEntity.ok(Map.of("message", "Values removed from list"));
    }

    @PostMapping("/{table}/list/prepend")
    public ResponseEntity<Map<String, String>> prependToList(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.prependToList(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("listColumn"), (java.util.List<Object>) body.get("values"));
        return ResponseEntity.ok(Map.of("message", "Values prepended to list"));
    }

    @PostMapping("/{table}/set/add")
    public ResponseEntity<Map<String, String>> addToSet(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.addToSet(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("setColumn"), new java.util.HashSet<>((java.util.List<Object>) body.get("values")));
        return ResponseEntity.ok(Map.of("message", "Values added to set"));
    }

    @PostMapping("/{table}/set/remove")
    public ResponseEntity<Map<String, String>> removeFromSet(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.removeFromSet(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("setColumn"), new java.util.HashSet<>((java.util.List<Object>) body.get("values")));
        return ResponseEntity.ok(Map.of("message", "Values removed from set"));
    }

    @PostMapping("/{table}/map/put")
    public ResponseEntity<Map<String, String>> putToMap(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.putToMap(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("mapColumn"), (java.util.Map<Object, Object>) body.get("entries"));
        return ResponseEntity.ok(Map.of("message", "Entries added to map"));
    }

    @PostMapping("/{table}/map/remove")
    public ResponseEntity<Map<String, String>> removeFromMap(
            @PathVariable String table, @RequestBody Map<String, Object> body) {
        collectionService.removeFromMap(table,
                (String) body.get("idColumn"), body.get("idValue"),
                (String) body.get("mapColumn"), new java.util.HashSet<>((java.util.List<Object>) body.get("keys")));
        return ResponseEntity.ok(Map.of("message", "Keys removed from map"));
    }
}
