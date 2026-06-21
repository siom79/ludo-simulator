package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertSame;

class RandomStrategyTest {

    private List<Move> threeMoves(Player player) {
        return List.of(
                new Move(player, new Token(), 0, 5, null),
                new Move(player, new Token(), 0, 10, null),
                new Move(player, new Token(), 0, 15, null));
    }

    @Test
    void selectsMoveAtIndexDeterminedByRandom() {
        Player player = new Player(0, "P0", null);
        List<Move> moves = threeMoves(player);
        int expectedIndex = new Random(42).nextInt(moves.size());

        RandomStrategy strategy = new RandomStrategy(new Random(42));
        Move selected = strategy.selectMove(null, player, 3, moves);

        assertSame(moves.get(expectedIndex), selected);
    }

    @Test
    void singleLegalMoveIsAlwaysReturned() {
        Player player = new Player(0, "P0", null);
        Move onlyMove = new Move(player, new Token(), 0, 5, null);

        RandomStrategy strategy = new RandomStrategy(new Random(7));
        Move selected = strategy.selectMove(null, player, 3, List.of(onlyMove));

        assertSame(onlyMove, selected);
    }
}
