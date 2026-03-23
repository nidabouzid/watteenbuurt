package com.nbodev.watteenbuurt.simulation;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationClockTest {

    private static final LocalDateTime START = LocalDateTime.of(2025, 1, 6, 0, 0);

    @Test
    void initialTimeIsStartTime() {
        SimulationClock clock = new SimulationClock(START);
        assertThat(clock.getSimulatedTime()).isEqualTo(START);
    }

    @Test
    void isNotRunningByDefault() {
        SimulationClock clock = new SimulationClock(START);
        assertThat(clock.isRunning()).isFalse();
    }

    @Test
    void startSetsRunningTrue() {
        SimulationClock clock = new SimulationClock(START);
        clock.start();
        assertThat(clock.isRunning()).isTrue();
    }

    @Test
    void stopSetsRunningFalse() {
        SimulationClock clock = new SimulationClock(START);
        clock.start();
        clock.stop();
        assertThat(clock.isRunning()).isFalse();
    }

    @Test
    void tickAdvancesTimeByOneMinute() {
        SimulationClock clock = new SimulationClock(START);
        LocalDateTime after = clock.tick();
        assertThat(after).isEqualTo(START.plusMinutes(1));
        assertThat(clock.getSimulatedTime()).isEqualTo(START.plusMinutes(1));
    }

    @Test
    void multipleTicksAccumulateCorrectly() {
        SimulationClock clock = new SimulationClock(START);
        for (int i = 0; i < 60; i++) {
            clock.tick();
        }
        assertThat(clock.getSimulatedTime()).isEqualTo(START.plusHours(1));
    }

    @Test
    void tickHoursConstantIsOneMinuteInHours() {
        assertThat(SimulationClock.TICK_HOURS).isEqualTo(1.0 / 60);
    }

    @Test
    void setSimulatedTimeOverridesCurrentTime() {
        SimulationClock clock = new SimulationClock(START);
        LocalDateTime newTime = LocalDateTime.of(2025, 6, 1, 12, 0);
        clock.setSimulatedTime(newTime);
        assertThat(clock.getSimulatedTime()).isEqualTo(newTime);
    }
}
