package fr.uge.backpackhero;

/**
 * Represents a combatant in battle. Both heroes and enemies implement this
 * interface.
 */
public sealed interface Fighter permits Hero, EnemyBase {

	/**
	 * Returns the name of this fighter.
	 * 
	 * @return the name
	 */
	String name();

	/**
	 * Returns the current health points.
	 * 
	 * @return the current HP
	 */
	int hp();

	/**
	 * Returns the maximum health points.
	 * 
	 * @return the maximum HP
	 */
	int maxhp();

	/**
	 * Returns the current shield points.
	 * 
	 * @return the current shield
	 */
	int shieldpoint();

	/**
	 * Applies damage to this fighter.
	 * 
	 * @param damage     the damage amount
	 * @param projectile whether the damage is from a projectile
	 */
	void takeDamage(int damage, boolean projectile);

	/**
	 * Applies poison damage that bypasses shield.
	 * 
	 * @param damage the poison damage
	 */
	void takePoisonDamage(int damage);

	/**
	 * Heals this fighter.
	 * 
	 * @param heal the healing amount
	 */
	void healing(int heal);

	/**
	 * Adds shield to this fighter.
	 * 
	 * @param shield the shield amount
	 */
	void takeShield(int shield);

	/**
	 * Returns the effect manager for this fighter.
	 * 
	 * @return the effect manager
	 */
	EffectManager effects();

	/**
	 * Checks if this fighter is alive.
	 * 
	 * @return true if HP is greater than 0
	 */
	default boolean isAlive() {
		return (hp() > 0);
	}
}
