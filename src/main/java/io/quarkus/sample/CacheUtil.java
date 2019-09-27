package io.quarkus.sample;

import io.quarkus.hibernate.orm.panache.Panache;
import org.hibernate.Session;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;

public class CacheUtil {

    private static Statistics getStatistics() {
        return Panache.getEntityManager()
            .unwrap(Session.class)
            .getSessionFactory()
            .getStatistics();
    }

    private static CacheRegionStatistics getEntityCacheStats(Class<?> entityType) {
        return getStatistics()
            .getDomainDataRegionStatistics(entityType.getName());
    }

    static String showEntityCacheStats(String method, Class<?> entityType) {
        final CacheRegionStatistics stats = getEntityCacheStats(entityType);
        return String.format("[%s] %s {cache hit: %-2d, cache miss: %-2d, cache put: %-2d}"
            , method
            , entityType.getSimpleName()
            , stats.getHitCount()
            , stats.getMissCount()
            , stats.getPutCount()
        );
    }

    static String showQueryCacheStats() {
        final Statistics stats = getStatistics();
        return String.format("query {cache hit: %-2d, cache miss: %-2d, cache put: %-2d}"
            , stats.getQueryCacheHitCount()
            , stats.getQueryCacheMissCount()
            , stats.getQueryCachePutCount()
        );
    }

}
