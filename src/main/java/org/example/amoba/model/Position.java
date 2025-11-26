package org.example.amoba.model;

import java.util.Objects;

/**
 * Immutable position on the board, 0-based row and column indexes.
 */
public final class Position {

  private final int row;
  private final int col;

  /**
   * Creates a new position.
   *
   * @param row zero-based row index
   * @param col zero-based column index
   */
  public Position(final int row, final int col) {
    this.row = row;
    this.col = col;
  }

  /**
   * Row index (0-based).
   *
   * @return row
   */
  public int getRow() {
    return row;
  }

  /**
   * Column index (0-based).
   *
   * @return col
   */
  public int getCol() {
    return col;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Position position = (Position) o;
    return row == position.row && col == position.col;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col);
  }

  @Override
  public String toString() {
    return "Position{" + "row=" + row + ", col=" + col + '}';
  }
}
