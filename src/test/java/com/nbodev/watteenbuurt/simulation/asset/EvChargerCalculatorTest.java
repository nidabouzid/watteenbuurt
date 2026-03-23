package com.nbodev.watteenbuurt.simulation.asset;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class EvChargerCalculatorTest {

    /** Always triggers a session start, session length = MIN (30 ticks). */
    private static Random alwaysStart() {
        return new Random() {
            @Override public double nextDouble() { return 0.0; }
            @Override public int nextInt(int bound) { return 0; }
        };
    }

    /** Never triggers a session start. */
    private static Random neverStart() {
        return new Random() {
            @Override public double nextDouble() { return 1.0; }
        };
    }

    @Test
    void homeChargerIsIdleInitially() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.HOME, neverStart());
        assertThat(calc.isCharging()).isFalse();
        assertThat(calc.compute()).isEqualTo(0.0);
    }

    @Test
    void publicChargerIsIdleInitially() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.PUBLIC, neverStart());
        assertThat(calc.isCharging()).isFalse();
        assertThat(calc.compute()).isEqualTo(0.0);
    }

    @Test
    void homeChargerDeliversCorrectPowerDuringSession() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.HOME, alwaysStart());
        double power = calc.compute(); // session starts on this tick
        assertThat(power).isEqualTo(7.4);
    }

    @Test
    void publicChargerDeliversCorrectPowerDuringSession() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.PUBLIC, alwaysStart());
        double power = calc.compute(); // session starts on this tick
        assertThat(power).isEqualTo(11.0);
    }

    @Test
    void isChargingTrueWhileSessionActive() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.HOME, alwaysStart());
        calc.compute(); // start session (30 ticks with alwaysStart + nextInt=0)
        assertThat(calc.isCharging()).isTrue();
    }

    @Test
    void sessionEndsAfterMinimumDuration() {
        // alwaysStart: nextInt=0 → session = MIN_SESSION_TICKS (30), first call starts it
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.HOME, alwaysStart());
        calc.compute(); // tick 0: starts session, remainingTicks = 30, returns power

        // ticks 1..30: session active (remainingTicks decrements from 30 to 0)
        for (int i = 0; i < 30; i++) {
            assertThat(calc.compute()).isEqualTo(7.4);
        }

        // tick 31: session exhausted, new session starts immediately (alwaysStart)
        assertThat(calc.compute()).isEqualTo(7.4);
        assertThat(calc.isCharging()).isTrue();
    }

    @Test
    void noSessionStartsWhenRngAboveThreshold() {
        EvChargerCalculator calc = new EvChargerCalculator(EvChargerCalculator.ChargerKind.PUBLIC, neverStart());
        for (int i = 0; i < 100; i++) {
            assertThat(calc.compute()).isEqualTo(0.0);
        }
        assertThat(calc.isCharging()).isFalse();
    }
}
