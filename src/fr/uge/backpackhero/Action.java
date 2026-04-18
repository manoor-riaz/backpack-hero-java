package fr.uge.backpackhero;

/**
 * Represents the type of action that can be performed in combat. Actions
 * determine what an enemy will do during their turn.
 */
public enum Action {
	/**
	 * Attack action to deal damage to enemies.
	 */
	ATTACK,

	/**
	 * Defense action to protect the hero.
	 */
	DEFENSE,

	/**
	 * Curse action to inflict a curse on the hero.
	 */
	CURSE,

	/**
	 * Status effect action to apply a status effect to the hero.
	 */
	EFFECT
}