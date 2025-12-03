package org.example.amoba.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Additional tests focusing on win detection in various directions.
 */
public class BoardWinDetectionTest {

  @Test
  void testWinVertical() throws IOException {
    Path tmp = Files.createTempFile("board0", ".txt");
    try {
      Files.writeString(tmp, "6 6\n" +
          "..o...\n" +
          "..x...\n" +
          "..x...\n" +
          "..x...\n" +
          "..x...\n" +
          "..o...\n");
      Board b = Board.loadFromFile(tmp);
      assertTrue(b.hasWon(Mark.X));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void testWinDiagDownRight() throws IOException {
    Path tmp = Files.createTempFile("board1", ".txt");
    try {
      Files.writeString(tmp, "7 7\n" +
          "x......\n" +
          ".x.....\n" +
          "..x....\n" +
          "...x...\n" +
          ".......\n" +
          ".......\n" +
          ".......\n");
      Board b = Board.loadFromFile(tmp);
      assertTrue(b.hasWon(Mark.X));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test
  void testWinDiagDownLeft() throws IOException {
    Path tmp = Files.createTempFile("board2", ".txt");
    try {
      Files.writeString(tmp, "7 7\n" +
          "......x\n" +
          ".....x.\n" +
          "....x..\n" +
          "...x...\n" +
          ".......\n" +
          ".......\n" +
          ".......\n");
      Board b = Board.loadFromFile(tmp);
      assertTrue(b.hasWon(Mark.X));
    } finally {
      Files.deleteIfExists(tmp);
    }
  }
}
