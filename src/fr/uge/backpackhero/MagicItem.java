package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a magic item that uses mana to deal damage. Damage is increased by
 * accessory bonuses.
 */
public final class MagicItem implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final Stats stats;
	private final int manaCost;

	/**
	 * Creates a new magic item.
	 * 
	 * @param name      : the name of the magic item
	 * @param rarity    : the rarity level
	 * @param shape     : the shape of the item
	 * @param stats     : the stats provided by this item
	 * @param manaCost  : the mana cost to use this item
	 * @throws IllegalArgumentException : if manaCost is negative
	 */
	public MagicItem(String name, Rarity rarity, Shape shape, Stats stats, int manaCost) {
		this.name = Objects.requireNonNull(name);
		this.rarity = Objects.requireNonNull(rarity);
		this.shape = Objects.requireNonNull(shape);
		if (manaCost < 0) {
			throw new IllegalArgumentException();
		}
		this.stats = Objects.requireNonNull(stats);
		this.manaCost = manaCost;
	}

	/**
	 * Returns the name of this magic item.
	 * 
	 * @return : the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this magic item.
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
	 * Returns the mana cost of this magic item.
	 * 
	 * @return : the mana cost
	 */
	public int manaCost() {
		return manaCost;
	}

	/**
	 * Returns the stats of this magic item.
	 * 
	 * @return : the stats
	 */
	public Stats stats() {
		return stats;
	}

	/**
	 * Uses this magic item to attack an enemy. Total damage includes accessory
	 * bonuses.
	 * 
	 * @param hero  : the hero using the item
	 * @param enemy : the target enemy
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);

		var totalDamage = Math.max(0, stats.damage() + hero.getAccessoryDamageBonus());

		enemy.takeDamage(totalDamage, false);
	}
}