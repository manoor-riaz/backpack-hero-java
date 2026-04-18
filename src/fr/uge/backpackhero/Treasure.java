package fr.uge.backpackhero;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a treasure containing a collection of items. Items can be removed
 * from the treasure as the hero collects them. A treasure must always be
 * created with at least one item.
 */
public final class Treasure {
	private final ArrayList<Item> items;

	/**
	 * Private constructor that creates a treasure with the given items.
	 * 
	 * @param items the list of items in the treasure
	 * @throws NullPointerException     if items is null
	 * @throws IllegalArgumentException if items is empty
	 */
	private Treasure(List<Item> items) {
		Objects.requireNonNull(items);
		if (items.isEmpty()) {
			throw new IllegalArgumentException("Treasure must contain at least one item");
		}
		this.items = new ArrayList<>(items);
	}

	/**
	 * Factory method to create a treasure with the given items. Creates an
	 * immutable copy of the provided list.
	 * 
	 * @param items the list of items to include in the treasure
	 * @return a new Treasure containing the items
	 * @throws NullPointerException     if items is null
	 * @throws IllegalArgumentException if items is empty
	 */
	public static Treasure of(List<Item> items) {
		return new Treasure(List.copyOf(Objects.requireNonNull(items)));
	}

	/**
	 * Returns an immutable copy of the items in the treasure.
	 * 
	 * @return a list of items currently in the treasure
	 */
	public List<Item> items() {
		return List.copyOf(items);
	}

	/**
	 * Checks if the treasure is empty (all items have been collected).
	 * 
	 * @return true if the treasure contains no items, false otherwise
	 */
	public boolean isEmpty() {
		return items.isEmpty();
	}

	/**
	 * Checks if the treasure contains a specific item.
	 * 
	 * @param item the item to search for
	 * @return true if the item is in the treasure, false otherwise
	 * @throws NullPointerException if item is null
	 */
	public boolean contains(Item item) {
		Objects.requireNonNull(item);
		return items.contains(item);
	}

	/**
	 * Removes an item from the treasure. This is typically called when the hero
	 * collects an item.
	 * 
	 * @param item the item to remove
	 * @return true if the item was removed, false if it wasn't in the treasure
	 * @throws NullPointerException if item is null
	 */
	public boolean remove(Item item) {
		Objects.requireNonNull(item);
		return items.remove(item);
	}
}