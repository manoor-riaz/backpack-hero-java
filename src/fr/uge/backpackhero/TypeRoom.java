package fr.uge.backpackhero;

/**
 * Represents the different types of rooms in the dungeon.
 */
public enum TypeRoom {
	/**
	 * An empty corridor room.
	 */
	CORRIDOR,

	/**
	 * A room with enemies to fight.
	 */
	ENEMY,

	/**
	 * A room with a merchant to buy and sell items.
	 */
	MERCHANT,

	/**
	 * A room with a healer to restore health.
	 */
	HEALER,

	/**
	 * A room with treasure to loot.
	 */
	TREASURE,

	/**
	 * A room with a surprise encounter.
	 */
	SURPRISE,

	/**
	 * The exit to the next floor.
	 */
	EXIT;

}
