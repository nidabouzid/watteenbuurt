package com.nbodev.watteenbuurt.domain;


import com.nbodev.watteenbuurt.domain.asset.AssetType;

import java.util.List;

/**
 * The top-level domain aggregate. Holds all houses and public chargers.
 * Does not compute anything itself — read by the simulation engine and REST layer.
 */
public class Neighbourhood {

    private final List<House> houses;
    private final List<PublicCharger> publicChargers;

    public Neighbourhood(List<House> houses, List<PublicCharger> publicChargers) {
        if (houses.size() != 30)
            throw new IllegalArgumentException("Neighbourhood must have exactly 30 houses, got: " + houses.size());
        if (publicChargers.size() != 6)
            throw new IllegalArgumentException("Neighbourhood must have exactly 6 public chargers, got: " + publicChargers.size());

        this.houses = List.copyOf(houses);
        this.publicChargers = List.copyOf(publicChargers);
    }

    /**
     * Total current neighbourhood power in kW (net of PV generation).
     */
    public double getTotalCurrentPowerKw() {
        double housePower = houses.stream().mapToDouble(House::getCurrentNetPowerKw).sum();
        double chargerPower = publicChargers.stream().mapToDouble(PublicCharger::getCurrentPowerKw).sum();
        return housePower + chargerPower;
    }

    /**
     * Count of houses that have a specific asset type installed.
     */
    public long countHousesWithAsset(AssetType type) {
        return houses.stream().filter(h -> h.hasAssetOfType(type)).count();
    }

    public List<House> getHouses() {
        return houses;
    }

    public List<PublicCharger> getPublicChargers() {
        return publicChargers;
    }
}