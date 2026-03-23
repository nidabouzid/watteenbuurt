package com.nbodev.watteenbuurt.domain;

import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.asset.AssetType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HouseTest {

    @Test
    void netPowerIsPositiveWhenConsuming() {
        Asset baseLoad = new Asset("base", AssetType.BASE_LOAD);
        baseLoad.tick(2.0, 1.0 / 60);

        House house = new House("h1", List.of(baseLoad));

        assertThat(house.getCurrentNetPowerKw()).isCloseTo(2.0, within(1e-9));
    }

    @Test
    void pvGenerationReducesNetPower() {
        Asset baseLoad = new Asset("base", AssetType.BASE_LOAD);
        baseLoad.tick(3.0, 1.0 / 60);

        Asset pv = new Asset("pv", AssetType.PV_PANEL);
        pv.tick(2.0, 1.0 / 60); // PV generating 2 kW

        House house = new House("h1", List.of(baseLoad, pv));

        // net = 3.0 (load) - 2.0 (PV) = 1.0
        assertThat(house.getCurrentNetPowerKw()).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void netPowerIsNegativeWhenPvSurplusExceedsLoad() {
        Asset baseLoad = new Asset("base", AssetType.BASE_LOAD);
        baseLoad.tick(1.0, 1.0 / 60);

        Asset pv = new Asset("pv", AssetType.PV_PANEL);
        pv.tick(4.0, 1.0 / 60); // PV generating more than load

        House house = new House("h1", List.of(baseLoad, pv));

        // net = 1.0 - 4.0 = -3.0 (exporting to grid)
        assertThat(house.getCurrentNetPowerKw()).isCloseTo(-3.0, within(1e-9));
    }

    @Test
    void tickNetMeterAccumulatesNetEnergy() {
        Asset baseLoad = new Asset("base", AssetType.BASE_LOAD);
        baseLoad.tick(2.0, 1.0 / 60);

        House house = new House("h1", List.of(baseLoad));
        house.tickNetMeter(1.0 / 60);

        assertThat(house.getNetMeter().getTotalKwh()).isCloseTo(2.0 / 60, within(1e-6));
    }

    @Test
    void hasAssetOfTypeReturnsTrueWhenPresent() {
        Asset pv = new Asset("pv", AssetType.PV_PANEL);
        House house = new House("h1", List.of(pv));

        assertThat(house.hasAssetOfType(AssetType.PV_PANEL)).isTrue();
        assertThat(house.hasAssetOfType(AssetType.HEAT_PUMP)).isFalse();
    }

    @Test
    void hasAssetOfTypeReturnsFalseWhenAbsent() {
        Asset baseLoad = new Asset("base", AssetType.BASE_LOAD);
        House house = new House("h1", List.of(baseLoad));

        assertThat(house.hasAssetOfType(AssetType.HOME_EV_CHARGER)).isFalse();
    }
}
