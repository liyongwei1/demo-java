package cn.liyongwei.utils;

import java.util.Map;
import java.util.concurrent.*;

public class LocalCacheUtil {

    //本地缓存定期清理时间
    private static final long DEFAULT_GC_TIME = 5;
    private static final TimeUnit DEFAULT_GC_UNIT = TimeUnit.MINUTES;

    private static final ScheduledExecutorService refreshExecutor = new ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());

    private static volatile ConcurrentHashMap<String, CacheElement<Object>> cacheMap = new ConcurrentHashMap<>();

    /**
     * 读取本地缓存
     *
     * @param key          缓存key
     * @param requireClass 缓存值对应class
     */
    public static <T> T get(String key, Class<T> requireClass) {
        CacheElement<Object> e = cacheMap.get(key);
        //过期返回null
        if (null == e || e.isOvertime()) {
            return null;
        }
        return (T) e.value;
    }

    /**
     * 保存至本地缓存
     *
     * @param key           缓存key
     * @param value         缓存值
     * @param timeoutMillis 过期时间。单位：ms
     */
    public static <T> void set(String key, T value, long timeoutMillis) {
        cacheMap.put(key, new CacheElement<>(value, timeoutMillis));
    }

    /**
     * 手动清除key对应的本地缓存
     */
    public static void remove(String key) {
        cacheMap.remove(key);
    }

    /**
     * 注册缓存清理任务
     */
    static {
        refreshExecutor.scheduleAtFixedRate(LocalCacheUtil::startGC, 0, DEFAULT_GC_TIME, DEFAULT_GC_UNIT);
    }

    private static void startGC() {
        ConcurrentHashMap<String, CacheElement<Object>> newCache = new ConcurrentHashMap<>();
        for (Map.Entry<String, CacheElement<Object>> e : cacheMap.entrySet()) {
            // 丢弃已经过期的数据
            if (e.getValue().isOvertime()) {
                continue;
            }
            newCache.put(e.getKey(), e.getValue());
        }
        cacheMap = newCache;
    }

    // 缓存对象
    static class CacheElement<T> {

        // 缓存值
        private final T value;

        // 过期时间
        private final Long expire;

        /**
         * 有参数构造方法
         *
         * @param value         缓存的值
         * @param timeoutMillis 过期时间。单位：ms
         */
        public CacheElement(T value, long timeoutMillis) {
            this.value = value;
            this.expire = System.currentTimeMillis() + timeoutMillis;
        }

        /**
         * 判断缓存是否过期
         */
        public boolean isOvertime() {
            return expire < System.currentTimeMillis();
        }
    }

    private LocalCacheUtil() {
    }
}
