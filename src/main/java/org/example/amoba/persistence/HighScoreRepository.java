package org.example.amoba.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for storing and reading high scores in an SQLite database.
 */
public final class HighScoreRepository {

  private final String url;

  /**
   * Creates a repository for the given JDBC url, e.g. {@code jdbc:sqlite:amoba.db}.
   *
   * @param jdbcUrl jdbc url
   */
  public HighScoreRepository(final String jdbcUrl) {
    this.url = jdbcUrl;
  }

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(url);
  }

  /**
   * Creates the table if it doesn't exist.
   *
   * @throws SQLException if DB error
   */
  public void initialize() throws SQLException {
    try (Connection c = connect();
         PreparedStatement ps = c.prepareStatement(
             "CREATE TABLE IF NOT EXISTS highscore (" +
                 "name TEXT PRIMARY KEY, " +
                 "wins INTEGER NOT NULL DEFAULT 0)")) {
      ps.executeUpdate();
    }
  }

  /**
   * Increments the win counter for the given player (insert if new).
   *
   * @param name player name (non-null)
   * @throws SQLException if DB error
   */
  public void incrementWins(final String name) throws SQLException {
    String sql = "INSERT INTO highscore(name, wins) VALUES(?, 1) "
        + "ON CONFLICT(name) DO UPDATE SET wins = wins + 1";
    try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.executeUpdate();
    }
  }

  /**
   * Retrieves all high score entries ordered by wins descending and then name ascending.
   *
   * @return all high scores ordered by wins desc, then name asc.
   * @throws SQLException if DB error
   */
  public List<ScoreEntry> findAllOrderByWinsDesc() throws SQLException {
    String sql = "SELECT name, wins FROM highscore ORDER BY wins DESC, name ASC";
    List<ScoreEntry> list = new ArrayList<>();
    try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(new ScoreEntry(rs.getString(1), rs.getInt(2)));
        }
      }
    }
    return list;
  }
}
