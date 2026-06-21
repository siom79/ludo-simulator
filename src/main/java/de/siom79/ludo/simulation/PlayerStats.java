package de.siom79.ludo.simulation;

import de.siom79.ludo.strategy.StrategySpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record PlayerStats(
        int playerIndex,
        String name,
        StrategySpec strategy,
        int wins,
        List<Integer> roundsToWin,
        List<Integer> turnsToWin) {

    public double winRate(int totalRuns) {
        return totalRuns == 0 ? 0.0 : (double) wins / totalRuns;
    }

    public static final class Builder {
        private final int playerIndex;
        private final String name;
        private final StrategySpec strategy;
        private int wins = 0;
        private final List<Integer> roundsToWin = new ArrayList<>();
        private final List<Integer> turnsToWin = new ArrayList<>();

        public Builder(int playerIndex, String name, StrategySpec strategy) {
            this.playerIndex = playerIndex;
            this.name = name;
            this.strategy = strategy;
        }

        public String name() {
            return name;
        }

        public void recordWin(int rounds, int turns) {
            wins++;
            roundsToWin.add(rounds);
            turnsToWin.add(turns);
        }

        public PlayerStats build() {
            return new PlayerStats(playerIndex, name, strategy, wins,
                    Collections.unmodifiableList(roundsToWin), Collections.unmodifiableList(turnsToWin));
        }
    }
}
