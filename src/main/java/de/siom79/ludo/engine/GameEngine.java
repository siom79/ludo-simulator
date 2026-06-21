package de.siom79.ludo.engine;

import de.siom79.ludo.dice.Dice;
import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;
import de.siom79.ludo.rules.RuleSet;

import java.util.List;

public final class GameEngine {

    private final Board board;
    private final RuleSet ruleSet;
    private final Dice dice;

    public GameEngine(Board board, RuleSet ruleSet, Dice dice) {
        this.board = board;
        this.ruleSet = ruleSet;
        this.dice = dice;
    }

    public GameResult playGame() {
        List<Player> players = board.players();
        int playerCount = players.size();
        int currentPlayerIndex = 0;
        int totalTurns = 0;
        int totalRounds = 0;
        int consecutiveSixes = 0;

        while (true) {
            Player current = players.get(currentPlayerIndex);
            int roll = dice.roll();
            totalTurns++;

            boolean rolledSix = roll == 6;
            consecutiveSixes = rolledSix ? consecutiveSixes + 1 : 0;
            boolean forfeited = rolledSix && consecutiveSixes == 3 && ruleSet.threeSixesForfeitsTurn();

            if (!forfeited) {
                List<Move> legalMoves = LegalMoveCalculator.computeLegalMoves(board, current, roll, ruleSet);
                if (!legalMoves.isEmpty()) {
                    Move chosen = current.strategy().selectMove(board, current, roll, legalMoves);
                    CaptureResolver.apply(chosen);
                    if (current.hasWon()) {
                        return new GameResult(current.index(), totalRounds, totalTurns);
                    }
                }
            }

            boolean extraTurn = rolledSix && ruleSet.extraTurnOnSix() && !forfeited;
            if (!extraTurn) {
                currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
                consecutiveSixes = 0;
                if (currentPlayerIndex == 0) {
                    totalRounds++;
                }
            }
        }
    }
}
