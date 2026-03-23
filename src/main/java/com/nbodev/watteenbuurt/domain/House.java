package com.nbodev.watteenbuurt.domain;


import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.asset.AssetType;
import com.nbodev.watteenbuurt.domain.asset.EnergyMeter;
import lombok.Getter;

import java.util.List;

/**
 * A residential unit with one or more energy assets and an aggregate meter.
 * The house meter tracks net energy (consumption minus PV generation).
 */
@Getter
public class House {

    private final String id;
    private final List<Asset> assets;
    private final EnergyMeter netMeter = new EnergyMeter(); // net house-level meter

    public House(String id, List<Asset> assets) {
        this.id = id;
        this.assets = List.copyOf(assets);
    }

    /**
     * Net current power in kW.
     * Positive = net consumer, negative = net exporter (PV surplus).
     */
    public double getCurrentNetPowerKw() {
        return assets.stream()
                .mapToDouble(a -> a.getType() == AssetType.PV_PANEL
                        ? -a.getCurrentPowerKw()   // PV offsets load (negative)
                        : a.getCurrentPowerKw())
                .sum();
    }

    /**
     * Called by engine each tick to accumulate net house energy.
     */
    public void tickNetMeter(double tickHours) {
        netMeter.addEnergy(getCurrentNetPowerKw(), tickHours);
    }

    public boolean hasAssetOfType(AssetType type) {
        return assets.stream().anyMatch(a -> a.getType() == type);
    }

}