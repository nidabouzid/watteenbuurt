package com.nbodev.watteenbuurt.api.dto;


import com.nbodev.watteenbuurt.domain.asset.AssetType;

public record AssetDto(
        String id,
        AssetType type,
        double currentPowerKw,
        double totalKwh
) {
}