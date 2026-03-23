package com.nbodev.watteenbuurt.domain.snapshot;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HistoryBufferTest {

    private static final LocalDateTime BASE = LocalDateTime.of(2025, 1, 6, 0, 0);

    private TickSnapshot snapshot(int minuteOffset) {
        return new TickSnapshot(BASE.plusMinutes(minuteOffset), minuteOffset, 10.0, 0.5);
    }

    @Test
    void emptyBufferHasSizeZero() {
        HistoryBuffer buffer = new HistoryBuffer();
        assertThat(buffer.size()).isEqualTo(0);
        assertThat(buffer.getAll()).isEmpty();
    }

    @Test
    void singleAddIsRetrievable() {
        HistoryBuffer buffer = new HistoryBuffer();
        buffer.add(snapshot(0));

        assertThat(buffer.size()).isEqualTo(1);
        assertThat(buffer.getAll()).hasSize(1);
        assertThat(buffer.getAll().get(0).totalPowerKw()).isEqualTo(0.0);
    }

    @Test
    void snapshotsReturnedInChronologicalOrder() {
        HistoryBuffer buffer = new HistoryBuffer();
        for (int i = 0; i < 10; i++) {
            buffer.add(snapshot(i));
        }

        List<TickSnapshot> all = buffer.getAll();
        assertThat(all).hasSize(10);
        for (int i = 0; i < 10; i++) {
            assertThat(all.get(i).totalPowerKw()).isEqualTo(i);
        }
    }

    @Test
    void bufferCapacityIs1440() {
        HistoryBuffer buffer = new HistoryBuffer();
        for (int i = 0; i < 1440; i++) {
            buffer.add(snapshot(i));
        }
        assertThat(buffer.size()).isEqualTo(1440);
    }

    @Test
    void oldestSnapshotDroppedWhenCapacityExceeded() {
        HistoryBuffer buffer = new HistoryBuffer();

        // Fill to capacity
        for (int i = 0; i < 1440; i++) {
            buffer.add(snapshot(i));
        }
        // Add one more — minute 0 (power=0.0) should be evicted
        buffer.add(snapshot(1440));

        assertThat(buffer.size()).isEqualTo(1440);
        List<TickSnapshot> all = buffer.getAll();

        // Oldest should now be minute 1 (power=1.0)
        assertThat(all.get(0).totalPowerKw()).isEqualTo(1.0);
        // Newest should be minute 1440
        assertThat(all.get(1439).totalPowerKw()).isEqualTo(1440.0);
    }

    @Test
    void chronologicalOrderPreservedAfterWrap() {
        HistoryBuffer buffer = new HistoryBuffer();

        for (int i = 0; i < 1500; i++) {
            buffer.add(snapshot(i));
        }

        List<TickSnapshot> all = buffer.getAll();
        for (int i = 1; i < all.size(); i++) {
            assertThat(all.get(i).time()).isAfter(all.get(i - 1).time());
        }
    }
}
