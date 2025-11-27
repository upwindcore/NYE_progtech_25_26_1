package org.example.amoba.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import org.example.amoba.game.Game;
import org.example.amoba.game.GameState;
import org.example.amoba.model.Board;
import org.example.amoba.model.Mark;
import org.example.amoba.model.Position;
import org.example.amoba.persistence.HighScoreRepository;
import org.example.amoba.persistence.ScoreEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple console UI for the game.
 */
public final class ConsoleUi {

  private static final Logger LOG = LoggerFactory.getLogger(ConsoleUi.class);
  private static final Path DEFAULT_SAVE = Path.of("game.txt");

  private final Scanner scanner = new Scanner(System.in);
  private final HighScoreRepository repo = new HighScoreRepository("jdbc:sqlite:amoba.db");

  private String playerName = "Player";
  private Game game;

  /**
   * Starts the UI loop.
   */
  public void start() {
    System.out.println("Üdv az Amőba játékban!");
    askPlayerName();
    ensureDatabase();
    loadInitialBoard();
    mainMenu();
  }

  private void askPlayerName() {
    System.out.print("Add meg a játékos neved: ");
    String name = scanner.nextLine().trim();
    if (!name.isEmpty()) {
      playerName = name;
    }
  }

  private void ensureDatabase() {
    try {
      repo.initialize();
    } catch (SQLException e) {
      LOG.error("Nem sikerült inicializálni az adatbázist", e);
      System.out.println("Az adatbázis nem elérhető!");
    }
  }

  private void loadInitialBoard() {
    Board board;
    if (Files.exists(DEFAULT_SAVE)) {
      try {
        board = Board.loadFromFile(DEFAULT_SAVE);
        System.out.println("Játék betöltve a fájlból: " + DEFAULT_SAVE);
      } catch (IOException e) {
        LOG.warn("Nem sikerült betölteni a mentést, új játék indul.", e);
        board = new Board(10, 10);
      }
    } else {
      board = new Board(10, 10);
    }
    game = new Game(board);
  }

  private void mainMenu() {
    System.out.println("Parancsok: new, load <fájl>, save <fájl>, move <mező> (pl. b3), show, highscore, quit");
    showBoard();
    while (true) {
      System.out.print("> ");
      String line = scanner.nextLine();
      if (line == null) {
        return;
      }
      handleCommand(line.trim());
    }
  }

  private void handleCommand(final String input) {
    if (input.isEmpty()) {
      return;
    }
    String[] parts = input.split("\\s+", 2);
    String cmd = parts[0].toLowerCase(Locale.ROOT);
    String arg = parts.length > 1 ? parts[1].trim() : "";
    try {
      switch (cmd) {
        case "new":
          game = new Game(10, 10);
          showBoard();
          break;
        case "load":
          loadCommand(arg);
          break;
        case "save":
          saveCommand(arg);
          break;
        case "show":
          showBoard();
          break;
        case "move":
          moveCommand(arg);
          break;
        case "highscore":
          printHighScores();
          break;
        case "quit":
          System.out.println("Kilépés.");
          System.exit(0);
          break;
        default:
          System.out.println("Ismeretlen parancs.");
      }
    } catch (Exception e) {
      LOG.debug("Hiba a parancs feldolgozása közben", e);
      System.out.println("Hiba: " + e.getMessage());
    }
  }

  private void loadCommand(final String arg) {
    if (arg.isEmpty()) {
      System.out.println("Használat: load <fájl>");
      return;
    }
    try {
      Board b = Board.loadFromFile(Path.of(arg));
      game = new Game(b);
      System.out.println("Mentés betöltve.");
      showBoard();
    } catch (IOException e) {
      System.out.println("Nem sikerült betölteni a mentést: " + e.getMessage());
    }
  }

  private void saveCommand(final String arg) {
    if (arg.isEmpty()) {
      System.out.println("Használat: save <fájl>");
      return;
    }
    try {
      game.getBoard().saveToFile(Path.of(arg));
      System.out.println("Játék elmentve.");
    } catch (IOException e) {
      System.out.println("Nem sikerült menteni a játékot: " + e.getMessage());
    }
  }

  private void moveCommand(final String arg) {
    if (arg.isEmpty()) {
      System.out.println("Használat: move <mező>, pl. b3");
      return;
    }
    Position pos = MoveParser.parse(arg);
    if (pos == null) {
      System.out.println("Érvénytelen mező formátum.");
      return;
    }
    if (pos.getRow() >= game.getBoard().getRows() || pos.getCol() >= game.getBoard().getCols()) {
      System.out.println("A megadott mező a táblán kívül van.");
      return;
    }
    if (game.getCurrent() != Mark.X) {
      System.out.println("Most nem a játékos köre van.");
      return;
    }
    try {
      game.humanMove(pos);
      showBoard();
      GameState st = game.state();
      if (st == GameState.X_WON) {
        System.out.println("Gratulálunk, nyertél!");
        safeRecordWin(playerName);
        return;
      }
      if (st == GameState.DRAW) {
        System.out.println("Döntetlen. Nincs több lépés.");
        return;
      }
      // AI move
      Position ai = game.aiMove();
      if (ai != null) {
        System.out.println("Gép lépése: " + coord(ai));
      }
      showBoard();
      st = game.state();
      if (st == GameState.O_WON) {
        System.out.println("A gép nyert.");
      } else if (st == GameState.DRAW) {
        System.out.println("Döntetlen. Nincs több lépés.");
      }
    } catch (IllegalArgumentException | IllegalStateException ex) {
      System.out.println("Érvénytelen lépés: " + ex.getMessage());
    }
  }

  private void safeRecordWin(final String name) {
    try {
      repo.incrementWins(Objects.requireNonNull(name));
    } catch (SQLException e) {
      LOG.warn("Nem sikerült menteni a győzelmet az adatbázisba", e);
    }
  }

  private void printHighScores() {
    try {
      List<ScoreEntry> all = repo.findAllOrderByWinsDesc();
      System.out.println("Név           Győzelmek");
      for (ScoreEntry s : all) {
        System.out.printf("%-12s %d%n", s.name(), s.wins());
      }
    } catch (SQLException e) {
      System.out.println("Nem sikerült betölteni a toplistát.");
      LOG.warn("High score lekérdezés hiba", e);
    }
  }

  private void showBoard() {
    System.out.println(game.getBoard().render());
  }

  private String coord(final Position p) {
    return (char) ('a' + p.getCol()) + Integer.toString(p.getRow() + 1);
  }
}
