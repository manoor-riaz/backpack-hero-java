package fr.uge.backpackhero;

/**
 * Represents the statistics of an item. Contains damage, shield, and healing
 * values.
 *
 * @param damage  : the damage value
 * @param shield  : the shield value
 * @param healing : the healing value
 */
public record Stats(int damage, int shield, int healing) {

	/**
	 * Creates new stats.
	 * 
	 * @param damage  : the damage value
	 * @param shield  : the shield value
	 * @param healing : the healing value
	 * @throws IllegalArgumentException : if any stat is negative
	 */
	public Stats {
		if (damage < 0 || shield < 0 || healing < 0) {
			throw new IllegalArgumentException("You can't have negative stats");
		}
	}
}
