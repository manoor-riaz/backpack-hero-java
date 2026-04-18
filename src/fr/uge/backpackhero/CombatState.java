package fr.uge.backpackhero;

/**
 * Represents the different states of a combat. Determines whose turn it is and
 * whether combat has ended.
 */
public enum CombatState {

	/**
	 * The player is choosing their action.
	 */
	PLAYER_TURN,

	/**
	 * The enemies are performing their actions.
	 */
	ENEMY_TURN,

	/**
	 * The hero won the combat.
	 */
	VICTORY,

	/**
	 * The hero lost the combat.
	 */
	DEFEAT
}