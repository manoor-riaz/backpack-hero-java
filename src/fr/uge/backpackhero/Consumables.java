package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a consumable item that can be used once. Consumables can deal
 * damage, heal, or provide shield.
 */
public final class Consumables implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final int cost;
	private final Stats stats;
	private final int manaCost;

	/**
	 * Creates a new consumable item.
	 * 
	 * @param name     the name of the consumable
	 * @param rarity   the rarity level
	 * @param shape    the shape of the consumable
	 * @param cost     the energy cost to use this consumable
	 * @param stats    the stats provided by this consumable
	 * @param manaCost the mana cost to use this consumable
	 * @throws NullPointerException     if any parameter is null
	 * @throws IllegalArgumentException if cost or manaCost is negative
	 */
	public Consumables(String name, Rarity rarity, Shape shape, int cost, Stats stats, int manaCost) {
		this.name = Objects.requireNonNull(name);
		this.rarity = Objects.requireNonNull(rarity);
		this.shape = Objects.requireNonNull(shape);
		if (cost < 0 || manaCost < 0) {
			throw new IllegalArgumentException("Cost and manaCost must be non-negative");
		}
		this.cost = cost;
		this.stats = Objects.requireNonNull(stats);
		this.manaCost = manaCost;
	}

	/**
	 * Returns the name of this consumable.
	 * 
	 * @return the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this consumable.
	 * 
	 * @return the rarity
	 */
	@Override
	public Rarity rarity() {
		return rarity;
	}

	/**
	 * Returns the shape of this consumable.
	 * 
	 * @return the shape
	 */
	@Override
	public Shape shape() {
		return shape;
	}

	/**
	 * Returns the energy cost to use this consumable.
	 * 
	 * @return the cost
	 */
	public int cost() {
		return cost;
	}

	/**
	 * Returns the stats of this consumable.
	 * 
	 * @return the stats
	 */
	@Override
	public Stats stats() {
		return stats;
	}

	/**
	 * Returns the mana cost of this consumable.
	 * 
	 * @return the mana cost
	 */
	public int manaCost() {
		return manaCost;
	}

	/**
	 * Checks if this item is consumable.
	 * 
	 * @return always true for consumables
	 */
	@Override
	public boolean isConsumable() {
		return true;
	}

	/**
	 * Uses this consumable. Applies damage to enemy if present, and healing/shield
	 * to hero.
	 * 
	 * @param hero  the hero using the consumable
	 * @param enemy the target enemy, can be null
	 * @throws NullPointerException if hero is null
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);

		if (stats.damage() > 0 && enemy != null) {
			enemy.takeDamage(stats.damage(), false);
		}

		if (stats.shield() > 0) {
			hero.takeShield(stats.shield());
		}

		if (stats.healing() > 0) {
			hero.healing(stats.healing());
		}
	}
}
