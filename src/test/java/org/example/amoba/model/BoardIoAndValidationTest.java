package org.example.amoba.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Extra tests to cover Board I/O and validation branches.
 */
public class BoardIoAndValidationTest {

  @Test
  void testSaveLoadRoundtrip() throws IOException {
    Board b = new Board(5, 5);
    // First legal move must be a center cell
    b = b.place(new Position(2, 2), Mark.X);
    // Adjacent move is legal now
    b = b.place(new Position(2, 3), Mark.O);

    Path tmp = Files.createTempFile("board_rt", ".txt");
    try {
      b.saveToFile(tmp);
      Board loaded = Board.loadFromFile(tmp);
      assertEquals(Mark.X, loaded.get(new Position(2, 2)));
      assertEquals(Mark.O, loaded.get(new Position(2, 3)));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void testInvalidSizeConstructorThrows() {
    assertThrows(IllegalArgumentException.class, () -> new Board(4, 5));
    assertThrows(IllegalArgumentException.class, () -> new Board(26, 5));
    assertThrows(IllegalArgumentException.class, () -> new Board(5, 26));
    assertThrows(IllegalArgumentException.class, () -> new Board(5, 6)); // cols > rows
  }

  @Test
  void testOutOfBoundsPlacementThrows() {
    Board b = new Board(5, 5);
    assertThrows(IllegalArgumentException.class,
        () -> b.place(new Position(-1, 0), Mark.X));
    assertThrows(IllegalArgumentException.class,
        () -> b.place(new Position(0, 5), Mark.X));
  }

  @Test
  void testOccupiedCellThrows() {
    Board b = new Board(5, 5);
    b = b.place(new Position(2, 2), Mark.X);
    Board b2 = b;
    assertThrows(IllegalArgumentException.class,
        () -> b2.place(new Position(2, 2), Mark.O));
  }

  @Test
  void testIsStuckOnFullBoard() throws IOException {
    Path tmp = Files.createTempFile("full", ".txt");
    try {
      String content = "5 5\n"
          + "xoxox\n"
          + "oxxox\n"
          + "oxoxx\n"
          + "xxoxo\n"
          + "xoxxo\n";
      Files.writeString(tmp, content);
      Board b = Board.loadFromFile(tmp);
      assertTrue(b.isStuck());
      assertFalse(b.hasWon(Mark.X));
      assertFalse(b.hasWon(Mark.O));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void testLoadInvalidFormatThrows() throws IOException {
    // Missing cols in header
    Path bad1 = Files.createTempFile("bad1", ".txt");
    try {
      Files.writeString(bad1, "5\n");
      assertThrows(IOException.class, () -> Board.loadFromFile(bad1));
    } finally {
      Files.deleteIfExists(bad1);
    }

    // Too short row line
    Path bad2 = Files.createTempFile("bad2", ".txt");
    try {
      Files.writeString(bad2, "5 5\n" +
          ".....\n" +
          "....\n");
      assertThrows(IOException.class, () -> Board.loadFromFile(bad2));
    } finally {
      Files.deleteIfExists(bad2);
    }
  }

  @Test
  void testRenderHeaderContainsColumnLetters() {
    Board b = new Board(5, 5);
    String rendered = b.render();
    assertTrue(rendered.startsWith("   a b c d e"));
  }
}
