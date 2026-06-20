package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;

import java.util.List;
import java.util.Objects;

public final class ExitPriorityStrategy implements MoveStrategy {

    private final MoveStrategy fallback;

    public ExitPriorityStrategy(MoveStrategy fallback) {
        this.fallback = Objects.requireNonNull(fallback);
    }

    @Override
    public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
        if (diceRoll == 6) {
            List<Move> exitMoves = legalMoves.stream().filter(move -> move.fromPosition() == 0).toList();
            if (!exitMoves.isEmpty()) {
                return fallback.selectMove(board, self, diceRoll, exitMoves);
            }
        }
        return fallback.selectMove(board, self, diceRoll, legalMoves);
    }
}
