package fr.uge.backpackhero;

import java.util.List;
import java.util.Objects;

/**
 * Represents the reward obtained after winning a combat. Contains experience
 * points and loot items.
 *
 * @param xp   the experience points gained
 * @param loot the list of items obtained as loot
 */
public record CombatReward(int xp, List<Item> loot) {

	/**
	 * Creates a new combat reward. The loot list is made immutable.
	 *
	 * @param xp   the experience points rewarded
	 * @param loot the list of items obtained
	 * @throws IllegalArgumentException if xp is negative
	 * @throws NullPointerException     if loot is null
	 */
	public CombatReward {
		if (xp < 0) {
			throw new IllegalArgumentException("XP cannot be negative");
		}
		Objects.requireNonNull(loot);

		loot = List.copyOf(loot);
	}
}