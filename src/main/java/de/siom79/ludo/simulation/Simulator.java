package de.siom79.ludo.simulation;

import de.siom79.ludo.dice.RandomDice;
import de.siom79.ludo.engine.GameEngine;
import de.siom79.ludo.engine.GameResult;
import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.strategy.MoveStrategy;
import de.siom79.ludo.strategy.StrategyFactory;
import de.siom79.ludo.strategy.StrategySpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Simulator {

    public SimulationReport run(SimulationConfig config) {
        List<StrategySpec> specs = config.strategies();
        int playerCount = specs.size();
        List<PlayerStats.Builder> statsBuilders = new ArrayList<>(playerCount);
        for (int i = 0; i < playerCount; i++) {
            String name = i == 0 ? "Self" : "Opponent " + i;
            statsBuilders.add(new PlayerStats.Builder(i, name, specs.get(i)));
        }

        Random seedSource = config.seed() != null ? new Random(config.seed()) : new Random();

        for (int run = 0; run < config.runs(); run++) {
            Random gameRandom = new Random(seedSource.nextLong());
            List<Player> players = new ArrayList<>(playerCount);
            for (int i = 0; i < playerCount; i++) {
                MoveStrategy strategy = StrategyFactory.create(specs.get(i), gameRandom);
                players.add(new Player(i, statsBuilders.get(i).name(), strategy));
            }
            Board board = new Board(players);
            GameEngine engine = new GameEngine(board, config.ruleSet(), new RandomDice(gameRandom));
            GameResult result = engine.playGame();
            statsBuilders.get(result.winnerIndex()).recordWin(result.totalRounds(), result.totalTurns());
        }

        List<PlayerStats> stats = statsBuilders.stream().map(PlayerStats.Builder::build).toList();
        return new SimulationReport(config, stats);
    }
}
