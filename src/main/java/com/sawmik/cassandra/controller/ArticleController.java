package com.sawmik.cassandra.controller;

import com.sawmik.cassandra.entity.Article;
import com.sawmik.cassandra.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<Article> create(@RequestBody Article article) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.save(article));
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> update(@PathVariable UUID id, @RequestBody Article article) {
        return ResponseEntity.ok(articleService.update(id, article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        articleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("count", articleService.count()));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Article>> bulkCreate(@RequestBody List<Article> articles) {
        return ResponseEntity.status(HttpStatus.CREATED).body(articleService.bulkSave(articles));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> getByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(articleService.findByAuthor(author));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Article>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(articleService.findByStatus(status));
    }

    @GetMapping("/published/{published}")
    public ResponseEntity<List<Article>> getByPublished(@PathVariable Boolean published) {
        return ResponseEntity.ok(articleService.findByPublished(published));
    }

    @GetMapping("/published-not-deleted")
    public ResponseEntity<List<Article>> getAllPublishedNotDeleted() {
        return ResponseEntity.ok(articleService.findAllPublishedNotDeleted());
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<List<Article>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(articleService.searchByTitle(title));
    }

    @GetMapping("/search/content/{content}")
    public ResponseEntity<List<Article>> searchByContent(@PathVariable String content) {
        return ResponseEntity.ok(articleService.searchByContent(content));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Article>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(articleService.findByPublishedAtBetween(start, end));
    }

    @GetMapping("/count/author/{author}")
    public ResponseEntity<Map<String, Long>> countByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(Map.of("count", articleService.countByAuthor(author)));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<Map<String, Long>> countByStatus(@PathVariable String status) {
        return ResponseEntity.ok(Map.of("count", articleService.countByStatus(status)));
    }

    @PatchMapping("/{id}/view")
    public ResponseEntity<Article> incrementViewCount(@PathVariable UUID id) {
        return ResponseEntity.ok(articleService.incrementViewCount(id));
    }
}
