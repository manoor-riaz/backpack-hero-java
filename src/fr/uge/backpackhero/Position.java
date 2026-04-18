package fr.uge.backpackhero;

/**
 * Represents a position in a grid with row and column coordinates. Both
 * coordinates must be non-negative.
 *
 * @param row : the row index (must be >= 0)
 * @param col : the column index (must be >= 0)
 */
public record Position(int row, int col) {

	/**
	 * Creates a new position.
	 *
	 * @param row : the row coordinate
	 * @param col : the column coordinate
	 * @throws IllegalArgumentException : if row or col is negative
	 */
	public Position {
		if (row < 0 || col < 0) {
			throw new IllegalArgumentException("Row and col must be non-negative");
		}
	}
}
