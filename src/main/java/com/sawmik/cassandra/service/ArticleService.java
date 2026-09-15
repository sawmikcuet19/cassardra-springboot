package com.sawmik.cassandra.service;

import com.sawmik.cassandra.entity.Article;
import com.sawmik.cassandra.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article save(Article article) {
        if (article.getId() == null) {
            article.setId(UUID.randomUUID());
        }
        if (article.getCreatedAt() == null) {
            article.setCreatedAt(Instant.now());
        }
        article.setUpdatedAt(Instant.now());
        if (article.getViewCount() == null) {
            article.setViewCount(0L);
        }
        if (article.getPublished() == null) {
            article.setPublished(false);
        }
        if (article.getStatus() == null) {
            article.setStatus("draft");
        }
        return articleRepository.save(article);
    }

    public Article update(UUID id, Article updated) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found: " + id));
        existing.setTitle(updated.getTitle());
        existing.setContent(updated.getContent());
        existing.setAuthor(updated.getAuthor());
        existing.setCategories(updated.getCategories());
        existing.setTags(updated.getTags());
        existing.setPublished(updated.getPublished());
        existing.setStatus(updated.getStatus());
        existing.setPublishedAt(updated.getPublishedAt());
        existing.setUpdatedAt(Instant.now());
        return articleRepository.save(existing);
    }

    public Optional<Article> findById(UUID id) {
        return articleRepository.findById(id);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public void deleteById(UUID id) {
        articleRepository.deleteById(id);
    }

    public void deleteAll() {
        articleRepository.deleteAll();
    }

    public long count() {
        return articleRepository.count();
    }

    public List<Article> bulkSave(List<Article> articles) {
        articles.forEach(a -> {
            if (a.getId() == null) a.setId(UUID.randomUUID());
            if (a.getCreatedAt() == null) a.setCreatedAt(Instant.now());
            a.setUpdatedAt(Instant.now());
            if (a.getViewCount() == null) a.setViewCount(0L);
        });
        return articleRepository.saveAll(articles).stream().toList();
    }

    // Search
    public List<Article> findByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }

    public List<Article> findByStatus(String status) {
        return articleRepository.findByStatus(status);
    }

    public List<Article> findByPublished(Boolean published) {
        return articleRepository.findByPublished(published);
    }

    public List<Article> searchByTitle(String title) {
        return articleRepository.searchByTitle(title);
    }

    public List<Article> searchByContent(String content) {
        return articleRepository.searchByContent(content);
    }

    public List<Article> findAllPublishedNotDeleted() {
        return articleRepository.findAllPublishedNotDeleted();
    }

    public List<Article> findByPublishedAtBetween(Instant start, Instant end) {
        return articleRepository.findByPublishedAtBetween(start, end);
    }

    public long countByAuthor(String author) {
        return articleRepository.countByAuthor(author);
    }

    public long countByStatus(String status) {
        return articleRepository.countByStatus(status);
    }

    public Article incrementViewCount(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found: " + id));
        article.setViewCount(article.getViewCount() + 1);
        return articleRepository.save(article);
    }
}
