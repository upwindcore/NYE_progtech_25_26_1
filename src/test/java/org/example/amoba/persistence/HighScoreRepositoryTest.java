package org.example.amoba.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link HighScoreRepository} using a temporary SQLite file database.
 */
public class HighScoreRepositoryTest {

  @Test
  void testInitializeUpsertAndOrdering() throws SQLException, IOException {
    Path db = Files.createTempFile("hsrepo", ".db");
    try {
      String url = "jdbc:sqlite:" + db.toString();
      HighScoreRepository repo = new HighScoreRepository(url);

      repo.initialize();

      // Insert some wins; ensure upsert on conflict increases counter
      repo.incrementWins("Alice");
      repo.incrementWins("Bob");
      repo.incrementWins("Alice"); // Alice should have 2 now
      repo.incrementWins("Amy");   // Same wins as Bob to test secondary ordering by name

      List<ScoreEntry> all = repo.findAllOrderByWinsDesc();

      // Expect order by wins desc, then name asc: Alice(2), Amy(1), Bob(1)
      assertEquals(3, all.size());
      assertEquals("Alice", all.get(0).name());
      assertEquals(2, all.get(0).wins());

      assertEquals("Amy", all.get(1).name());
      assertEquals(1, all.get(1).wins());

      assertEquals("Bob", all.get(2).name());
      assertEquals(1, all.get(2).wins());
    } finally {
      Files.deleteIfExists(db);
    }
  }
}
