package de.siom79.ludo.strategy;

import de.siom79.ludo.model.Board;
import de.siom79.ludo.model.Move;
import de.siom79.ludo.model.Player;

import java.util.List;

public interface MoveStrategy {
    Move selectMove(Board board, Player self, int diceRoll, List<Move> legalMoves);
}
