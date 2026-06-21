package de.siom79.ludo.simulation;

import java.util.List;

public record SimulationReport(SimulationConfig config, List<PlayerStats> playerStats) {
}
