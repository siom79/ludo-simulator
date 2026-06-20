package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;

import java.util.Comparator;
import java.util.List;

public final class NearestFirstStrategy implements MoveStrategy {

    @Override
    public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
        return legalMoves.stream()
                .min(Comparator.comparingInt(Move::fromPosition))
                .orElseThrow();
    }
}
