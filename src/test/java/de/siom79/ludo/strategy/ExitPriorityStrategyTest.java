package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ExitPriorityStrategyTest {

    private static final class RecordingStrategy implements MoveStrategy {
        List<Move> receivedMoves;

        @Override
        public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
            this.receivedMoves = legalMoves;
            return legalMoves.get(0);
        }
    }

    @Test
    void delegatesOnlyExitMovesToFallbackOnSix() {
        Player player = new Player(0, "P0", null);
        Move exitMove = new Move(player, new Token(), 0, 1, null);
        Move advanceMove = new Move(player, new Token(), 5, 11, null);
        RecordingStrategy fallback = new RecordingStrategy();
        ExitPriorityStrategy strategy = new ExitPriorityStrategy(fallback);

        Move selected = strategy.selectMove(null, player, 6, List.of(advanceMove, exitMove));

        assertEquals(List.of(exitMove), fallback.receivedMoves);
        assertSame(exitMove, selected);
    }

    @Test
    void delegatesFullListToFallbackOnSixWithoutExitMoves() {
        Player player = new Player(0, "P0", null);
        Move advanceMove = new Move(player, new Token(), 5, 11, null);
        Move otherMove = new Move(player, new Token(), 10, 16, null);
        RecordingStrategy fallback = new RecordingStrategy();
        ExitPriorityStrategy strategy = new ExitPriorityStrategy(fallback);

        Move selected = strategy.selectMove(null, player, 6, List.of(advanceMove, otherMove));

        assertEquals(List.of(advanceMove, otherMove), fallback.receivedMoves);
        assertSame(advanceMove, selected);
    }

    @Test
    void delegatesFullListToFallbackOnNonSixEvenWithExitMoveAvailable() {
        Player player = new Player(0, "P0", null);
        Move exitMove = new Move(player, new Token(), 0, 3, null);
        Move advanceMove = new Move(player, new Token(), 5, 8, null);
        RecordingStrategy fallback = new RecordingStrategy();
        ExitPriorityStrategy strategy = new ExitPriorityStrategy(fallback);

        Move selected = strategy.selectMove(null, player, 3, List.of(exitMove, advanceMove));

        assertEquals(List.of(exitMove, advanceMove), fallback.receivedMoves);
        assertSame(exitMove, selected);
    }
}
