package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.sawmik.cassandra.entity.MetricsCounter;
import com.sawmik.cassandra.repository.MetricsCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetricsCounterService {

    private final MetricsCounterRepository metricsCounterRepository;
    private final CqlSession cqlSession;

    public MetricsCounter getOrCreate(String name) {
        return metricsCounterRepository.findById(name)
                .orElseGet(() -> {
                    cqlSession.execute("UPDATE metrics_counters SET counter_value = counter_value + 0 WHERE name = '" + name + "'");
                    return metricsCounterRepository.findById(name).orElse(new MetricsCounter(name, 0L));
                });
    }

    public void increment(String name, long delta) {
        metricsCounterRepository.incrementCounter(name, delta);
    }

    public void decrement(String name, long delta) {
        metricsCounterRepository.decrementCounter(name, delta);
    }

    public Optional<MetricsCounter> getCounter(String name) {
        return metricsCounterRepository.findById(name);
    }

    public MetricsCounter save(MetricsCounter counter) {
        return metricsCounterRepository.save(counter);
    }

    public void delete(String name) {
        metricsCounterRepository.deleteById(name);
    }
}
