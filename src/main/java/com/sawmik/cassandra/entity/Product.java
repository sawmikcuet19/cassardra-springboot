package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Indexed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table("products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("name")
    @Indexed
    private String name;

    @Column("description")
    private String description;

    @Column("category")
    @Indexed
    private String category;

    @Column("tags")
    private List<String> tags;

    @Column("price")
    @Indexed
    private BigDecimal price;

    @Column("stock_quantity")
    private Integer stockQuantity;

    @Column("available")
    @Indexed
    private Boolean available;

    @Column("reviews")
    private List<com.sawmik.cassandra.udt.Review> reviews;

    @Column("seller_id")
    private String sellerId;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
