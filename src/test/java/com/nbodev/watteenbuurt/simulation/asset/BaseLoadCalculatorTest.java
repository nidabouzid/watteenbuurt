package com.nbodev.watteenbuurt.simulation.asset;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class BaseLoadCalculatorTest {

    // Seed 42 gives stable, deterministic output
    private final BaseLoadCalculator calc = new BaseLoadCalculator(new Random(42));

    private LocalDateTime at(int hour, int minute) {
        return LocalDateTime.of(2025, 1, 6, hour, minute);
    }

    @Test
    void outputIsAlwaysPositive() {
        Random rng = new Random(0);
        BaseLoadCalculator c = new BaseLoadCalculator(rng);
        for (int hour = 0; hour < 24; hour++) {
            assertThat(c.compute(at(hour, 0))).isGreaterThan(0.0);
        }
    }

    @Test
    void outputIsNeverBelowMinimum() {
        BaseLoadCalculator c = new BaseLoadCalculator(new Random(99));
        for (int hour = 0; hour < 24; hour++) {
            assertThat(c.compute(at(hour, 0))).isGreaterThanOrEqualTo(0.1);
        }
    }

    @Test
    void morningPeakIsHigherThanMidnightBaseline() {
        // Use a seeded RNG: compare many samples at morning peak vs midnight
        double morningAvg = average(new BaseLoadCalculator(new Random(1)), at(7, 30), 100);
        double midnightAvg = average(new BaseLoadCalculator(new Random(1)), at(0, 0), 100);

        assertThat(morningAvg).isGreaterThan(midnightAvg);
    }

    @Test
    void eveningPeakIsHigherThanMidnightBaseline() {
        double eveningAvg = average(new BaseLoadCalculator(new Random(2)), at(19, 0), 100);
        double midnightAvg = average(new BaseLoadCalculator(new Random(2)), at(0, 0), 100);

        assertThat(eveningAvg).isGreaterThan(midnightAvg);
    }

    @Test
    void outputIsDeterministicWithSameSeed() {
        LocalDateTime time = at(12, 0);
        double first = new BaseLoadCalculator(new Random(42)).compute(time);
        double second = new BaseLoadCalculator(new Random(42)).compute(time);

        assertThat(first).isEqualTo(second);
    }

    private double average(BaseLoadCalculator c, LocalDateTime time, int samples) {
        double sum = 0;
        for (int i = 0; i < samples; i++) {
            sum += c.compute(time);
        }
        return sum / samples;
    }
}
