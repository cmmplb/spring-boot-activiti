package io.github.cmmplb.activiti.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存缓存
 * @author <a href="https://blog.csdn.net/weixin_43652507/article/details/126934347">Java 使用内存缓存数据、设置失效时间</a>
 */
public class TimeExpiredPoolCacheUtil {

    // 过期时间默认10分钟
    private static final long DEFAULT_CACHED_MILLIS = 10 * 60 * 1000L;
    // 定时清理默认10分钟
    private static final long TIMER_MILLIS = 10 * 60 * 1000L;

    /**
     * 定时器定时清理过期缓存
     */
    private static final Timer TIMER = new Timer();

    /**
     * 对象池
     */
    private static ConcurrentHashMap<String, DataWrapper<?>> dataPool = null;

    /**
     * 对象单例
     */
    private static TimeExpiredPoolCacheUtil instance = null;

    private TimeExpiredPoolCacheUtil() {
        dataPool = new ConcurrentHashMap<>();
    }

    private static synchronized void syncInit() {
        if (instance == null) {
            instance = new TimeExpiredPoolCacheUtil();
            initTimer();
        }
    }

    public static TimeExpiredPoolCacheUtil getInstance() {
        if (instance == null) {
            syncInit();
        }
        return instance;
    }

    private static void initTimer() {
        TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    clearExpiredCaches();
                } catch (Exception e) {
                    //logger.error("clearExpiredCaches error.", e);
                }
            }
        }, TIMER_MILLIS, TIMER_MILLIS);
    }

    /**
     * 缓存数据
     * @param key          key值
     * @param data         缓存数据
     * @param cachedMillis 过期时间
     * @param dataReNewer  刷新数据
     */
    @SuppressWarnings("unchecked")
    public <T> T put(String key, T data, long cachedMillis, DataReNewer<T> dataReNewer) throws Exception {
        DataWrapper<T> dataWrapper = (DataWrapper<T>) dataPool.get(key);
        if (data == null && dataReNewer != null) {
            data = dataReNewer.reNewData();
        }
        //当重新获取数据为空，直接返回不做put
        if (data == null) {
            return null;
        }
        if (dataWrapper != null) {
            //更新
            dataWrapper.update(data, cachedMillis);
        } else {
            dataWrapper = new DataWrapper<>(data, cachedMillis);
            dataPool.put(key, dataWrapper);
        }
        return data;
    }

    /**
     * 设置缓存值和时间
     */
    @SuppressWarnings("unchecked")
    public <T> T put(String key, T data, long cachedMillis) throws Exception {
        DataWrapper<T> dataWrapper = (DataWrapper<T>) dataPool.get(key);
        if (dataWrapper != null) {
            //更新
            dataWrapper.update(data, cachedMillis);
        } else {
            dataWrapper = new DataWrapper<>(data, cachedMillis);
            dataPool.put(key, dataWrapper);
        }
        return data;
    }

    /**
     * 默认构造时间的缓存数据
     */
    @Deprecated
    public <T> T put(String key, T data, DataReNewer<T> dataRenewer) throws Exception {
        return put(key, data, DEFAULT_CACHED_MILLIS, dataRenewer);
    }

    /**
     * 获取缓存
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, long cachedMillis, DataReNewer<T> dataReNewer) throws Exception {
        DataWrapper<T> dataWrapper = (DataWrapper<T>) dataPool.get(key);
        if (dataWrapper != null && !dataWrapper.isExpired()) {
            return dataWrapper.data;
        }
        return put(key, null, cachedMillis, dataReNewer);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws Exception {
        DataWrapper<T> dataWrapper = (DataWrapper<T>) dataPool.get(key);
        if (dataWrapper != null && !dataWrapper.isExpired()) {
            return dataWrapper.data;
        }
        return null;
    }

    /**
     * 清除缓存
     */
    public void clear() {
        dataPool.clear();
    }

    /**
     * 删除指定key的value
     */
    public void remove(String key) {
        dataPool.remove(key);
    }

    /**
     * 数据封装
     */
    private static class DataWrapper<T> {
        /**
         * 数据
         */
        private T data;
        /**
         * 到期时间
         */
        private long expiredTime;
        /**
         * 缓存时间
         */
        private long cachedMillis;

        private DataWrapper(T data, long cachedMillis) {
            this.update(data, cachedMillis);
        }

        public void update(T data, long cachedMillis) {
            this.data = data;
            this.cachedMillis = cachedMillis;
            this.updateExpiredTime();
        }

        public void updateExpiredTime() {
            this.expiredTime = System.currentTimeMillis() + cachedMillis;
        }

        /**
         * 数据是否过期
         */
        public boolean isExpired() {
            if (this.expiredTime > 0) {
                return System.currentTimeMillis() > this.expiredTime;
            }
            return true;
        }
    }

    /**
     * 数据构造
     */
    public interface DataReNewer<T> {
        T reNewData();
    }

    /**
     * 清除过期的缓存
     */
    private static void clearExpiredCaches() {
        List<String> expiredKeyList = new LinkedList<>();

        for (Entry<String, DataWrapper<?>> entry : dataPool.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredKeyList.add(entry.getKey());
            }
        }
        for (String key : expiredKeyList) {
            dataPool.remove(key);
        }
    }
}