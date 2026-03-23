package com.nbodev.watteenbuurt.simulation;

import java.time.LocalDateTime;

/**
 * Controls simulated time progression.
 * <p>
 * Step size: 1 simulated minute per tick.
 * Rationale: 1-minute granularity captures daily load shape (morning peaks,
 * midday solar, evening demand) without overwhelming memory or compute.
 * Finer steps add no realism for residential loads; coarser steps lose shape.
 */
public class SimulationClock {

    public static final int TICK_MINUTES = 1;
    public static final double TICK_HOURS = TICK_MINUTES / 60.0;

    private LocalDateTime simulatedTime;
    private boolean running = false;

    public SimulationClock(LocalDateTime startTime) {
        this.simulatedTime = startTime;
    }

    /**
     * Advance simulated time by one tick. Returns the new time.
     */
    public LocalDateTime tick() {
        simulatedTime = simulatedTime.plusMinutes(TICK_MINUTES);
        return simulatedTime;
    }

    public LocalDateTime getSimulatedTime() {
        return simulatedTime;
    }

    public void setSimulatedTime(LocalDateTime time) {
        this.simulatedTime = time;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }
}