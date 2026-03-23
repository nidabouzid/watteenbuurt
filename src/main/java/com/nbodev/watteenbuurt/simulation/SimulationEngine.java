package com.nbodev.watteenbuurt.simulation;


import com.nbodev.watteenbuurt.config.SimulationConfig;
import com.nbodev.watteenbuurt.domain.House;
import com.nbodev.watteenbuurt.domain.Neighbourhood;
import com.nbodev.watteenbuurt.domain.PublicCharger;
import com.nbodev.watteenbuurt.domain.asset.Asset;
import com.nbodev.watteenbuurt.domain.snapshot.HistoryBuffer;
import com.nbodev.watteenbuurt.domain.snapshot.TickSnapshot;
import com.nbodev.watteenbuurt.domain.weather.WeatherState;
import com.nbodev.watteenbuurt.simulation.asset.BaseLoadCalculator;
import com.nbodev.watteenbuurt.simulation.asset.EvChargerCalculator;
import com.nbodev.watteenbuurt.simulation.asset.HeatPumpCalculator;
import com.nbodev.watteenbuurt.simulation.asset.PvCalculator;
import com.nbodev.watteenbuurt.simulation.weather.WeatherService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main simulation loop. Runs on a ScheduledExecutorService.
 * Each tick:
 * 1. Advance simulated clock
 * 2. Compute weather
 * 3. Tick every asset (compute power + accumulate energy)
 * 4. Tick house net meters
 * 5. Store snapshot in history buffer
 */
@Service
public class SimulationEngine {

    private final SimulationConfig config;
    private final WeatherService weatherService;
    private final NeighbourhoodFactory factory;
    private final HistoryBuffer historyBuffer = new HistoryBuffer();
    // Asset calculators keyed by asset id
    private final Map<String, BaseLoadCalculator> baseCalcs = new LinkedHashMap<>();
    private final Map<String, HeatPumpCalculator> hpCalcs = new LinkedHashMap<>();
    private final Map<String, PvCalculator> pvCalcs = new LinkedHashMap<>();
    private final Map<String, EvChargerCalculator> evCalcs = new LinkedHashMap<>();
    private final SimulationClock clock;
    private Neighbourhood neighbourhood;
    private WeatherState currentWeather;
    private ScheduledExecutorService scheduler;

    public SimulationEngine(SimulationConfig config,
                            WeatherService weatherService,
                            NeighbourhoodFactory factory) {
        this.config = config;
        this.weatherService = weatherService;
        this.factory = factory;
        this.clock = new SimulationClock(config.getStartDateTime());
    }

    @PostConstruct
    public void init() {
        neighbourhood = factory.build();
        initCalculators();
        clock.start();
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "simulation-engine");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::tick, 0, config.getTickIntervalMs(), TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }

    private void tick() {
        if (!clock.isRunning()) return;

        LocalDateTime now = clock.tick();
        currentWeather = weatherService.compute(now);

        // Tick every house asset
        for (House house : neighbourhood.getHouses()) {
            for (Asset asset : house.getAssets()) {
                double power = computePower(asset, currentWeather, now);
                asset.tick(power, SimulationClock.TICK_HOURS);
            }
            house.tickNetMeter(SimulationClock.TICK_HOURS);
        }

        // Tick public chargers
        for (PublicCharger charger : neighbourhood.getPublicChargers()) {
            EvChargerCalculator calc = evCalcs.get(charger.getId());
            double power = calc != null ? calc.compute() : 0.0;
            charger.getAsset().tick(power, SimulationClock.TICK_HOURS);
        }

        // Store snapshot
        historyBuffer.add(new TickSnapshot(
                now,
                neighbourhood.getTotalCurrentPowerKw(),
                currentWeather.temperatureCelsius(),
                currentWeather.cloudinessFactor()
        ));
    }

    private double computePower(Asset asset, WeatherState weather, LocalDateTime time) {
        return switch (asset.getType()) {
            case BASE_LOAD -> baseCalcs.getOrDefault(asset.getId(), defaultBase()).compute(time);
            case HEAT_PUMP -> hpCalcs.getOrDefault(asset.getId(), new HeatPumpCalculator()).compute(weather);
            case PV_PANEL -> pvCalcs.getOrDefault(asset.getId(), new PvCalculator()).compute(weather);
            case HOME_EV_CHARGER -> evCalcs.getOrDefault(asset.getId(), defaultHomeEv()).compute();
            case PUBLIC_EV_CHARGER -> 0.0; // handled separately via PublicCharger
        };
    }

    private void initCalculators() {
        Random rng = new Random(config.getRandomSeed());

        for (House house : neighbourhood.getHouses()) {
            for (Asset asset : house.getAssets()) {
                switch (asset.getType()) {
                    case BASE_LOAD -> baseCalcs.put(asset.getId(), new BaseLoadCalculator(new Random(rng.nextLong())));
                    case HEAT_PUMP -> hpCalcs.put(asset.getId(), new HeatPumpCalculator());
                    case PV_PANEL -> pvCalcs.put(asset.getId(), new PvCalculator());
                    case HOME_EV_CHARGER -> evCalcs.put(asset.getId(), new EvChargerCalculator(
                            EvChargerCalculator.ChargerKind.HOME, new Random(rng.nextLong())));
                    default -> {
                    }
                }
            }
        }

        // Public chargers
        for (PublicCharger charger : neighbourhood.getPublicChargers()) {
            evCalcs.put(charger.getId(), new EvChargerCalculator(
                    EvChargerCalculator.ChargerKind.PUBLIC, new Random(rng.nextLong())));
        }
    }

    // Fallbacks (should not be needed in practice)
    private BaseLoadCalculator defaultBase() {
        return new BaseLoadCalculator(new Random());
    }

    private EvChargerCalculator defaultHomeEv() {
        return new EvChargerCalculator(EvChargerCalculator.ChargerKind.HOME, new Random());
    }

    // --- API accessors ---
    public Neighbourhood getNeighbourhood() {
        return neighbourhood;
    }

    public HistoryBuffer getHistoryBuffer() {
        return historyBuffer;
    }

    public WeatherState getCurrentWeather() {
        return currentWeather;
    }

    public SimulationClock getClock() {
        return clock;
    }
}