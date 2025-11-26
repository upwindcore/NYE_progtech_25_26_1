package org.example.amoba.model;

/**
 * Represents a cell state on the Gomoku board.
 */
public enum Mark {
  /** Human player's mark. */
  X('x'),
  /** AI player's mark. */
  O('o'),
  /** Empty cell. */
  EMPTY('.');

  private final char symbol;

  Mark(final char symbol) {
    this.symbol = symbol;
  }

  /**
   * Symbol used in text files and board rendering.
   *
   * @return char symbol
   */
  public char getSymbol() {
    return symbol;
  }

  /**
   * Parses a character into a Mark.
   *
   * @param c character
   * @return corresponding mark
   */
  public static Mark fromChar(final char c) {
    if (c == 'x' || c == 'X') {
      return X;
    }
    if (c == 'o' || c == 'O') {
      return O;
    }
    return EMPTY;
  }
}
