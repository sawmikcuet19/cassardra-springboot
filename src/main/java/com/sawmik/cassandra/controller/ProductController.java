package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.dto.product.ProductRequest;
import com.sawmik.cassandra.entity.Product;
import com.sawmik.cassandra.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(request));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable UUID id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> patch(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(productService.patch(id, updates));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        productService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("count", productService.count()));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Product>> bulkCreate(@RequestBody List<ProductRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.bulkSave(requests));
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody List<UUID> ids) {
        productService.bulkDelete(ids);
        return ResponseEntity.noContent().build();
    }

    // --- Search endpoints ---

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Product>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @GetMapping("/search/category/{category}")
    public ResponseEntity<List<Product>> searchByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    @GetMapping("/search/price")
    public ResponseEntity<List<Product>> searchByPriceRange(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productService.findByPriceRange(min, max));
    }

    @GetMapping("/search/available/{available}")
    public ResponseEntity<List<Product>> searchByAvailable(@PathVariable Boolean available) {
        return ResponseEntity.ok(productService.findByAvailable(available));
    }

    @GetMapping("/search/tag/{tag}")
    public ResponseEntity<List<Product>> searchByTag(@PathVariable String tag) {
        return ResponseEntity.ok(productService.findByTag(tag));
    }

    @GetMapping("/search/category/{category}/available/{available}")
    public ResponseEntity<List<Product>> searchByCategoryAndAvailable(
            @PathVariable String category, @PathVariable Boolean available) {
        return ResponseEntity.ok(productService.findByCategoryAndAvailable(category, available));
    }

    @GetMapping("/search/price-less-than/{price}")
    public ResponseEntity<List<Product>> searchByPriceLessThan(@PathVariable BigDecimal price) {
        return ResponseEntity.ok(productService.findByPriceLessThanEqual(price));
    }

    @GetMapping("/count/category/{category}")
    public ResponseEntity<Map<String, Long>> countByCategory(@PathVariable String category) {
        return ResponseEntity.ok(Map.of("count", productService.countByCategory(category)));
    }

    @GetMapping("/count/available/{available}")
    public ResponseEntity<Map<String, Long>> countByAvailable(@PathVariable Boolean available) {
        return ResponseEntity.ok(Map.of("count", productService.countByAvailable(available)));
    }
}
