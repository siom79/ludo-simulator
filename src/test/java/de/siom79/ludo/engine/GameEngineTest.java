package de.siom79.ludo.engine;

import de.siom79.ludo.dice.Dice;
import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import de.siom79.ludo.rules.RuleSet;
import de.siom79.ludo.strategy.FurthestFirstStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEngineTest {

    private static final class ScriptedDice implements Dice {
        private final int[] values;
        private int index = 0;

        ScriptedDice(int... values) {
            this.values = values;
        }

        @Override
        public int roll() {
            if (index >= values.length) {
                throw new IllegalStateException("No more scripted dice values");
            }
            return values[index++];
        }
    }

    private Board newBoard(int playerCount) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(i, "P" + i, new FurthestFirstStrategy()));
        }
        return new Board(players);
    }

    // Leaves goal slot 41 open and parks the remaining three tokens at 42/43/44 so the
    // last token's entry into 41 never needs to pass over an occupied own slot.
    private void seedAlmostWonWithFreeSlot41(Player player) {
        player.tokens().get(0).setPosition(42);
        player.tokens().get(1).setPosition(43);
        player.tokens().get(2).setPosition(44);
        player.tokens().get(3).setPosition(40);
    }

    @Test
    void threeConsecutiveSixesForfeitTurn() {
        Board board = newBoard(2);
        seedAlmostWonWithFreeSlot41(board.players().get(1));
        RuleSet ruleSet = RuleSet.standard();
        GameEngine engine = new GameEngine(board, ruleSet, new ScriptedDice(6, 6, 6, 1));

        GameResult result = engine.playGame();

        assertEquals(1, result.winnerIndex());
        assertEquals(4, result.totalTurns());
        // Player 0 only acted on the first two sixes (exit + advance); the third six was
        // forfeited, so the exited token must still sit where the second six left it.
        Token exited = board.players().get(0).tokens().stream()
                .filter(t -> !t.isHome())
                .findFirst()
                .orElseThrow();
        assertEquals(7, exited.position());
    }

    @Test
    void exactRollIntoLastFreeGoalSlotEndsGame() {
        Board board = newBoard(2);
        seedAlmostWonWithFreeSlot41(board.players().get(0));
        RuleSet ruleSet = RuleSet.standard();
        GameEngine engine = new GameEngine(board, ruleSet, new ScriptedDice(1));

        GameResult result = engine.playGame();

        assertEquals(0, result.winnerIndex());
    }

    @Test
    void extraTurnOnSixDisabledPassesTurnImmediately() {
        Board board = newBoard(2);
        seedAlmostWonWithFreeSlot41(board.players().get(1));
        RuleSet ruleSet = RuleSet.builder().extraTurnOnSix(false).build();
        GameEngine engine = new GameEngine(board, ruleSet, new ScriptedDice(6, 1));

        GameResult result = engine.playGame();

        assertEquals(1, result.winnerIndex());
        assertEquals(2, result.totalTurns());
    }

    @Test
    void emptyLegalMovesForfeitTurnWithoutCrashing() {
        Board board = newBoard(2);
        seedAlmostWonWithFreeSlot41(board.players().get(1));
        RuleSet ruleSet = RuleSet.standard();
        // Player 0 rolls a non-six with all tokens at home: no legal move, must forfeit cleanly.
        GameEngine engine = new GameEngine(board, ruleSet, new ScriptedDice(3, 1));

        GameResult result = engine.playGame();

        assertEquals(1, result.winnerIndex());
        assertEquals(2, result.totalTurns());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void totalRoundsCountsOneFullCycleRegardlessOfPlayerCount(int playerCount) {
        Board board = newBoard(playerCount);
        seedAlmostWonWithFreeSlot41(board.players().get(0));
        RuleSet ruleSet = RuleSet.standard();

        int[] rolls = new int[playerCount + 1];
        rolls[0] = 2; // player 0's first turn: every token is blocked, forfeits without moving
        for (int i = 1; i < playerCount; i++) {
            rolls[i] = 1; // remaining players: all tokens at home, non-six, forfeits
        }
        rolls[playerCount] = 1; // player 0's second turn: exact roll into the only free slot

        GameEngine engine = new GameEngine(board, ruleSet, new ScriptedDice(rolls));

        GameResult result = engine.playGame();

        assertEquals(0, result.winnerIndex());
        assertEquals(1, result.totalRounds());
        assertEquals(playerCount + 1, result.totalTurns());
    }
}
