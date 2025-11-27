package org.example.amoba.cli;

import org.example.amoba.model.Position;

/**
 * Parses human-readable coordinates like "b3" or "j10" into 0-based positions.
 */
public final class MoveParser {

  private MoveParser() {
  }

  /**
   * Parses input string.
   *
   * @param s coordinate like a1, c5, j10
   * @return position or null if invalid format
   */
  public static Position parse(final String s) {
    if (s == null || s.length() < 2) {
      return null;
    }
    char colChar = Character.toLowerCase(s.charAt(0));
    if (colChar < 'a' || colChar > 'z') {
      return null;
    }
    String rowPart = s.substring(1).trim();
    if (rowPart.isEmpty()) {
      return null;
    }
    try {
      int row = Integer.parseInt(rowPart);
      int r0 = row - 1;
      int c0 = colChar - 'a';
      if (r0 < 0) {
        return null;
      }
      return new Position(r0, c0);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
