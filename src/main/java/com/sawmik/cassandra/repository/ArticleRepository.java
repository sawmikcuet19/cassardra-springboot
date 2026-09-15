package com.sawmik.cassandra.repository;

import com.sawmik.cassandra.entity.Article;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends CassandraRepository<Article, UUID> {

    List<Article> findByAuthor(String author);

    List<Article> findByTitle(String title);

    List<Article> findByPublished(Boolean published);

    List<Article> findByStatus(String status);

    @AllowFiltering
    List<Article> findByCategoriesContaining(String category);

    @AllowFiltering
    List<Article> findByTagsContaining(String tag);

    @AllowFiltering
    List<Article> findByPublishedAndStatus(Boolean published, String status);

    @Query("SELECT * FROM articles WHERE author = ?0 ALLOW FILTERING")
    List<Article> findByAuthorWithFilter(String author);

    @Query("SELECT * FROM articles WHERE title CONTAINS ?0 ALLOW FILTERING")
    List<Article> searchByTitle(String title);

    @Query("SELECT * FROM articles WHERE content CONTAINS ?0 ALLOW FILTERING")
    List<Article> searchByContent(String content);

    @Query("SELECT * FROM articles WHERE published = true AND status != 'deleted' ALLOW FILTERING")
    List<Article> findAllPublishedNotDeleted();

    @AllowFiltering
    long countByAuthor(String author);

    @AllowFiltering
    long countByStatus(String status);

    @AllowFiltering
    List<Article> findByPublishedAtBetween(Instant start, Instant end);
}
