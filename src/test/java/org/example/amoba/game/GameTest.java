package org.example.amoba.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.example.amoba.model.Board;
import org.example.amoba.model.Mark;
import org.example.amoba.model.Position;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Game} logic.
 */
public class GameTest {

  /**
   * AI chooses a legal move and turn switches back to human.
   */
  @Test
  void testAiMoveIsLegal() {
    // deterministic RNG to make test stable
    Random rng = new Random(42L);
    Board b = new Board(5, 5);
    Game g = new Game(b, rng);
    // human places center
    Position center = new Position(2, 2);
    g.humanMove(center);
    assertEquals(Mark.O, g.getCurrent());
    g.aiMove();

    assertEquals(Mark.X, g.getCurrent());
    // After AI move, board should not be empty
    assertTrue(!g.getBoard().isEmpty());
  }

  /**
   * Game state detects X winner from a prepared board.
   */
  @Test
  void testStateXWon() throws IOException {
    Path tmp = Files.createTempFile("boardx", ".txt");
    try {
      Files.writeString(tmp, "6 6\n" +
          "......\n" +
          "......\n" +
          "..xxxx\n" +
          "......\n" +
          "......\n" +
          "......\n");
      Board b = Board.loadFromFile(tmp);
      Game g = new Game(b);
      assertEquals(GameState.X_WON, g.state());
    } finally {
      Files.deleteIfExists(tmp);
    }
  }
}
