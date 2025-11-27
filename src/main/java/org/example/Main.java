package org.example;

import org.example.amoba.cli.ConsoleUi;

/**
 * Application entrypoint.
 */
public final class Main {

  private Main() {
    // Utility class
  }

  /**
   * Starts the Gomoku terminal application.
   *
   * @param args command-line arguments (unused)
   */
  public static void main(String[] args) {
    new ConsoleUi().start();
  }
}
