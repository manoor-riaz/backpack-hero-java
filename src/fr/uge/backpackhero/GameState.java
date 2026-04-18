package fr.uge.backpackhero;

/**
 * Represents the different states of the game.
 */
public enum GameState {

	/**
	 * The hero is exploring the dungeon.
	 */
	EXPLORATION,

	/**
	 * The hero is in combat.
	 */
	COMBAT,

	/**
	 * The hero is choosing loot after combat.
	 */
	LOOT,

	/**
	 * The hero is opening a treasure.
	 */
	TREASURE,

	/**
	 * The hero is talking to a merchant.
	 */
	MERCHANT,

	/**
	 * The hero is at the healer.
	 */
	HEALER,

	/**
	 * The hero has died.
	 */
	GAME_OVER,

	/**
	 * The hero has won the game.
	 */
	VICTORY

}