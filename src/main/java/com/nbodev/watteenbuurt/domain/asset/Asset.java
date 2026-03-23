package com.nbodev.watteenbuurt.domain.asset;

import lombok.Getter;
import lombok.Setter;

/**
 * A single energy asset (load or generator) within a house or public infrastructure.
 * Each asset tracks its own energy meter and reports current power on each tick.
 */
@Getter
public class Asset {

    private final String id;
    private final AssetType type;
    private final EnergyMeter meter = new EnergyMeter();

    @Setter
    private double currentPowerKw = 0.0;

    public Asset(String id, AssetType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Called by the simulation engine each tick.
     * Updates currentPowerKw and accumulates energy into the meter.
     *
     * @param powerKw   computed power for this tick
     * @param tickHours duration of this tick in hours
     */
    public void tick(double powerKw, double tickHours) {
        this.currentPowerKw = powerKw;
        meter.addEnergy(powerKw, tickHours);
    }

    public double getTotalKwh() {
        return meter.getTotalKwh();
    }

}