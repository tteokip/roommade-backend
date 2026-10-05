package com.roommade.domain.house.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 만료 시간이 지난 항목은 다음 조회 시점에 자연스럽게 다시 계산되고(지연 만료), 저장 개수가
 * maxSize를 넘으면 가장 오래 전에 넣은 항목부터 제거한다(LinkedHashMap의 삽입 순서 기반).
 * 재시작하면 캐시는 비워진다 — 별도 영속화나 분산 캐시(Redis 등)는 이 최소 구현의 범위 밖이다.
 */
final class TtlCache<K, V> {

    private final long ttlMillis;
    private final Map<K, Entry<V>> store;

    TtlCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.store = new LinkedHashMap<>(16, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, Entry<V>> eldest) {
                return size() > maxSize;
            }
        };
    }

    V getOrCompute(K key, Supplier<V> loader) {
        long now = System.currentTimeMillis();
        synchronized (store) {
            Entry<V> cached = store.get(key);
            if (cached != null && !cached.isExpired(now)) {
                return cached.value;
            }
        }

        // 외부 API 호출 중에는 잠금을 해제해 다른 키의 조회가 함께 진행될 수 있게 한다.
        V value = loader.get();

        synchronized (store) {
            store.put(key, new Entry<>(value, now + ttlMillis));
        }
        return value;
    }

    private static final class Entry<V> {
        private final V value;
        private final long expiresAtMillis;

        private Entry(V value, long expiresAtMillis) {
            this.value = value;
            this.expiresAtMillis = expiresAtMillis;
        }

        private boolean isExpired(long nowMillis) {
            return nowMillis >= expiresAtMillis;
        }
    }
}
