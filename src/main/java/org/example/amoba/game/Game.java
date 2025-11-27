package org.example.amoba.game;

import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.example.amoba.model.Board;
import org.example.amoba.model.Mark;
import org.example.amoba.model.Position;

/**
 * Encapsulates the game state and turn management between human (X) and AI (O).
 */
public final class Game {

  private final Random random;
  private Board board;
  private Mark current; // whose turn is next

  /**
   * Creates a new game with a fresh empty board of the given size.
   * Human (X) starts.
   *
   * @param rows rows
   * @param cols cols
   */
  public Game(final int rows, final int cols) {
    this(new Board(rows, cols));
  }

  /**
   * Creates a game from an existing board.
   *
   * @param board initial board
   */
  public Game(final Board board) {
    this(board, new SecureRandom());
  }

  /**
   * Creates a game with a custom RNG (useful for tests).
   *
   * @param board initial board
   * @param random RNG
   */
  public Game(final Board board, final Random random) {
    this.board = Objects.requireNonNull(board, "board");
    this.random = Objects.requireNonNull(random, "random");
    this.current = Mark.X; // human begins
  }

  /**
   * Returns the current board snapshot.
   *
   * @return current board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Returns whose turn is next.
   *
   * @return whose turn is next
   */
  public Mark getCurrent() {
    return current;
  }

  /**
   * Applies a human move.
   *
   * @param pos position
   */
  public void humanMove(final Position pos) {
    if (current != Mark.X) {
      throw new IllegalStateException("Not human turn");
    }
    board = board.place(pos, Mark.X);
    current = Mark.O;
  }

  /**
   * Lets the AI place a random legal move, if any.
   *
   * @return the position chosen by AI, or null if no moves
   */
  public Position aiMove() {
    if (current != Mark.O) {
      throw new IllegalStateException("Not AI turn");
    }
    List<Position> legal = board.legalMoves();
    if (legal.isEmpty()) {
      current = Mark.X; // hand back, though game is draw
      return null;
    }
    Position choice = legal.get(random.nextInt(legal.size()));
    board = board.place(choice, Mark.O);
    current = Mark.X;
    return choice;
  }

  /**
   * Evaluates the game outcome.
   *
   * @return game state
   */
  public GameState state() {
    if (board.hasWon(Mark.X)) {
      return GameState.X_WON;
    }
    if (board.hasWon(Mark.O)) {
      return GameState.O_WON;
    }
    if (board.isStuck()) {
      return GameState.DRAW;
    }
    return GameState.IN_PROGRESS;
  }
}
