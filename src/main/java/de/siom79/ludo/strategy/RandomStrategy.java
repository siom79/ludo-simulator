package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;

import java.util.List;
import java.util.Random;

public final class RandomStrategy implements MoveStrategy {

    private final Random random;

    public RandomStrategy(Random random) {
        this.random = random;
    }

    @Override
    public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}
