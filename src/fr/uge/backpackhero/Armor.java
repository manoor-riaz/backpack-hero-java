package fr.uge.backpackhero;

import module java.base;

/**
 * Represents an armor item that provides protection to the hero. Armor can be
 * used to gain shield points.
 */
public final class Armor implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final int cost;
	private final Stats stats;
	private final int manaCost;

	/**
	 * Creates a new armor with all parameters.
	 * 
	 * @param name     the name of the armor
	 * @param rarity   the rarity level
	 * @param shape    the shape of the armor
	 * @param cost     the energy cost to use this armor
	 * @param stats    the stats provided by this armor
	 * @param manaCost the mana cost to use this armor
	 * @throws NullPointerException     if any parameter is null
	 * @throws IllegalArgumentException if cost or manaCost is negative
	 */
	public Armor(String name, Rarity rarity, Shape shape, int cost, Stats stats, int manaCost) {
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
	 * Returns the name of this armor.
	 * 
	 * @return the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this armor.
	 * 
	 * @return the rarity
	 */
	@Override
	public Rarity rarity() {
		return rarity;
	}

	/**
	 * Returns the shape of this armor.
	 * 
	 * @return the shape
	 */
	@Override
	public Shape shape() {
		return shape;
	}

	/**
	 * Returns the energy cost to use this armor.
	 * 
	 * @return the cost
	 */
	public int cost() {
		return cost;
	}

	/**
	 * Returns the stats of this armor.
	 * 
	 * @return the stats
	 */
	@Override
	public Stats stats() {
		return stats;
	}

	/**
	 * Returns the mana cost of this armor.
	 * 
	 * @return the mana cost
	 */
	public int manaCost() {
		return manaCost;
	}

	/**
	 * Uses this armor to give shield points to the hero.
	 * 
	 * @param hero  the hero using the armor
	 * @param enemy the enemy (not used for armor)
	 * @throws NullPointerException if hero is null
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);
		hero.takeShield(stats.shield());
	}

	/**
	 * Checks if this item is armor.
	 * 
	 * @return always true for armor
	 */
	@Override
	public boolean isArmor() {
		return true;
	}
}