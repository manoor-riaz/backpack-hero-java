package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a grid cell in the backpack. Each cell can be locked or unlocked.
 * A locked cell requires a key to be unlocked.
 */
public class Grid {
	private boolean unlocked;

	/**
	 * Creates a new grid cell. By default, the cell is locked.
	 */
	public Grid() {
		this.unlocked = false;
	}

	/**
	 * Checks if the cell is unlocked.
	 * 
	 * @return true if the cell is unlocked, false otherwise
	 */
	public boolean isUnlocked() {
		return unlocked;
	}

	/**
	 * Unlocks the cell using a key from the hero. If the cell is already unlocked,
	 * returns true without consuming a key. If the hero has a key, consumes it and
	 * unlocks the cell.
	 * 
	 * @param hero the hero attempting to unlock the cell
	 * @return true if the cell is unlocked (or was already unlocked), false if the
	 *         hero has no key
	 * @throws NullPointerException if hero is null
	 */
	public boolean unlock(Hero hero) {
		Objects.requireNonNull(hero);

		if (unlocked) {
			return true;
		}

		if (hero.backpack().hasKey()) {
			hero.backpack().removeKey();
			unlocked = true;
			return true;
		}

		return false;
	}
}