package com.nbodev.watteenbuurt.api.controller;


import com.nbodev.watteenbuurt.api.dto.AssetDto;
import com.nbodev.watteenbuurt.api.dto.HouseDto;
import com.nbodev.watteenbuurt.api.dto.NeighbourhoodSummaryDto;
import com.nbodev.watteenbuurt.api.dto.PublicChargerDto;
import com.nbodev.watteenbuurt.domain.House;
import com.nbodev.watteenbuurt.domain.Neighbourhood;
import com.nbodev.watteenbuurt.domain.PublicCharger;
import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.asset.AssetType;
import com.nbodev.watteenbuurt.simulation.SimulationEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/neighbourhood")
@Tag(name = "Neighbourhood", description = "Houses, assets and public chargers")
public class NeighbourhoodController {

    private final SimulationEngine engine;

    public NeighbourhoodController(SimulationEngine engine) {
        this.engine = engine;
    }

    /**
     * GET /api/neighbourhood/summary
     * Aggregate counts and current total power. Lightweight.
     */
    @Operation(summary = "Neighbourhood summary", description = "Aggregate counts and current total power.")
    @GetMapping("/summary")
    public NeighbourhoodSummaryDto getSummary() {
        Neighbourhood n = engine.getNeighbourhood();
        return new NeighbourhoodSummaryDto(
                n.getHouses().size(),
                (int) n.countHousesWithAsset(AssetType.PV_PANEL),
                (int) n.countHousesWithAsset(AssetType.HEAT_PUMP),
                (int) n.countHousesWithAsset(AssetType.HOME_EV_CHARGER),
                n.getPublicChargers().size(),
                n.getTotalCurrentPowerKw()
        );
    }

    /**
     * GET /api/neighbourhood/houses
     * All 30 houses with per-asset current power and cumulative kWh.
     */
    @Operation(summary = "All 30 houses", description = "Per-house net power and per-asset cumulative kWh.")
    @GetMapping("/houses")
    public List<HouseDto> getHouses() {
        return engine.getNeighbourhood().getHouses().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * GET /api/neighbourhood/chargers
     * All 6 public chargers with status and cumulative kWh.
     */
    @Operation(summary = "All 6 public chargers")
    @GetMapping("/chargers")
    public List<PublicChargerDto> getChargers() {
        return engine.getNeighbourhood().getPublicChargers().stream()
                .map(this::toDto)
                .toList();
    }

    private HouseDto toDto(House house) {
        List<AssetDto> assetDtos = house.getAssets().stream()
                .map(this::toDto)
                .toList();
        return new HouseDto(
                house.getId(),
                house.getCurrentNetPowerKw(),
                house.getNetMeter().getTotalKwh(),
                assetDtos
        );
    }

    private AssetDto toDto(Asset asset) {
        return new AssetDto(
                asset.getId(),
                asset.getType(),
                asset.getCurrentPowerKw(),
                asset.getTotalKwh()
        );
    }

    private PublicChargerDto toDto(PublicCharger charger) {
        return new PublicChargerDto(
                charger.getId(),
                charger.getCurrentPowerKw(),
                charger.getTotalKwh(),
                charger.getCurrentPowerKw() > 0
        );
    }
}