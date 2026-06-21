package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class CapturePriorityStrategyTest {

    private static final class RecordingStrategy implements MoveStrategy {
        List<Move> receivedMoves;

        @Override
        public Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves) {
            this.receivedMoves = legalMoves;
            return legalMoves.get(0);
        }
    }

    @Test
    void delegatesOnlyCapturesToFallbackWhenCapturesExist() {
        Player player = new Player(0, "P0", null);
        Move noCapture = new Move(player, new Token(), 5, 8, null);
        Move capture = new Move(player, new Token(), 10, 13, new Token());
        RecordingStrategy fallback = new RecordingStrategy();
        CapturePriorityStrategy strategy = new CapturePriorityStrategy(fallback);

        Move selected = strategy.selectMove(null, player, 3, List.of(noCapture, capture));

        assertEquals(List.of(capture), fallback.receivedMoves);
        assertSame(capture, selected);
    }

    @Test
    void delegatesFullListToFallbackWhenNoCapturesExist() {
        Player player = new Player(0, "P0", null);
        Move first = new Move(player, new Token(), 5, 8, null);
        Move second = new Move(player, new Token(), 10, 13, null);
        RecordingStrategy fallback = new RecordingStrategy();
        CapturePriorityStrategy strategy = new CapturePriorityStrategy(fallback);

        Move selected = strategy.selectMove(null, player, 3, List.of(first, second));

        assertEquals(List.of(first, second), fallback.receivedMoves);
        assertSame(first, selected);
    }
}
