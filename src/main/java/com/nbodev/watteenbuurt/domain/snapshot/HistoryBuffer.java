package com.nbodev.watteenbuurt.domain.snapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Fixed-size ring buffer holding the last 24 simulated hours of tick snapshots.
 * 24h × 60 min = 1440 slots at 1-minute tick resolution.
 * <p>
 * Thread-safe: engine writes, REST layer reads concurrently.
 */
public class HistoryBuffer {

    private static final int CAPACITY = 24 * 60; // 1440 snapshots

    private final TickSnapshot[] buffer = new TickSnapshot[CAPACITY];
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int head = 0;   // next write position
    private int size = 0;

    public void add(TickSnapshot snapshot) {
        lock.writeLock().lock();
        try {
            buffer[head] = snapshot;
            head = (head + 1) % CAPACITY;
            if (size < CAPACITY) size++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns snapshots in chronological order (oldest first).
     */
    public List<TickSnapshot> getAll() {
        lock.readLock().lock();
        try {
            List<TickSnapshot> result = new ArrayList<>(size);
            int start = size < CAPACITY ? 0 : head;
            for (int i = 0; i < size; i++) {
                result.add(buffer[(start + i) % CAPACITY]);
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return size;
        } finally {
            lock.readLock().unlock();
        }
    }
}