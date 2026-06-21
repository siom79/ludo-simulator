package de.siom79.ludo.simulation;

import de.siom79.ludo.rules.RuleSet;
import de.siom79.ludo.strategy.StrategySpec;
import de.siom79.ludo.strategy.StrategyType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatorTest {

    private SimulationConfig config(int runs, Long seed) {
        List<StrategySpec> strategies = List.of(
                StrategySpec.of(StrategyType.RANDOM),
                StrategySpec.of(StrategyType.FURTHEST_FIRST),
                StrategySpec.of(StrategyType.RANDOM));
        return new SimulationConfig(strategies, RuleSet.standard(), runs, seed);
    }

    @Test
    void sumOfWinsAcrossAllPlayersEqualsTotalRuns() {
        SimulationReport report = new Simulator().run(config(50, 123L));

        int totalWins = report.playerStats().stream().mapToInt(PlayerStats::wins).sum();

        assertEquals(50, totalWins);
    }

    @Test
    void statsSamplesAreOnlyRecordedForActualWins() {
        SimulationReport report = new Simulator().run(config(50, 123L));

        for (PlayerStats stats : report.playerStats()) {
            assertEquals(stats.wins(), stats.roundsToWin().size());
            assertEquals(stats.wins(), stats.turnsToWin().size());
        }
    }

    @Test
    void identicalSeedProducesIdenticalReport() {
        SimulationReport first = new Simulator().run(config(30, 999L));
        SimulationReport second = new Simulator().run(config(30, 999L));

        assertEquals(first, second);
    }
}
