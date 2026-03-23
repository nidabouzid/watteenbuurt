package com.nbodev.watteenbuurt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * All tunable knobs in one place — override via application.yml.
 * <p>
 * Asset distribution (out of 30 houses):
 * PV panels:        40% → 12 houses
 * Heat pumps:       30% → 9  houses
 * Home EV chargers: 20% → 6  houses
 * (combinations allowed — assigned by seeded RNG)
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "simulation")
public class SimulationConfig {

    private final long randomSeed = 42L;
    private final int houseCount = 30;
    private final int publicChargerCount = 6;

    private final double pvFraction = 0.40;
    private final double heatPumpFraction = 0.30;
    private final double homeEvFraction = 0.20;

    // Real-time interval between ticks (ms). 200ms = 5 sim-minutes/real-second.
    private final int tickIntervalMs = 200;


}