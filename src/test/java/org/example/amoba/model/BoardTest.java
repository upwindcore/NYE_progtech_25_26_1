package org.example.amoba.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Board} rules and win detection.
 */
public class BoardTest {

  /**
   * On odd-sized board only center cell is legal at start.
   */
  @Test
  void testCenterStartOdd() {
    Board b = new Board(5, 5);
    List<Position> legal = b.legalMoves();
    assertEquals(1, legal.size());
    Position p = legal.get(0);
    assertEquals(new Position(2, 2), p);
  }

  /**
   * On even-sized board four center cells are legal at start.
   */
  @Test
  void testCenterStartEven() {
    Board b = new Board(10, 10);
    List<Position> legal = b.legalMoves();
    assertEquals(4, legal.size());
    assertTrue(legal.contains(new Position(4, 4)));
    assertTrue(legal.contains(new Position(4, 5)));
    assertTrue(legal.contains(new Position(5, 4)));
    assertTrue(legal.contains(new Position(5, 5)));
  }

  /**
   * After first move, only adjacent empty cells are legal.
   */
  @Test
  void testAdjacencyRule() {
    Board b = new Board(5, 5);
    // place center (2,2)
    Position center = new Position(2, 2);
    b = b.place(center, Mark.X);

    List<Position> legal = b.legalMoves();
    assertFalse(legal.contains(new Position(0, 0))); // far corner not adjacent
    assertTrue(legal.contains(new Position(1, 1))); // adjacent diagonal
  }

  /**
   * Non-adjacent placement is rejected.
   */
  @Test
  void testIllegalNonAdjacentMove() {
    Board b = new Board(5, 5);
    // place center
    b = b.place(new Position(2, 2), Mark.X);
    // now try a non-adjacent cell
    final Board b2 = b;
    assertThrows(IllegalArgumentException.class,
        () -> b2.place(new Position(0, 0), Mark.O));
  }

  /**
   * Win detection works for horizontal four-in-a-row.
   */
  @Test
  void testWinDetectionHorizontal() throws IOException {
    // Build a board via file to bypass move legality when arranging the test pattern
    Path tmp = Files.createTempFile("board", ".txt");
    try {
      Files.writeString(tmp, "5 5\n" +
          ".....\n" +
          ".xxxx\n" +
          ".....\n" +
          ".....\n" +
          ".....\n");
      Board b = Board.loadFromFile(tmp);
      assertTrue(b.hasWon(Mark.X));
      assertFalse(b.hasWon(Mark.O));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }
}
