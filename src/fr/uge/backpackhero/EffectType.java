package fr.uge.backpackhero;

/**
 * Represents the different types of status effects. Effects can be positive or
 * negative and may decrease over time.
 */
public enum EffectType {

	/** Deals damage at the start of each turn */
	BURN(true, false),

	/** Deals damage at the end of each turn */
	POISON(true, false),

	/** Adds bonus damage to attacks */
	FREEZE(true, false),

	/** Reduces damage dealt */
	WEAK(true, false),

	/** Increases damage dealt */
	RAGE(true, true),

	/** Increases shield gained */
	HASTE(true, true),

	/** Reduces shield gained */
	SLOW(true, false),

	/** Heals at the start of each turn */
	REGEN(true, true),

	/** Allows dodging attacks */
	DODGE(true, true),

	/** Reflects damage to attackers */
	SPIKES(true, true),

	/** Prevents taking actions */
	SLEEP(true, false),

	/** Charms the fighter when stacks exceed HP */
	CHARM(true, false),

	/** Generic curse effect */
	CURSE(false, false),

	/** Reduces projectile damage taken */
	ROUGH_HIDE(false, true),

	/** Converts healing into damage */
	ZOMBIE(false, false);

	private final boolean decrease;
	private final boolean isPositive;

	/**
	 * Creates an effect type.
	 * 
	 * @param decrease   whether the effect decreases each turn
	 * @param isPositive whether the effect is beneficial
	 */
	EffectType(boolean decrease, boolean isPositive) {
		this.decrease = decrease;
		this.isPositive = isPositive;
	}

	/**
	 * Checks if this effect decreases over time.
	 * 
	 * @return true if the effect decreases
	 */
	public boolean decrease() {
		return decrease;
	}

	/**
	 * Checks if this effect is positive.
	 * 
	 * @return true if beneficial
	 */
	public boolean isPositive() {
		return isPositive;
	}

	/**
	 * Returns the display name of this effect.
	 * 
	 * @return the effect name
	 */
	public String getName() {
		return switch (this) {
		case BURN -> "Brulure";
		case POISON -> "Poison";
		case FREEZE -> "Gel";
		case WEAK -> "Faiblesse";
		case RAGE -> "Rage";
		case HASTE -> "Hate";
		case SLOW -> "Lenteur";
		case REGEN -> "Regeneration";
		case DODGE -> "Esquive";
		case SPIKES -> "Epines";
		case SLEEP -> "Sommeil";
		case CHARM -> "Charme";
		case CURSE -> "Malediction";
		case ROUGH_HIDE -> "Peau Dure";
		case ZOMBIE -> "Zombie";
		};
	}
}
