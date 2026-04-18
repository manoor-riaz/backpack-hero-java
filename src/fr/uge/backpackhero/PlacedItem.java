package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Represents an item placed in the backpack with its position and orientation.
 * This record encapsulates the item itself, its position in the grid, and its
 * rotation state.
 * 
 * @param item        the item that has been placed
 * @param pos         the position of the item in the backpack grid
 * @param orientation the orientation (rotation) of the item
 */
public record PlacedItem(Item item, Position pos, Orientation orientation) {
	/**
	 * Compact constructor that validates all parameters are non-null.
	 * 
	 * @throws NullPointerException if item, pos, or orientation is null
	 */
	public PlacedItem {
		Objects.requireNonNull(item);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(orientation);
	}
}