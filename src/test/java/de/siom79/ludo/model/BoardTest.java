package de.siom79.ludo.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    private final Board board = new Board(List.of());

    @Test
    void globalCellOfPlayerZero() {
        assertEquals(0, board.globalCellOf(0, 1));
        assertEquals(39, board.globalCellOf(0, 40));
    }

    @Test
    void globalCellOfPlayerOneWrapsAround() {
        assertEquals(10, board.globalCellOf(1, 1));
        assertEquals(0, board.globalCellOf(1, 31));
    }

    @Test
    void globalCellOfPlayerTwoWrapsAround() {
        assertEquals(20, board.globalCellOf(2, 1));
        assertEquals(0, board.globalCellOf(2, 21));
    }

    @Test
    void globalCellOfPlayerThreeWrapsAround() {
        assertEquals(30, board.globalCellOf(3, 1));
        assertEquals(0, board.globalCellOf(3, 11));
    }

    @Test
    void globalCellOfRejectsOutOfRangePosition() {
        assertThrows(IllegalArgumentException.class, () -> board.globalCellOf(0, 0));
        assertThrows(IllegalArgumentException.class, () -> board.globalCellOf(0, 41));
    }
}
