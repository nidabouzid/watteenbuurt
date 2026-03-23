package com.nbodev.watteenbuurt.domain.asset;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Accumulates energy (kWh) for a single asset since simulation start.
 * Thread-safe via AtomicLong (stores microwatt-hours to avoid floating-point drift).
 */
public class EnergyMeter {

    private static final long SCALE = 1_000_000L; // store as micro-kWh

    private final AtomicLong accumulatedMicroKwh = new AtomicLong(0);

    /**
     * Add energy produced/consumed during one simulation tick.
     *
     * @param powerKw       current power in kW (positive = consumption, negative = generation/export)
     * @param durationHours tick duration in hours (e.g. 1 min = 1.0/60)
     */
    public void addEnergy(double powerKw, double durationHours) {
        long microKwh = Math.round(powerKw * durationHours * SCALE);
        accumulatedMicroKwh.addAndGet(microKwh);
    }

    /**
     * Returns total accumulated energy in kWh (may be negative for net generators).
     */
    public double getTotalKwh() {
        return accumulatedMicroKwh.get() / (double) SCALE;
    }

    public void reset() {
        accumulatedMicroKwh.set(0);
    }
}