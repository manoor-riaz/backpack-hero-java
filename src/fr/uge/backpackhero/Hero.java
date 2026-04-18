package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Represents the player's hero character. The hero has health, energy, shield,
 * mana, a backpack, and can level up.
 */
public final class Hero implements Fighter {
	private final String name;
	private int hpmax = 40;
	private int hp;
	private final int epmax = 3;
	private int ep;
	private int shieldpoint;
	private int mp;
	private final BackPack backpack;

	private int level = 1;
	private int xp = 0;

	private int curseNumberRefused = 0;

	private final EffectManager effects;

	/**
	 * Creates a new hero with the specified name.
	 * 
	 * @param name : the hero's name
	 */
	public Hero(String name) {
		Objects.requireNonNull(name);
		this.name = name;
		this.setHp(hpmax);
		this.ep = epmax;
		this.shieldpoint = 0;
		this.mp = 0;
		this.backpack = new BackPack();
		this.effects = new EffectManager();
	}

	/**
	 * Applies damage to the hero. Damage is reduced by passive shield, then
	 * absorbed by shield points, then health.
	 * 
	 * @param damage : the amount of damage to take
	 * @throws IllegalArgumentException : if damage is negative
	 */
	@Override
	public void takeDamage(int damage, boolean projectile) {
		if (damage < 0) {
			throw new IllegalArgumentException("Damage cannot be < 0");
		}

		if (!isAlive()) {
			return;
		}

		if (effects.tryToDodge()) {
			return;
		}

		var realDamage = damage;
		if (projectile && effects.hasRoughHide()) {
			realDamage = realDamage / 2;
		}

		realDamage += effects.freezeDamage();

		var passiveShield = backpack.passiveShield();
		realDamage = Math.max(0, realDamage - passiveShield);

		if (shieldpoint >= realDamage) {
			shieldpoint -= realDamage;
			return;
		}

		var absorbeddamage = realDamage - shieldpoint;
		shieldpoint = 0;
		setHp(Math.max(0, getHp() - absorbeddamage));
	}

	@Override
	public void takePoisonDamage(int damage) {
		if (damage < 0) {
			throw new IllegalArgumentException("Damage cannot be < 0");
		}
		if (!isAlive()) {
			return;
		}
		setHp(Math.max(0, getHp() - damage));
	}

	/**
	 * Uses energy points.
	 * 
	 * @param cost : the amount of energy to use
	 * @throws IllegalArgumentException : if cost is negative
	 * @throws IllegalStateException    : if not enough energy
	 */
	public void useEp(int cost) {
		if (cost < 0) {
			throw new IllegalArgumentException();
		}
		if (ep < cost) {
			throw new IllegalStateException();
		}

		ep -= cost;
	}

	/**
	 * Adds shield points to the hero.
	 * 
	 * @param shield : the amount of shield to add
	 * @throws IllegalArgumentException : if shield is negative
	 */
	@Override
	public void takeShield(int shield) {
		if (shield < 0) {
			throw new IllegalArgumentException("Shield point cannot be negative");
		}
		var realShield = Math.max(0, shield + effects.shieldModifier());
		shieldpoint += realShield;
	}

	/**
	 * Heals the hero.
	 * 
	 * @param heal : the amount of hp to restore
	 * @throws IllegalArgumentException : if heal is negative
	 */
	@Override
	public void healing(int heal) {
		if (heal < 0) {
			throw new IllegalArgumentException("Heal point cannot be negative");
		}

		if (effects.isZombie()) {
			takePoisonDamage(heal);
			return;
		}

		setHp(getHp() + heal);
		if (getHp() > hpmax) {
			setHp(hpmax);
		}
	}

	/**
	 * Resets stats at the start of a new turn. Restores energy, resets shield,
	 * refreshes mana, and applies accessory shield bonus.
	 */
	public void statsNewTurn() {
		ep = epmax;

		shieldpoint = 0;
		mp = backpack.getTotalMana();

		int accessoryShield = getAccessoryShieldBonus();
		if (accessoryShield > 0) {
			takeShield(accessoryShield);
		}
	}

	/**
	 * Increases the maximum hp.
	 * 
	 * @param hp : the amount to increase max hp by
	 * @throws IllegalArgumentException : if hp is negative
	 */
	public void addMaxHp(int hp) {
		if (hp < 0) {
			throw new IllegalArgumentException("You cannot lower the number of max HP");
		}

		hpmax += hp;
		this.setHp(this.getHp() + hp);
	}

