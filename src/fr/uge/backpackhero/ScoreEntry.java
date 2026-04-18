package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Represents a score entry in the Hall of Fame. Contains the player name,
 * score, and date of achievement. Entries are sorted in descending order by
 * score (highest first).
 * 
 * @param playerName the name of the player
 * @param score      the score achieved
 * @param date       the date when the score was achieved
 */
public record ScoreEntry(String playerName, int score, String date) implements Comparable<ScoreEntry> {

	/**
	 * Compact constructor that validates all parameters are non-null.
	 * 
	 * @throws NullPointerException if playerName or date is null
	 */
	public ScoreEntry {
		Objects.requireNonNull(playerName);
		Objects.requireNonNull(date);
	}

	/**
	 * Compares this score entry with another for ordering. Entries are ordered in
	 * descending order by score (highest score first).
	 * 
	 * @param other the score entry to compare to
	 * @return a negative integer if this score is higher, zero if scores are equal,
	 *         a positive integer if this score is lower
	 */
	@Override
	public int compareTo(ScoreEntry other) {
		return Integer.compare(other.score, this.score);
	}

	/**
	 * Returns a formatted string representation of the score entry.
	 * 
	 * @return a string in the format "playerName - score points - date"
	 */
	@Override
	public String toString() {
		return String.format("%s - %d points - %s", playerName, score, date);
	}
}