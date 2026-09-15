package com.sawmik.cassandra.service;

import com.sawmik.cassandra.dto.product.ProductRequest;
import com.sawmik.cassandra.entity.Product;
import com.sawmik.cassandra.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product save(ProductRequest request) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setTags(request.getTags());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        product.setReviews(request.getReviews());
        product.setSellerId(request.getSellerId());
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }

    public Product update(UUID id, ProductRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setTags(request.getTags());
        existing.setPrice(request.getPrice());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setAvailable(request.getAvailable());
        existing.setReviews(request.getReviews());
        existing.setSellerId(request.getSellerId());
        existing.setUpdatedAt(Instant.now());
        return productRepository.save(existing);
    }

    public Product patch(UUID id, Map<String, Object> updates) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        if (updates.containsKey("name")) existing.setName((String) updates.get("name"));
        if (updates.containsKey("description")) existing.setDescription((String) updates.get("description"));
        if (updates.containsKey("category")) existing.setCategory((String) updates.get("category"));
        if (updates.containsKey("price")) existing.setPrice(BigDecimal.valueOf(((Number) updates.get("price")).doubleValue()));
        if (updates.containsKey("stockQuantity")) existing.setStockQuantity((Integer) updates.get("stockQuantity"));
        if (updates.containsKey("available")) existing.setAvailable((Boolean) updates.get("available"));
        if (updates.containsKey("sellerId")) existing.setSellerId((String) updates.get("sellerId"));
        existing.setUpdatedAt(Instant.now());
        return productRepository.save(existing);
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    public void deleteAll() {
        productRepository.deleteAll();
    }

    public long count() {
        return productRepository.count();
    }

    public boolean existsById(UUID id) {
        return productRepository.existsById(id);
    }

    public List<Product> bulkSave(List<ProductRequest> requests) {
        return requests.stream().map(this::save).toList();
    }

    public void bulkDelete(List<UUID> ids) {
        productRepository.deleteAllById(ids);
    }

    // Search methods
    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByAvailable(Boolean available) {
        return productRepository.findByAvailable(available);
    }

    public List<Product> findByPriceBetween(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public List<Product> findByCategoryAndAvailable(String category, Boolean available) {
        return productRepository.findByCategoryAndAvailable(category, available);
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceRange(min, max);
    }

    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    public List<Product> findByTag(String tag) {
        return productRepository.findByTag(tag);
    }

    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    public long countByAvailable(Boolean available) {
        return productRepository.countByAvailable(available);
    }

    public List<Product> findByPriceLessThanEqual(BigDecimal price) {
        return productRepository.findByPriceLessThanEqual(price);
    }
}
