package by.michael.noughtsandcrosses.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameFieldTests {

    @Test
    void testCrossWinHorizontalFirstRow() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][0] = CellType.CROSS.getValue();
        field[0][1] = CellType.CROSS.getValue();
        field[0][2] = CellType.CROSS.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MAX));
        assertFalse(gameField.hasWinner(PlayerType.MIN));
    }

    @Test
    void testCrossWinVerticalFirstColumn() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][0] = CellType.CROSS.getValue();
        field[1][0] = CellType.CROSS.getValue();
        field[2][0] = CellType.CROSS.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MAX));
    }

    @Test
    void testCrossWinMainDiagonal() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][0] = CellType.CROSS.getValue();
        field[1][1] = CellType.CROSS.getValue();
        field[2][2] = CellType.CROSS.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MAX));
    }

    @Test
    void testCrossWinSecondaryDiagonal() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][2] = CellType.CROSS.getValue();
        field[1][1] = CellType.CROSS.getValue();
        field[2][0] = CellType.CROSS.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MAX));
    }

    @Test
    void testNoughtWinHorizontalFirstRow() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][0] = CellType.NOUGHT.getValue();
        field[0][1] = CellType.NOUGHT.getValue();
        field[0][2] = CellType.NOUGHT.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MIN));
        assertFalse(gameField.hasWinner(PlayerType.MAX));
    }

    @Test
    void testNoughtWinVerticalSecondColumn() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][1] = CellType.NOUGHT.getValue();
        field[1][1] = CellType.NOUGHT.getValue();
        field[2][1] = CellType.NOUGHT.getValue();

        assertTrue(gameField.hasWinner(PlayerType.MIN));
    }

    @Test
    void testDraw() {
        GameField gameField = new GameField();
        int[][] field = gameField.getField();
        field[0][0] = CellType.CROSS.getValue();
        field[0][1] = CellType.CROSS.getValue();
        field[0][2] = CellType.NOUGHT.getValue();
        field[1][0] = CellType.NOUGHT.getValue();
        field[1][1] = CellType.NOUGHT.getValue();
        field[1][2] = CellType.CROSS.getValue();
        field[2][0] = CellType.CROSS.getValue();
        field[2][1] = CellType.NOUGHT.getValue();
        field[2][2] = CellType.CROSS.getValue();

        assertTrue(gameField.isFull());
        assertFalse(gameField.hasWinner(PlayerType.MAX));
        assertFalse(gameField.hasWinner(PlayerType.MIN));
    }

    @Test
    void testIsValidMove() {
        GameField gameField = new GameField();

        assertTrue(gameField.isValidMove(0, 0));
        assertTrue(gameField.isValidMove(2, 2));

        gameField.makeMove(1, 1, PlayerType.MAX);
        assertFalse(gameField.isValidMove(1, 1));

        assertFalse(gameField.isValidMove(-1, 0));
        assertFalse(gameField.isValidMove(0, -1));
        assertFalse(gameField.isValidMove(3, 0));
        assertFalse(gameField.isValidMove(0, 3));
    }

    @Test
    void testMakeMove() {
        GameField gameField = new GameField();

        gameField.makeMove(0, 0, PlayerType.MAX);
        assertEquals(CellType.CROSS.getValue(), gameField.getField()[0][0]);

        gameField.makeMove(1, 1, PlayerType.MIN);
        assertEquals(CellType.NOUGHT.getValue(), gameField.getField()[1][1]);
    }

    @Test
    void testMakeMoveThrowsExceptionWhenCellOccupied() {
        GameField gameField = new GameField();
        gameField.makeMove(0, 0, PlayerType.MAX);

        assertThrows(IllegalArgumentException.class, () -> {
            gameField.makeMove(0, 0, PlayerType.MIN);
        });
    }

    @Test
    void testCopy() {
        GameField original = new GameField();
        original.makeMove(0, 0, PlayerType.MAX);
        original.makeMove(1, 1, PlayerType.MIN);

        GameField copy = original.copy();

        assertEquals(original.getField()[0][0], copy.getField()[0][0]);
        assertEquals(original.getField()[1][1], copy.getField()[1][1]);

        // Проверяем, что это действительно копия
        copy.makeMove(2, 2, PlayerType.MAX);
        assertNotEquals(original.getField()[2][2], copy.getField()[2][2]);
    }
}