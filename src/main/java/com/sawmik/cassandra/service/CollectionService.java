package com.sawmik.cassandra.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectionService {

    private final CqlSession cqlSession;

    private Object parseIdValue(Object idValue) {
        if (idValue instanceof String s) {
            try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return s; }
        }
        return idValue;
    }

    public void addToList(String table, String idColumn, Object idValue,
                          String listColumn, List<Object> valuesToAdd) {
        String cql = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ?",
                table, listColumn, listColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, valuesToAdd, parseIdValue(idValue)));
        log.info("Added {} values to {} in {}", valuesToAdd.size(), listColumn, table);
    }

    public void removeFromList(String table, String idColumn, Object idValue,
                               String listColumn, List<Object> valuesToRemove) {
        String cql = String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?",
                table, listColumn, listColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, valuesToRemove, parseIdValue(idValue)));
        log.info("Removed {} values from {} in {}", valuesToRemove.size(), listColumn, table);
    }

    public void prependToList(String table, String idColumn, Object idValue,
                              String listColumn, List<Object> valuesToPrepend) {
        String cql = String.format("UPDATE %s SET %s = ? + %s WHERE %s = ?",
                table, listColumn, listColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, valuesToPrepend, parseIdValue(idValue)));
        log.info("Prepended {} values to {} in {}", valuesToPrepend.size(), listColumn, table);
    }

    public void addToSet(String table, String idColumn, Object idValue,
                         String setColumn, Set<Object> valuesToAdd) {
        String cql = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ?",
                table, setColumn, setColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, valuesToAdd, parseIdValue(idValue)));
        log.info("Added {} values to set {} in {}", valuesToAdd.size(), setColumn, table);
    }

    public void removeFromSet(String table, String idColumn, Object idValue,
                              String setColumn, Set<Object> valuesToRemove) {
        String cql = String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?",
                table, setColumn, setColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, valuesToRemove, parseIdValue(idValue)));
        log.info("Removed {} values from set {} in {}", valuesToRemove.size(), setColumn, table);
    }

    public void putToMap(String table, String idColumn, Object idValue,
                         String mapColumn, Map<Object, Object> entriesToAdd) {
        String cql = String.format("UPDATE %s SET %s = %s + ? WHERE %s = ?",
                table, mapColumn, mapColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, entriesToAdd, parseIdValue(idValue)));
        log.info("Added {} entries to map {} in {}", entriesToAdd.size(), mapColumn, table);
    }

    public void removeFromMap(String table, String idColumn, Object idValue,
                              String mapColumn, Set<Object> keysToRemove) {
        String cql = String.format("UPDATE %s SET %s = %s - ? WHERE %s = ?",
                table, mapColumn, mapColumn, idColumn);
        cqlSession.execute(SimpleStatement.newInstance(cql, keysToRemove, parseIdValue(idValue)));
        log.info("Removed {} keys from map {} in {}", keysToRemove.size(), mapColumn, table);
    }
}
