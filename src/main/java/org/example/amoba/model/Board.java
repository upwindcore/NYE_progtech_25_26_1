package org.example.amoba.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable-like Board aggregate containing the state and core game rules.
 * The board uses 0-based coordinates internally.
 */
public final class Board {

  private final int rows;
  private final int cols;
  private final Mark[][] cells;

  /**
   * Creates an empty board with given dimensions.
   *
   * @param rows number of rows (5..25)
   * @param cols number of columns (5..25, and cols <= rows)
   */
  public Board(final int rows, final int cols) {
    if (rows < 5 || rows > 25 || cols < 5 || cols > 25 || cols > rows) {
      throw new IllegalArgumentException("Invalid board size");
    }
    this.rows = rows;
    this.cols = cols;
    this.cells = new Mark[rows][cols];
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        cells[r][c] = Mark.EMPTY;
      }
    }
  }

  private Board(final int rows, final int cols, final Mark[][] data) {
    this.rows = rows;
    this.cols = cols;
    this.cells = data;
  }

  /**
   * Returns the number of rows on the board.
   *
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Returns the number of columns on the board.
   *
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }
  
  /**
   * Gets the mark at a given position.
   *
   * @param pos position
   * @return mark
   */
  public Mark get(final Position pos) {
    return cells[pos.getRow()][pos.getCol()];
  }
  
  /**
   * Returns a list of legal move positions according to the rules.
   * - Must be empty cell
   * - If board is empty, only center cell(s) are legal
   * - Otherwise must touch (8-neighborhood) at least one non-empty cell
   *
   * @return immutable list of positions
   */
  public List<Position> legalMoves() {
    List<Position> result = new ArrayList<>();
    boolean empty = isEmpty();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] != Mark.EMPTY) {
          continue;
        }
        if (empty) {
          if (isCenterCell(r, c)) {
            result.add(new Position(r, c));
          }
        } else {
          if (touchesAnyMark(r, c)) {
            result.add(new Position(r, c));
          }
        }
      }
    }
    return Collections.unmodifiableList(result);
  }
  
  private boolean isCenterCell(final int r, final int c) {
    int r0 = (rows - 1) / 2;
    int r1 = rows / 2;
    int c0 = (cols - 1) / 2;
    int c1 = cols / 2;
    return (r == r0 || r == r1) && (c == c0 || c == c1);
  }
  
  private boolean touchesAnyMark(final int r, final int c) {
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int nr = r + dr;
        int nc = c + dc;
        if (inBounds(nr, nc) && cells[nr][nc] != Mark.EMPTY) {
          return true;
        }
      }
    }
    return false;
  }
  
  private boolean inBounds(final int r, final int c) {
    return r >= 0 && r < rows && c >= 0 && c < cols;
  }
  
  /**
   * Places a mark at a position if legal and returns a new Board instance.
   *
   * @param pos position
   * @param mark mark to place
   * @return new Board with the move applied
   * @throws IllegalArgumentException if move is illegal
   */
  public Board place(final Position pos, final Mark mark) {
    Objects.requireNonNull(pos, "pos");
    Objects.requireNonNull(mark, "mark");
    if (!inBounds(pos.getRow(), pos.getCol())) {
      throw new IllegalArgumentException("Out of bounds");
    }
    if (cells[pos.getRow()][pos.getCol()] != Mark.EMPTY) {
      throw new IllegalArgumentException("Cell not empty");
    }
    // Rule checks
    List<Position> legal = legalMoves();
    if (!legal.contains(pos)) {
      throw new IllegalArgumentException("Illegal move according to adjacency/center rule");
    }
    Mark[][] copy = copyCells();
    copy[pos.getRow()][pos.getCol()] = mark;
    return new Board(rows, cols, copy);
  }
  
  private Mark[][] copyCells() {
    Mark[][] copy = new Mark[rows][cols];
    for (int r = 0; r < rows; r++) {
      System.arraycopy(cells[r], 0, copy[r], 0, cols);
    }
    return copy;
  }
  
  /**
   * Checks if the given mark has a four-in-a-row on this board.
   *
   * @param mark mark to evaluate
   * @return true if the mark has 4 contiguous marks in any direction
   */
  public boolean hasWon(final Mark mark) {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] != mark) {
          continue;
        }
        if (checkDir(r, c, 1, 0, mark)) {
          return true; // horizontal
        }
        if (checkDir(r, c, 0, 1, mark)) {
          return true; // vertical
        }
        if (checkDir(r, c, 1, 1, mark)) {
          return true; // diag down-right
        }
        if (checkDir(r, c, 1, -1, mark)) {
          return true; // diag down-left
        }
      }
    }
    return false;
  }
  
  private boolean checkDir(final int r, final int c, final int dr, final int dc, final Mark m) {
    for (int k = 1; k < 4; k++) { // need 3 more after the first
      int nr = r + dr * k;
      int nc = c + dc * k;
      if (!inBounds(nr, nc) || cells[nr][nc] != m) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Checks whether the board contains no marks.
   *
   * @return true if no cell contains a mark.
   */
  public boolean isEmpty() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        if (cells[r][c] != Mark.EMPTY) {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Checks whether no legal moves remain on this board.
   *
   * @return true if no legal moves remain.
   */
  public boolean isStuck() {
    return legalMoves().isEmpty();
  }

  /**
   * Serializes the board to a simple text file format:
   * First line: rows cols
   * Next rows lines: exactly cols characters using '.', 'x', 'o'
   *
   * @param path path to write
   * @throws IOException if IO fails
   */
  public void saveToFile(final Path path) throws IOException {
    try (BufferedWriter w = Files.newBufferedWriter(path)) {
      w.write(rows + " " + cols);
      w.newLine();
      for (int r = 0; r < rows; r++) {
        StringBuilder sb = new StringBuilder(cols);
        for (int c = 0; c < cols; c++) {
          sb.append(cells[r][c].getSymbol());
        }
        w.write(sb.toString());
        w.newLine();
      }
    }
  }

  /**
   * Loads a board from a text file saved by {@link #saveToFile(Path)}.
   *
   * @param path path to read
   * @return new Board
   * @throws IOException if IO fails or format invalid
   */
  public static Board loadFromFile(final Path path) throws IOException {
    try (BufferedReader r = Files.newBufferedReader(path)) {
      String header = r.readLine();
      if (header == null) {
        throw new IOException("Empty file");
      }
      String[] parts = header.trim().split("\\s+");
      if (parts.length < 2) {
        throw new IOException("Header must contain rows and cols");
      }
      int rows = Integer.parseInt(parts[0]);
      int cols = Integer.parseInt(parts[1]);
      Mark[][] data = new Mark[rows][cols];
      for (int i = 0; i < rows; i++) {
        String line = r.readLine();
        if (line == null || line.length() < cols) {
          throw new IOException("Invalid board row at line " + (i + 2));
        }
        for (int j = 0; j < cols; j++) {
          data[i][j] = Mark.fromChar(line.charAt(j));
        }
      }
      return new Board(rows, cols, data);
    }
  }

  /**
   * Renders the board with column letters and row numbers for console display.
   *
   * @return multi-line string
   */
  public String render() {
    StringBuilder sb = new StringBuilder();
    sb.append("   ");
    for (int c = 0; c < cols; c++) {
      sb.append((char) ('a' + c)).append(' ');
    }
    sb.append('\n');
    for (int r = 0; r < rows; r++) {
      String num = String.valueOf(r + 1);
      if (rows >= 10 && r + 1 < 10) {
        sb.append(' ');
      }
      sb.append(num).append(' ');
      if (num.length() == 1) {
        sb.append(' ');
      }
      for (int c = 0; c < cols; c++) {
        sb.append(cells[r][c].getSymbol()).append(' ');
      }
      sb.append('\n');
    }
    return sb.toString();
  }
}
