package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefensiveStrategyTest {

    private static final class RecordingStrategy implements MoveStrategy {
        List<Move> receivedMoves;

        @Override
        public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
            this.receivedMoves = legalMoves;
            return legalMoves.get(0);
        }
    }

    @Test
    void selectsUniqueSafestMoveWithoutConsultingFallback() {
        Player self = new Player(0, "Self", null);
        Player opponent = new Player(1, "Opp", null);
        Board board = new Board(List.of(self, opponent));
        // opponent token at relative position 5 (global cell 14) can reach global cells 15-20 next roll.
        opponent.tokens().get(0).setPosition(5);

        Move safe = new Move(self, self.tokens().get(0), 0, 1, null); // global cell 0, unreachable
        Move risky = new Move(self, self.tokens().get(1), 10, 16, null); // global cell 15, reachable with a 1

        RecordingStrategy fallback = new RecordingStrategy();
        DefensiveStrategy strategy = new DefensiveStrategy(fallback);

        Move selected = strategy.selectMove(board, self, 3, List.of(risky, safe));

        assertSame(safe, selected);
    }

    @Test
    void delegatesToFallbackWhenMultipleMovesTieOnRisk() {
        Player self = new Player(0, "Self", null);
        Player opponent = new Player(1, "Opp", null);
        Board board = new Board(List.of(self, opponent));
        // opponent token stays at home, so every main-track move carries zero risk and all tie.

        Move first = new Move(self, self.tokens().get(0), 0, 1, null);
        Move second = new Move(self, self.tokens().get(1), 5, 2, null);

        RecordingStrategy fallback = new RecordingStrategy();
        DefensiveStrategy strategy = new DefensiveStrategy(fallback);

        Move selected = strategy.selectMove(board, self, 3, List.of(first, second));

        assertEquals(List.of(first, second), fallback.receivedMoves);
        assertSame(first, selected);
    }
}
