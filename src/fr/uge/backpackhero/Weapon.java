package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a weapon item that deals damage to enemies. Damage is increased by
 * accessory bonuses.
 */
public final class Weapon implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final int cost;
	private final Stats stats;
	private final int manaCost;

	/**
	 * Creates a new weapon.
	 * 
	 * @param name     : the name of the weapon
	 * @param rarity   : the rarity level
	 * @param shape    : the shape of the weapon
	 * @param cost     : the energy cost to use this weapon
	 * @param stats    : the stats provided by this weapon
	 * @param manaCost : the mana cost to use this weapon
	 * @throws IllegalArgumentException : if cost is negative
	 */
	public Weapon(String name, Rarity rarity, Shape shape, int cost, Stats stats, int manaCost) {
		this.name = Objects.requireNonNull(name);
		this.rarity = Objects.requireNonNull(rarity);
		this.shape = Objects.requireNonNull(shape);
		if (cost < 0 || manaCost < 0) {
			throw new IllegalArgumentException();
		}
		this.cost = cost;
		this.stats = Objects.requireNonNull(stats);
		this.manaCost = manaCost;
	}

	/**
	 * Returns the name of this weapon.
	 * 
	 * @return : the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this weapon.
	 * 
	 * @return : the rarity
	 */
	@Override
	public Rarity rarity() {
		return rarity;
	}

	@Override
	public Shape shape() {
		return shape;
	}

	/**
	 * Returns the energy cost to use this weapon.
	 * 
	 * @return : the cost
	 */
	public int cost() {
		return cost;
	}

	/**
	 * Returns the stats of this weapon.
	 * 
	 * @return : the stats
	 */
	public Stats stats() {
		return stats;
	}

	/**
	 * Returns the mana cost of this weapon.
	 * 
	 * @return : the mana cost
	 */
	public int manaCost() {
		return manaCost;
	}

	/**
	 * Uses this weapon to attack an enemy. Total damage includes accessory bonuses.
	 * 
	 * @param hero  : the hero using the weapon
	 * @param enemy : the target enemy
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);

		var damage = ItemInteractions.weaponAllDamage(hero, this);
		var effectDamage = hero.effects().damageModifier();
		var neighboursInteraction = ItemInteractions.weaponUse(hero, this, enemy);
		var accessoryDamage = hero.getAccessoryDamageBonus();

		var totalDamage = Math.max(0, damage + effectDamage + neighboursInteraction + accessoryDamage);

		enemy.takeDamage(totalDamage, false);

		if (ItemInteractions.ifRemoveAfterUse(this)) {
			hero.backpack().remove(this);
		}
	}

	@Override
	public boolean isWeapon() {
		return true;
	}

}