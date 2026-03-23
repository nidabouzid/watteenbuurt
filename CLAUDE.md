# WattEenBuurt

Energy neighborhood simulation platform with a real-time web dashboard.

## Tech Stack

- **Backend:** Spring Boot 3.5.12, Java 21, Maven
- **Frontend:** Vanilla JS + HTML5 + Chart.js
- **API Docs:** SpringDoc OpenAPI / Swagger UI

## Build & Run

```bash
# Run
./mvnw spring-boot:run

# Build JAR
./mvnw clean package

# Run tests
./mvnw test
```

## Access Points

- Web UI: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- API base: http://localhost:8080/api/

## Project Structure

```
src/main/java/com/nbodev/watteenbuurt/
├── api/
│   ├── controller/     # REST controllers (SimulationController, NeighbourhoodController)
│   └── dto/            # Data transfer objects
├── config/             # Spring configuration (SimulationConfig, OpenApiConfig, WebConfig)
├── domain/             # Core domain model
│   ├── asset/          # Asset types: BASE_LOAD, PV_PANEL, HEAT_PUMP, EV_CHARGER
│   ├── snapshot/       # History buffer and tick snapshots
│   └── weather/        # WeatherState, Season
└── simulation/         # Simulation engine
    ├── asset/          # Power calculators (BaseLoad, HeatPump, Pv, EvCharger)
    └── weather/        # WeatherService
src/main/resources/
├── application.yaml    # Config: house-count, fractions, tick-interval-ms
└── static/index.html   # Web dashboard
```

## Configuration

Simulation parameters in `src/main/resources/application.yaml`:

| Parameter | Default | Description |
|---|---|---|
| `simulation.house-count` | 30 | Number of houses |
| `simulation.public-charger-count` | 6 | Public EV chargers |
| `simulation.pv-fraction` | 0.40 | Fraction of houses with solar panels |
| `simulation.heat-pump-fraction` | 0.30 | Fraction with heat pumps |
| `simulation.home-ev-fraction` | 0.20 | Fraction with home EV chargers |
| `simulation.tick-interval-ms` | 200 | Simulation update interval |
| `simulation.random-seed` | 42 | RNG seed for reproducibility |

## REST API

| Method | Path | Description |
|---|---|---|
| GET | `/api/simulation/state` | Current clock, weather, power |
| GET | `/api/simulation/history` | Last 24 simulated hours |
| POST | `/api/simulation/control` | Pause/resume `{"action":"pause"\|"resume"}` |
| GET | `/api/neighbourhood/summary` | Aggregate counts and current power |
| GET | `/api/neighbourhood/houses` | All houses with per-asset power |
| GET | `/api/neighbourhood/chargers` | All public chargers with status |

## Architecture Notes

- The simulation runs on a `ScheduledExecutorService` ticking every 200ms (configurable)
- Each tick: advance clock → compute weather → tick all assets → store snapshot
- History buffer retains the last 24 simulated hours of snapshots for charting
- `NeighbourhoodFactory` builds the domain graph at startup using `SimulationConfig`
