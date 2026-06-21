package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;

import java.util.List;
import java.util.Objects;

public final class CapturePriorityStrategy implements MoveStrategy {

    private final MoveStrategy fallback;

    public CapturePriorityStrategy(MoveStrategy fallback) {
        this.fallback = Objects.requireNonNull(fallback);
    }

    @Override
    public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
        List<Move> captures = legalMoves.stream().filter(Move::isCapture).toList();
        if (!captures.isEmpty()) {
            return fallback.selectMove(board, self, diceRoll, captures);
        }
        return fallback.selectMove(board, self, diceRoll, legalMoves);
    }
}
