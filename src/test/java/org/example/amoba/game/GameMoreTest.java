package org.example.amoba.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.example.amoba.model.Board;
import org.example.amoba.model.Mark;
import org.example.amoba.model.Position;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for {@link Game} covering turn validation and states.
 */
public class GameMoreTest {

  @Test
  void testHumanMoveOnAiTurnThrows() {
    Game g = new Game(5, 5);
    // First human move is ok
    g.humanMove(new Position(2, 2));
    // Now it's AI's turn
    assertThrows(IllegalStateException.class, () -> g.humanMove(new Position(2, 3)));
  }

  @Test
  void testAiMoveOnHumanTurnThrows() {
    Game g = new Game(5, 5);
    // At start it's human's turn
    assertThrows(IllegalStateException.class, g::aiMove);
  }

  @Test
  void testStateOWon() throws IOException {
    Path tmp = Files.createTempFile("boardo", ".txt");
    try {
      Files.writeString(tmp, "6 6\n" +
          "......\n" +
          "......\n" +
          "..oooo\n" +
          "......\n" +
          "......\n" +
          "......\n");
      Board b = Board.loadFromFile(tmp);
      Game g = new Game(b);
      assertEquals(GameState.O_WON, g.state());
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void testStateDrawWhenNoMoves() throws IOException {
    // Full 5x5 board with no four-in-a-row for either side -> DRAW
    Path tmp = Files.createTempFile("boarddraw", ".txt");
    try {
      String content = "5 5\n"
          + "xoxox\n"
          + "oxxox\n"
          + "oxoxx\n"
          + "xxoxo\n"
          + "xoxxo\n";
      Files.writeString(tmp, content);
      Board b = Board.loadFromFile(tmp);
      Game g = new Game(b);
      assertEquals(GameState.DRAW, g.state());
    } finally {
      Files.deleteIfExists(tmp);
    }
  }
}
