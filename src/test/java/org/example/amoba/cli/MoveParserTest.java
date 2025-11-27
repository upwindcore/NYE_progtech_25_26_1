package org.example.amoba.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.example.amoba.model.Position;
import org.junit.jupiter.api.Test;

/**
 * Tests for MoveParser.
 */
public class MoveParserTest {

  @Test
  void testSimpleParse() {
    Position p = MoveParser.parse("a1");
    assertEquals(new Position(0, 0), p);

    Position p2 = MoveParser.parse("j10");
    assertEquals(new Position(9, 9), p2);
  }

  @Test
  void testInvalid() {
    assertNull(MoveParser.parse(""));
    assertNull(MoveParser.parse("1a"));
    assertNull(MoveParser.parse("a0"));
    assertNull(MoveParser.parse(null));
  }
}
