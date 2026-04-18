package fr.uge.backpackhero;

import module java.base;

/**
 * Represents an accessory item that can be used by the hero. An accessory has
 * stats and can be used in combat to deal damage, provide shield, or heal the
 * hero.
 */
public final class Accessories implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final int cost;
	private final Stats stats;
	private final int manaCost;

	/**
	 * Creates a new accessory.
	 * 
	 * @param name     the name of the accessory
	 * @param rarity   the rarity level
	 * @param shape    the shape of the accessory
	 * @param cost     the energy cost to use this accessory
	 * @param stats    the stats provided by this accessory
	 * @param manaCost the mana cost to use this accessory
	 * @throws NullPointerException     if any parameter is null
	 * @throws IllegalArgumentException if cost or manaCost is negative
	 */
	public Accessories(String name, Rarity rarity, Shape shape, int cost, Stats stats, int manaCost) {
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
	 * Returns the name of this accessory.
	 * 
	 * @return the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this accessory.
	 * 
	 * @return the rarity
	 */
	@Override
	public Rarity rarity() {
		return rarity;
	}

	/**
	 * Returns the shape of this accessory.
	 * 
	 * @return the shape
	 */
	@Override
	public Shape shape() {
		return shape;
	}

	/**
	 * Returns the energy cost to use this accessory.
	 * 
	 * @return the cost
	 */
	public int cost() {
		return cost;
	}

	/**
	 * Returns the stats of this accessory.
	 * 
	 * @return the stats
	 */
	public Stats stats() {
		return stats;
	}

	/**
	 * Returns the mana cost of this accessory.
	 * 
	 * @return the mana cost
	 */
	public int manaCost() {
		return manaCost;
	}

	/**
	 * Uses this accessory in combat. Consumes energy, applies damage to the enemy,
	 * and healing/shield to the hero.
	 * 
	 * @param hero  the hero using the accessory
	 * @param enemy the enemy to attack
	 * @throws NullPointerException  if hero or enemy is null
	 * @throws IllegalStateException if the hero doesn't have enough energy
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);

		if (hero.ep() < cost) {
			throw new IllegalStateException("Not enough energy");
		}

		hero.useEp(cost);
		applyDamage(enemy);
		applyShield(hero);
		applyHealing(hero);
	}

	/**
	 * Applies damage to the enemy if the accessory has damage stats.
	 * 
	 * @param enemy the enemy to damage
	 */
	private void applyDamage(EnemyBase enemy) {
		if (stats.damage() > 0) {
			enemy.takeDamage(stats.damage(), false);
		}
	}

	/**
	 * Applies shield to the hero if the accessory has shield stats.
	 * 
	 * @param hero the hero to shield
	 */
	private void applyShield(Hero hero) {
		if (stats.shield() > 0) {
			hero.takeShield(stats.shield());
		}
	}

	/**
	 * Applies healing to the hero if the accessory has healing stats.
	 * 
	 * @param hero the hero to heal
	 */
	private void applyHealing(Hero hero) {
		if (stats.healing() > 0) {
			hero.healing(stats.healing());
		}
	}
}