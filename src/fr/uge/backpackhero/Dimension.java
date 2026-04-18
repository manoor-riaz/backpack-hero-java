package fr.uge.backpackhero;

/**
 * Represents the size of an item in the backpack. Both height and width must be
 * positive.
 *
 * @param height the height of the item
 * @param width  the width of the item
 */
public record Dimension(int height, int width) {

	/**
	 * Creates a new dimension.
	 * 
	 * @param height the height of the item
	 * @param width  the width of the item
	 * @throws IllegalArgumentException if height or width is not positive
	 */
	public Dimension {
		if (height <= 0 || width <= 0) {
			throw new IllegalArgumentException("Height and width must be positive");
		}
	}
}