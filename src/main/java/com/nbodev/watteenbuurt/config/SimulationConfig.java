package com.nbodev.watteenbuurt.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * All tunable knobs in one place — override via application.yml.
 * <p>
 * Asset distribution (out of 30 houses):
 * PV panels:        40% → 12 houses
 * Heat pumps:       30% → 9  houses
 * Home EV chargers: 20% → 6  houses
 * (combinations allowed — assigned by seeded RNG)
 */
@Validated
@Data
@ConfigurationProperties(prefix = "simulation")
public class SimulationConfig {

    private final long randomSeed;
    private final int houseCount;
    private final int publicChargerCount;
    private final double pvFraction;
    private final double heatPumpFraction;
    private final double homeEvFraction;
    private final int tickIntervalMs;
    private final LocalDateTime clockStartTime;

    public SimulationConfig(
            long randomSeed,
            @Min(1) int houseCount,
            @Min(1) int publicChargerCount,
            @DecimalMin("0.0") @DecimalMax("1.0") double pvFraction,
            @DecimalMin("0.0") @DecimalMax("1.0") double heatPumpFraction,
            @DecimalMin("0.0") @DecimalMax("1.0") double homeEvFraction,
            @Min(200) int tickIntervalMs,
            @NotNull LocalDateTime clockStartTime
    ) {
        this.randomSeed = randomSeed;
        this.houseCount = houseCount;
        this.publicChargerCount = publicChargerCount;
        this.pvFraction = pvFraction;
        this.heatPumpFraction = heatPumpFraction;
        this.homeEvFraction = homeEvFraction;
        this.tickIntervalMs = tickIntervalMs;
        this.clockStartTime = clockStartTime;
    }

}