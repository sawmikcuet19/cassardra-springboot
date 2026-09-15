package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.Product;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends CassandraRepository<Product, UUID> {

    List<Product> findByName(String name);

    List<Product> findByCategory(String category);

    @AllowFiltering
    List<Product> findByAvailable(Boolean available);

    @AllowFiltering
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @AllowFiltering
    List<Product> findByCategoryAndAvailable(String category, Boolean available);

    @AllowFiltering
    List<Product> findByPriceLessThanEqual(BigDecimal price);

    @Query("SELECT * FROM products WHERE category = ?0 ALLOW FILTERING")
    List<Product> findByCategoryWithFilter(String category);

    @Query("SELECT * FROM products WHERE name CONTAINS ?0 ALLOW FILTERING")
    List<Product> searchByName(String name);

    @Query("SELECT * FROM products WHERE tags CONTAINS ?0 ALLOW FILTERING")
    List<Product> findByTag(String tag);

    @Query("SELECT * FROM products WHERE price >= ?0 AND price <= ?1 ALLOW FILTERING")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);

    @AllowFiltering
    long countByCategory(String category);

    @AllowFiltering
    long countByAvailable(Boolean available);
}
