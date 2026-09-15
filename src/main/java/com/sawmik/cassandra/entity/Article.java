package com.sawmik.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Indexed;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table("articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @PrimaryKey
    @Id
    private UUID id;

    @Column("title")
    @Indexed
    private String title;

    @Column("content")
    private String content;

    @Column("author")
    @Indexed
    private String author;

    @Column("categories")
    private List<String> categories;

    @Column("tags")
    private Set<String> tags;

    @Column("view_count")
    private Long viewCount;

    @Column("published")
    @Indexed
    private Boolean published;

    @Column("status")
    @Indexed
    private String status;

    @Column("published_at")
    private Instant publishedAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
