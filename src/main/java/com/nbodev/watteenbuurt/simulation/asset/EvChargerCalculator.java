package com.nbodev.watteenbuurt.simulation.asset;

import java.util.Random;

/**
 * EV charger model — handles both home and public chargers.
 * <p>
 * Session model:
 * - Each tick: if idle, probability P of starting a session.
 * - Session power: 7.4 kW (home, Mode 2) or 11 kW (public, Type 2 AC).
 * - Session duration: uniform random 30–120 simulated minutes.
 * <p>
 * Home charger start probability:  8% per tick (concentrated in evening).
 * Public charger start probability: 15% per tick (spread across day).
 * <p>
 * Sessions are tracked as remaining ticks.
 */
public class EvChargerCalculator {

    private static final double HOME_POWER_KW = 7.4;
    private static final double PUBLIC_POWER_KW = 11.0;
    private static final double HOME_START_PROB = 0.08;
    private static final double PUBLIC_START_PROB = 0.15;
    private static final int MIN_SESSION_TICKS = 30;
    private static final int MAX_SESSION_TICKS = 120;
    private final ChargerKind kind;
    private final Random rng;
    private int remainingSessionTicks = 0;
    public EvChargerCalculator(ChargerKind kind, Random rng) {
        this.kind = kind;
        this.rng = rng;
    }

    public double compute() {
        if (remainingSessionTicks > 0) {
            remainingSessionTicks--;
            return powerKw();
        }
        double startProb = kind == ChargerKind.HOME ? HOME_START_PROB : PUBLIC_START_PROB;
        if (rng.nextDouble() < startProb) {
            remainingSessionTicks = MIN_SESSION_TICKS
                    + rng.nextInt(MAX_SESSION_TICKS - MIN_SESSION_TICKS + 1);
            return powerKw();
        }
        return 0.0;
    }

    private double powerKw() {
        return kind == ChargerKind.HOME ? HOME_POWER_KW : PUBLIC_POWER_KW;
    }

    public boolean isCharging() {
        return remainingSessionTicks > 0;
    }

    public enum ChargerKind {HOME, PUBLIC}
}