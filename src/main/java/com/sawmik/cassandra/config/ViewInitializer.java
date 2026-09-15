package com.sawmik.cassandra.config;

import com.sawmik.cassandra.service.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewInitializer implements CommandLineRunner {

    private final SchemaService schemaService;

    @Override
    public void run(String... args) {
        log.info("=== Creating Materialized Views ===");

        schemaService.createMaterializedView("cassandra_app",
                "products_by_category",
                "id, name, category, price, available",
                "products",
                "category IS NOT NULL AND id IS NOT NULL",
                "(category, id)"
        );

        schemaService.createMaterializedView("cassandra_app",
                "articles_by_author",
                "id, title, author, published, status, created_at",
                "articles",
                "author IS NOT NULL AND id IS NOT NULL",
                "(author, id)"
        );

        schemaService.createMaterializedView("cassandra_app",
                "logs_by_level",
                "id, level, service, message, timestamp",
                "log_entries",
                "level IS NOT NULL AND id IS NOT NULL",
                "(level, id)"
        );

        schemaService.createMaterializedView("cassandra_app",
                "locations_by_country",
                "id, name, country, city, type",
                "locations",
                "country IS NOT NULL AND id IS NOT NULL",
                "(country, id)"
        );

        schemaService.createMaterializedView("cassandra_app",
                "employees_by_department",
                "id, name, email, department",
                "employees",
                "department IS NOT NULL AND id IS NOT NULL",
                "(department, id)"
        );

        schemaService.createMaterializedView("cassandra_app",
                "events_by_type",
                "id, event_type, event_name, event_time, source",
                "events",
                "event_type IS NOT NULL AND id IS NOT NULL",
                "(event_type, id)"
        );

        log.info("=== Materialized Views Complete ===");
    }
}
