package com.nbodev.watteenbuurt.simulation.asset;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Base household consumption model.
 * <p>
 * Shape: sinusoidal daily curve with two peaks (morning + evening).
 * Range: 0.3 kW (night baseline) to ~3.5 kW (evening peak).
 * Noise: ±10% per tick to simulate appliance switching.
 */
public class BaseLoadCalculator {

    private static final double BASE_KW = 0.4;
    private static final double MORNING_KW = 1.5;
    private static final double EVENING_KW = 2.2;
    private static final double NOISE = 0.10;

    private final Random rng;

    public BaseLoadCalculator(Random rng) {
        this.rng = rng;
    }

    public double compute(LocalDateTime time) {
        double hour = time.getHour() + time.getMinute() / 60.0;

        double morningPeak = MORNING_KW * gaussianPeak(hour, 7.5, 1.5);
        double eveningPeak = EVENING_KW * gaussianPeak(hour, 19.0, 2.0);
        double load = BASE_KW + morningPeak + eveningPeak;

        // ±10% random noise
        double noise = 1.0 + (rng.nextDouble() - 0.5) * 2 * NOISE;
        return Math.max(0.1, load * noise);
    }

    /**
     * Gaussian bell curve centred at `mean` with given `sigma`.
     */
    private double gaussianPeak(double x, double mean, double sigma) {
        return Math.exp(-0.5 * Math.pow((x - mean) / sigma, 2));
    }
}