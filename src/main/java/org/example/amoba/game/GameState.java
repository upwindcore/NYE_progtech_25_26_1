package org.example.amoba.game;

/**
 * Represents the current outcome of a game.
 */
public enum GameState {
  /** Game continues. */
  IN_PROGRESS,
  /** Human (X) won. */
  X_WON,
  /** Computer (O) won. */
  O_WON,
  /** No legal moves remain and no winner. */
  DRAW
}
