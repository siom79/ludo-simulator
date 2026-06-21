package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;

import java.util.List;
import java.util.Objects;

public final class DefensiveStrategy implements MoveStrategy {

    private final MoveStrategy fallback;

    public DefensiveStrategy(MoveStrategy fallback) {
        this.fallback = Objects.requireNonNull(fallback);
    }

    @Override
    public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
        int minRisk = legalMoves.stream().mapToInt(move -> captureRisk(board, self, move)).min().orElseThrow();
        List<Move> safest = legalMoves.stream()
                .filter(move -> captureRisk(board, self, move) == minRisk)
                .toList();
        if (safest.size() == 1) {
            return safest.get(0);
        }
        return fallback.selectMove(board, self, diceRoll, safest);
    }

    // Only opponent tokens already on the main track are considered a threat;
    // tokens still in their house depend on the (here unavailable) RuleSet to know if/where they could exit.
    private int captureRisk(Board board, Player self, Move move) {
        int toPosition = move.toPosition();
        if (toPosition < 1 || toPosition > Board.MAIN_TRACK_LENGTH) {
            return 0;
        }
        int targetCell = board.globalCellOf(self.index(), toPosition);
        int risk = 0;
        for (Player opponent : board.players()) {
            if (opponent.index() == self.index()) {
                continue;
            }
            for (Token token : opponent.tokens()) {
                if (!token.isOnMainTrack()) {
                    continue;
                }
                for (int d = 1; d <= 6; d++) {
                    int candidate = token.position() + d;
                    if (candidate <= Board.MAIN_TRACK_LENGTH
                            && board.globalCellOf(opponent.index(), candidate) == targetCell) {
                        risk++;
                    }
                }
            }
        }
        return risk;
    }
}