	/**
	 * Calculates the total damage bonus from accessories.
	 * 
	 * @return : the total damage bonus
	 */
	public int getAccessoryDamageBonus() {
		var bonus = backpack.items().stream().mapToInt(Item::accessoryDamageBonus).sum();
		return bonus + effects.damageModifier();
	}

	/**
	 * Calculates the total shield bonus from accessories.
	 * 
	 * @return : the total shield bonus
	 */
	public int getAccessoryShieldBonus() {
		return backpack.items().stream().mapToInt(Item::accessoryShieldBonus).sum();
	}

	/**
	 * Calculates the XP needed for the next level.
	 * 
	 * @return : the XP required
	 */
	private int xpForNextLevel() {
		return 5 + (level - 1) * 5;
	}

	/**
	 * Gives XP to the hero and handles level ups.
	 * 
	 * @param amount : the amount of XP to gain
	 */
	public void gainXp(int amount) {
		if (amount <= 0)
			return;

		xp += amount;

		while (xp >= xpForNextLevel()) {
			xp -= xpForNextLevel();
			levelUp();
		}
	}

	/**
	 * Levels up the hero. Increases stats and randomly expands the backpack.
	 */
	private void levelUp() {
		level++;

		hpmax += 5;
		setHp(hpmax);
		ep = epmax;

		var nbCells = (level % 2 == 0) ? 4 : 3;
		backpack.addNbCells(nbCells);
	}

	/**
	 * Returns the XP needed for the next level.
	 * 
	 * @return : the XP required
	 */
	public int xpNeededForNextLevel() {
		return 5 + (level - 1) * 5;
	}

	/**
	 * Returns the hero's current level.
	 * 
	 * @return : the level
	 */
	public int level() {
		return level;
	}

	/**
	 * Returns the hero's current XP.
	 * 
	 * @return : the XP
	 */
	public int xp() {
		return xp;
	}

	/**
	 * Returns the XP remaining until next level.
	 * 
	 * @return : the remaining XP
	 */
	public int xpToNextLevel() {
		return xpForNextLevel() - xp;
	}

	/**
	 * Returns the current hp.
	 * 
	 * @return : the hp
	 */
	@Override
	public int hp() {
		return getHp();
	}

	/**
	 * Returns the maximum hp.
	 * 
	 * @return : the max hp
	 */
	@Override
	public int maxhp() {
		return hpmax;
	}

	/**
	 * Returns the current energy points.
	 * 
	 * @return : the ep
	 */
	public int ep() {
		return ep;
	}

	/**
	 * Returns the current shield points.
	 * 
	 * @return : the shield points
	 */
	@Override
	public int shieldpoint() {
		return shieldpoint;
	}

	/**
	 * Returns the hero's name.
	 * 
	 * @return : the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the hero's backpack.
	 * 
	 * @return : the backpack
	 */
	public BackPack backpack() {
		return backpack;
	}

	/**
	 * Returns the current mana points.
	 * 
	 * @return : the mana points
	 */
	public int mp() {
		return mp;
	}

	/**
	 * Refreshes mana from the backpack's mana stones.
	 */
	public void refreshManaFromBackpack() {
		this.mp = backpack.getTotalMana();
	}

	/** 
	 * Returns the damage taken when refusing the next curse
	 * @return the damage amount
	 */
	public int nextCurseRefusalDamage() {
		return curseNumberRefused + 1;
	}

	/** 
	 * Refuses a curse and returns the damage taken
	 * @return the damage taken
	 */
	public int refuseCurse() {
		curseNumberRefused++;
		var damage = curseNumberRefused;
		takePoisonDamage(damage);
		return damage;
	}

	/** 
	 * Returns the number of curses refused
	 * @return the number of refused curses
	 */
	public int curseNumberRefused() {
		return curseNumberRefused;
	}

	@Override
	public EffectManager effects() {
		return effects;
	}

	/** 
	 * Applies an effect to the hero
	 * @param type the type of effect
	 * @param acc the accumulator value
	 */
	public void applyEffect(EffectType type, int acc) {
		Objects.requireNonNull(type);

		effects.applyEffect(type, acc);
	}

	/** Removes all effects from the hero */
	public void removeEffects() {
		effects.removeAllEffects();
	}

	/** 
	 * Returns the spike damage dealt by the hero
	 * @return the spike damage
	 */
	public int spikeDamage() {
		return effects.spikeDamage();
	}

	/** 
	 * Returns the current health points
	 * @return the health points
	 */
	public int getHp() {
		return hp;
	}

	/** 
	 * Sets the health points
	 * @param hp the new health points
	 */
	public void setHp(int hp) {
		this.hp = hp;
	}

}