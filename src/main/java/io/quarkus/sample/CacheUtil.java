package io.quarkus.sample;

import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;

public class CacheUtil {

    private static Statistics getStatistics() {
        return null;
    }

    private static CacheRegionStatistics getEntityCacheStats(Class<?> entityType) {
        return null;
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
