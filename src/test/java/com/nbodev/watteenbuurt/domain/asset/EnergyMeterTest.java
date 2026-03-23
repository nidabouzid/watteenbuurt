package com.nbodev.watteenbuurt.domain.asset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class EnergyMeterTest {

    private final EnergyMeter meter = new EnergyMeter();

    @Test
    void startsAtZero() {
        assertThat(meter.getTotalKwh()).isEqualTo(0.0);
    }

    @Test
    void accumulatesEnergyOverOneTick() {
        // 1 kW for 1 hour = 1 kWh
        meter.addEnergy(1.0, 1.0);
        assertThat(meter.getTotalKwh()).isCloseTo(1.0, within(1e-6));
    }

    @Test
    void accumulatesEnergyOverOneMinuteTick() {
        // 3 kW for 1 minute = 0.05 kWh
        meter.addEnergy(3.0, 1.0 / 60);
        assertThat(meter.getTotalKwh()).isCloseTo(3.0 / 60, within(1e-6));
    }

    @Test
    void accumulatesAcrossMultipleTicks() {
        for (int i = 0; i < 60; i++) {
            meter.addEnergy(2.0, 1.0 / 60);
        }
        // 2 kW for 60 minutes = 2 kWh
        assertThat(meter.getTotalKwh()).isCloseTo(2.0, within(1e-4));
    }

    @Test
    void supportsNegativeEnergyForGeneration() {
        meter.addEnergy(-4.0, 0.5); // PV generating 4 kW for 30 min
        assertThat(meter.getTotalKwh()).isCloseTo(-2.0, within(1e-6));
    }

    @Test
    void resetClearsAccumulator() {
        meter.addEnergy(5.0, 1.0);
        meter.reset();
        assertThat(meter.getTotalKwh()).isEqualTo(0.0);
    }

    @Test
    void netEnergyIsCorrectWhenMixingConsumptionAndGeneration() {
        meter.addEnergy(3.0, 1.0);  // consume 3 kWh
        meter.addEnergy(-1.0, 1.0); // generate 1 kWh
        assertThat(meter.getTotalKwh()).isCloseTo(2.0, within(1e-6));
    }
}
