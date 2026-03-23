package com.nbodev.watteenbuurt.domain.asset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class AssetTest {

    @Test
    void currentPowerIsZeroBeforeFirstTick() {
        Asset asset = new Asset("a1", AssetType.BASE_LOAD);
        assertThat(asset.getCurrentPowerKw()).isEqualTo(0.0);
    }

    @Test
    void tickUpdatesPowerAndAccumulatesEnergy() {
        Asset asset = new Asset("a1", AssetType.BASE_LOAD);

        asset.tick(2.0, 1.0 / 60);

        assertThat(asset.getCurrentPowerKw()).isEqualTo(2.0);
        assertThat(asset.getTotalKwh()).isCloseTo(2.0 / 60, within(1e-6));
    }

    @Test
    void multipleTicksAccumulateEnergy() {
        Asset asset = new Asset("a1", AssetType.HEAT_PUMP);

        for (int i = 0; i < 60; i++) {
            asset.tick(1.5, 1.0 / 60);
        }

        assertThat(asset.getTotalKwh()).isCloseTo(1.5, within(1e-4));
    }

    @Test
    void currentPowerIsUpdatedOnEachTick() {
        Asset asset = new Asset("a1", AssetType.PV_PANEL);

        asset.tick(3.0, 1.0 / 60);
        assertThat(asset.getCurrentPowerKw()).isEqualTo(3.0);

        asset.tick(1.5, 1.0 / 60);
        assertThat(asset.getCurrentPowerKw()).isEqualTo(1.5);
    }
}
