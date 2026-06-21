package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

class FurthestFirstStrategyTest {

    @Test
    void selectsMoveWithHighestFromPosition() {
        Player player = new Player(0, "P0", null);
        Move low = new Move(player, new Token(), 5, 8, null);
        Move high = new Move(player, new Token(), 30, 33, null);
        Move mid = new Move(player, new Token(), 15, 18, null);

        FurthestFirstStrategy strategy = new FurthestFirstStrategy();
        Move selected = strategy.selectMove(null, player, 3, List.of(low, high, mid));

        assertSame(high, selected);
    }
}
