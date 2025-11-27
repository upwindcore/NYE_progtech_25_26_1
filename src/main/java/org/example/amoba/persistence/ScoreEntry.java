package org.example.amoba.persistence;

/**
 * Immutable high-score entry.
 *
 * @param name player name
 * @param wins number of wins
 */
public record ScoreEntry(String name, int wins) { }
