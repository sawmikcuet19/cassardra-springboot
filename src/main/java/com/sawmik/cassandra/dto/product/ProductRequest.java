package com.sawmik.cassandra.dto.product;

import com.sawmik.cassandra.udt.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String name;
    private String description;
    private String category;
    private List<String> tags;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean available;
    private List<Review> reviews;
    private String sellerId;
}
