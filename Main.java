import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public interface Cache<K, V> {
        public V get(K key);// Given a key, return the corresponding value

        public boolean exists(K key); // Does a value with key K exist? Return true/false

        public void clear(); // Remove all entries from cache

        public void clean(); // Remove all expired entries from cache

        public void put(K key, V value); // Puts an entry into cache
    }

    public static void main() {
        class CacheManager<K, V> implements Cache<K, V> {
            Long timeToLive = 50000L;
            ConcurrentHashMap<K, CacheObject<V>> cache = new ConcurrentHashMap<>();

            public V get(K key) {
                CacheObject<V> retrievedCacheObject = cache.get(key);

                if (retrievedCacheObject != null) {
                    return retrievedCacheObject.getValue();
                }

                return null;
            }

            public void put(K key, V value) {
                if (key != null) {
                    CacheObject<V> newCacheObject = new CacheObject<>(value, System.currentTimeMillis());
                    cache.put(key, newCacheObject);
                }
            }

            public boolean exists(K key) {
                CacheObject<V> cacheObject = cache.get(key);

                if (cacheObject != null) {
                    return true;
                }

                return false;
            }

            // Remove all cache objects
            public void remove(K key) {
                cache.remove(key);
            }

            // Remove all expired cache objects.
            public void clean() {
                for (K key : cache.keySet()) {
                    CacheObject<V> currentCacheObject = cache.get(key);
                    if (currentCacheObject.isExpired()) {
                        cache.remove(key);
                    }
                }
            }

            // Deletes all objects in the cache
            public void clear() {
                cache.clear();
            }

            class CacheObject<V> {
                private V value;
                private Long creationDate;

                CacheObject(V inputValue, Long inputCreationDate) {
                    value = inputValue;
                    creationDate = inputCreationDate;
                }

                V getValue() {
                    return value;
                }

                // Has the cache expired?
                boolean isExpired() {
                    return System.currentTimeMillis() > (getExpiryTime());
                }

                /**
                 * Get the expiry time for an entry at a given key
                 */
                Long getExpiryTime() {
                    return creationDate + timeToLive;
                }

            }
        }
    }
}
