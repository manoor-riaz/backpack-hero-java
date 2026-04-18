package fr.uge.backpackhero;

import java.util.List;
import java.util.Objects;

/**
 * Represents a key item used to unlock locked rooms. Keys cannot be used in
 * combat or rotated.
 */
public final class Key implements Item {

	private final Rarity rarity;
	private final Shape shape;

	/**
	 * Creates a new key item.
	 */
	public Key() {
		this.rarity = Rarity.COMMON;
		this.shape = new Shape(List.of(new Position(0, 0)));
	}

	/**
	 * Returns the name of the key.
	 * 
	 * @return : always "Key"
	 */
	@Override
	public String name() {
		return "Key";
	}

	/**
	 * Returns the rarity of the key.
	 * 
	 * @return : always COMMON
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
	 * Attempts to use the key in combat.
	 * 
	 * @param hero  : the hero
	 * @param enemy : the enemy
	 * @throws UnsupportedOperationException : keys cannot be used in combat
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);

		throw new UnsupportedOperationException("You can't use key in combat");
	}

	/**
	 * Checks if this item is a key.
	 * 
	 * @return : always true for keys
	 */
	@Override
	public boolean isKey() {
		return true;
	}
}