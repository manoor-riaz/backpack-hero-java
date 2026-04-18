package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Represents an item and its position in the backpack.
 *
 * @param item     : the item
 * @param position : the position in the backpack
 */
public record ItemPosition(Item item, Position position) {

	/**
	 * Creates a new item position.
	 *
	 * @param item     : the item to store
	 * @param position : the position where the item is placed
	 * @throws NullPointerException : if item or position is null
	 */
	public ItemPosition {
		Objects.requireNonNull(item);
		Objects.requireNonNull(position);
	}
}
