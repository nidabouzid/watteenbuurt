package com.nbodev.watteenbuurt.simulation;


import com.nbodev.watteenbuurt.config.SimulationConfig;
import com.nbodev.watteenbuurt.domain.House;
import com.nbodev.watteenbuurt.domain.Neighbourhood;
import com.nbodev.watteenbuurt.domain.PublicCharger;
import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.asset.AssetType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builds the neighbourhood from config using a seeded RNG (random number generator).
 * Asset distribution is deterministic — same seed always produces the same layout.
 */
@Component
public class NeighbourhoodFactory {

    private final SimulationConfig config;

    public NeighbourhoodFactory(SimulationConfig config) {
        this.config = config;
    }

    public Neighbourhood build() {
        Random rng = new Random(config.getRandomSeed());

        List<House> houses = new ArrayList<>();
        for (int i = 0; i < config.getHouseCount(); i++) {
            houses.add(buildHouse(i, rng));
        }

        List<PublicCharger> chargers = new ArrayList<>();
        for (int i = 0; i < config.getPublicChargerCount(); i++) {
            chargers.add(new PublicCharger("pub-charger-" + i));
        }

        return new Neighbourhood(houses, chargers);
    }

    private House buildHouse(int index, Random rng) {
        String houseId = "house-" + index;
        List<Asset> assets = new ArrayList<>();

        // Base load is always present
        assets.add(new Asset(houseId + "-base", AssetType.BASE_LOAD));

        if (rng.nextDouble() < config.getPvFraction()) {
            assets.add(new Asset(houseId + "-pv", AssetType.PV_PANEL));
        }
        if (rng.nextDouble() < config.getHeatPumpFraction()) {
            assets.add(new Asset(houseId + "-hp", AssetType.HEAT_PUMP));
        }
        if (rng.nextDouble() < config.getHomeEvFraction()) {
            assets.add(new Asset(houseId + "-ev", AssetType.HOME_EV_CHARGER));
        }

        return new House(houseId, assets);
    }
}