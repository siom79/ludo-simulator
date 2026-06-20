package de.siom79.ludo.engine;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.model.Token;
import de.siom79.ludo.rules.RuleSet;

import java.util.ArrayList;
import java.util.List;

public final class LegalMoveCalculator {

    private LegalMoveCalculator() {
    }

    public static List<Move> computeLegalMoves(Board board, Player player, int diceRoll, RuleSet ruleSet) {
        List<Move> moves = new ArrayList<>();
        for (Token token : player.tokens()) {
            Move move = computeMoveForToken(board, player, token, diceRoll, ruleSet);
            if (move != null) {
                moves.add(move);
            }
        }
        if (ruleSet.mandatoryCapture()) {
            List<Move> captures = moves.stream().filter(Move::isCapture).toList();
            if (!captures.isEmpty()) {
                return captures;
            }
        }
        return moves;
    }

    private static Move computeMoveForToken(Board board, Player player, Token token, int diceRoll, RuleSet ruleSet) {
        if (token.isHome()) {
            return computeExitMove(board, player, token, diceRoll, ruleSet);
        }
        int from = token.position();
        int newPos = from + diceRoll;
        if (token.isOnMainTrack()) {
            if (newPos <= Token.MAIN_TRACK_LENGTH) {
                return resolveMainTrackTarget(board, player, token, from, newPos);
            }
            if (newPos <= Token.GOAL_END) {
                return resolveGoalTarget(board, player, token, from, newPos, Token.GOAL_START, ruleSet);
            }
            return null;
        }
        // token.isInGoal()
        if (newPos > Token.GOAL_END) {
            return null;
        }
        return resolveGoalTarget(board, player, token, from, newPos, from + 1, ruleSet);
    }

    private static Move computeExitMove(Board board, Player player, Token token, int diceRoll, RuleSet ruleSet) {
        int newPos;
        if (ruleSet.exitOnlyOnSix()) {
            if (diceRoll != 6) {
                return null;
            }
            newPos = 1;
        } else {
            newPos = diceRoll;
        }
        return resolveMainTrackTarget(board, player, token, Token.HOME, newPos);
    }

    private static Move resolveMainTrackTarget(Board board, Player player, Token token, int from, int newPos) {
        if (board.ownTokenAt(player, newPos) != null) {
            return null;
        }
        int targetCell = board.globalCellOf(player.index(), newPos);
        Token captured = board.opponentTokenAtGlobalCell(targetCell, player.index());
        return new Move(player, token, from, newPos, captured);
    }

    private static Move resolveGoalTarget(Board board, Player player, Token token, int from, int newPos,
            int rangeStart, RuleSet ruleSet) {
        if (board.ownTokenAt(player, newPos) != null) {
            return null;
        }
        if (ruleSet.noSkippingInGoal()) {
            for (int slot = rangeStart; slot < newPos; slot++) {
                if (board.ownTokenAt(player, slot) != null) {
                    return null;
                }
            }
        }
        return new Move(player, token, from, newPos, null);
    }
}
