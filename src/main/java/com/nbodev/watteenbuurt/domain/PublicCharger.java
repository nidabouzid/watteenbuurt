package com.nbodev.watteenbuurt.domain;


import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.asset.AssetType;

/**
 * A public EV charging station. Shared infrastructure — usage modeled
 * probabilistically, independent of any specific house.
 * <p>
 * Usage model: each charger has a 15% chance per tick of starting a session
 * (if idle). Sessions last 30–90 simulated minutes at 11 kW (Type 2 AC).
 */
public class PublicCharger {

    private final Asset asset;

    public PublicCharger(String id) {
        this.asset = new Asset(id, AssetType.PUBLIC_EV_CHARGER);
    }

    public String getId() {
        return asset.getId();
    }

    public Asset getAsset() {
        return asset;
    }

    public double getCurrentPowerKw() {
        return asset.getCurrentPowerKw();
    }

    public double getTotalKwh() {
        return asset.getTotalKwh();
    }
}