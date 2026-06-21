package de.siom79.ludo.engine;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import de.siom79.ludo.rules.RuleSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegalMoveCalculatorTest {

    private Board newBoard(int playerCount) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(i, "P" + i, null));
        }
        return new Board(players);
    }

    private Optional<Move> moveForToken(List<Move> moves, Token token) {
        return moves.stream().filter(m -> m.token() == token).findFirst();
    }

    @Test
    void exitOnlyOnSixRequiresSix() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token token = player.tokens().get(0);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> movesWithThree = LegalMoveCalculator.computeLegalMoves(board, player, 3, ruleSet);
        assertTrue(moveForToken(movesWithThree, token).isEmpty());

        List<Move> movesWithSix = LegalMoveCalculator.computeLegalMoves(board, player, 6, ruleSet);
        Move move = moveForToken(movesWithSix, token).orElseThrow();
        assertEquals(1, move.toPosition());
    }

    @Test
    void exitOnlyOnSixDisabledExitsWithAnyRoll() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token token = player.tokens().get(0);
        RuleSet ruleSet = RuleSet.builder().exitOnlyOnSix(false).build();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 3, ruleSet);
        Move move = moveForToken(moves, token).orElseThrow();
        assertEquals(3, move.toPosition());
    }

    @Test
    void ownTokenBlocksMainTrackTarget() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token moving = player.tokens().get(0);
        Token blocker = player.tokens().get(1);
        moving.setPosition(5);
        blocker.setPosition(8);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 3, ruleSet);
        assertTrue(moveForToken(moves, moving).isEmpty());
    }

    @Test
    void landingOnSingleOpponentAlwaysCaptures() {
        Board board = newBoard(2);
        Player self = board.players().get(0);
        Player opponent = board.players().get(1);
        Token moving = self.tokens().get(0);
        Token opponentToken = opponent.tokens().get(0);
        moving.setPosition(5);
        // self (offset 0) moving 5+3=8 -> globalCell 7; opponent (offset 10) needs
        // globalCellOf(1, r) == 7, i.e. r = 38.
        opponentToken.setPosition(38);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, self, 3, ruleSet);
        Move move = moveForToken(moves, moving).orElseThrow();
        assertTrue(move.isCapture());
        assertSame(opponentToken, move.capturedToken());
    }

    @Test
    void goalEntryRequiresExactRoll() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token token = player.tokens().get(0);
        token.setPosition(39);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> overshoot = LegalMoveCalculator.computeLegalMoves(board, player, 6, ruleSet);
        assertTrue(moveForToken(overshoot, token).isEmpty());

        List<Move> exact = LegalMoveCalculator.computeLegalMoves(board, player, 3, ruleSet);
        Move move = moveForToken(exact, token).orElseThrow();
        assertEquals(42, move.toPosition());
    }

    @Test
    void noSkippingInGoalBlocksPassingOccupiedOwnSlot() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token moving = player.tokens().get(0);
        Token occupying = player.tokens().get(1);
        moving.setPosition(39);
        occupying.setPosition(41);
        RuleSet ruleSet = RuleSet.builder().noSkippingInGoal(true).build();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 4, ruleSet);
        assertTrue(moveForToken(moves, moving).isEmpty());
    }

    @Test
    void noSkippingInGoalDisabledAllowsPassingOccupiedOwnSlot() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token moving = player.tokens().get(0);
        Token occupying = player.tokens().get(1);
        moving.setPosition(39);
        occupying.setPosition(41);
        RuleSet ruleSet = RuleSet.builder().noSkippingInGoal(false).build();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 4, ruleSet);
        Move move = moveForToken(moves, moving).orElseThrow();
        assertEquals(43, move.toPosition());
    }

    @Test
    void movingOntoOwnOccupiedGoalSlotIsAlwaysInvalid() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token moving = player.tokens().get(0);
        Token occupying = player.tokens().get(1);
        moving.setPosition(39);
        occupying.setPosition(42);
        RuleSet ruleSet = RuleSet.builder().noSkippingInGoal(false).build();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 3, ruleSet);
        assertTrue(moveForToken(moves, moving).isEmpty());
    }

    @Test
    void overshootBeyondFortyFourIsAlwaysInvalid() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token token = player.tokens().get(0);
        token.setPosition(42);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 5, ruleSet);
        assertTrue(moveForToken(moves, token).isEmpty());
    }

    @Test
    void tokenAlreadyInGoalCanAdvanceFurther() {
        Board board = newBoard(2);
        Player player = board.players().get(0);
        Token token = player.tokens().get(0);
        token.setPosition(41);
        RuleSet ruleSet = RuleSet.standard();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, player, 2, ruleSet);
        Move move = moveForToken(moves, token).orElseThrow();
        assertEquals(43, move.toPosition());
    }

    @Test
    void mandatoryCaptureFiltersToCapturesOnly() {
        Board board = newBoard(2);
        Player self = board.players().get(0);
        Player opponent = board.players().get(1);
        Token moving = self.tokens().get(0);
        Token other = self.tokens().get(1);
        Token opponentToken = opponent.tokens().get(0);
        moving.setPosition(5);
        other.setPosition(20);
        opponentToken.setPosition(38);
        RuleSet ruleSet = RuleSet.builder().mandatoryCapture(true).build();

        List<Move> moves = LegalMoveCalculator.computeLegalMoves(board, self, 3, ruleSet);
        assertEquals(1, moves.size());
        assertTrue(moves.get(0).isCapture());
    }
}
