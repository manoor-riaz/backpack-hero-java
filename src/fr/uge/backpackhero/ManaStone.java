package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a mana stone that provides mana to the hero. Mana stones are
 * passive items that cannot be used in combat.
 */
public final class ManaStone implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;
	private final int mana;

	/**
	 * Creates a new mana stone.
	 * 
	 * @param rarity : the rarity of the mana stone
	 * @param mana   : the amount of mana provided
	 * @throws IllegalArgumentException : if mana is negative
	 */
	public ManaStone(Rarity rarity, int mana) {
		Objects.requireNonNull(rarity);
		if (mana < 0) {
			throw new IllegalArgumentException("Mana have to be positive");
		}
		this.name = "Mana Stone";
		this.rarity = rarity;
		this.shape = new Shape(List.of(new Position(0, 0)));
		this.mana = mana;
	}

	/**
	 * Returns the name of the mana stone.
	 * 
	 * @return : always "Mana Stone"
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of the mana stone.
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
	 * Returns the amount of mana provided by this stone.
	 * 
	 * @return : the mana amount
	 */
	public int mana() {
		return mana;
	}

	/**
	 * Attempts to use the mana stone in combat.
	 * 
	 * @param hero  : the hero
	 * @param enemy : the enemy
	 * @throws UnsupportedOperationException : mana stones are passive items
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(enemy);

		throw new UnsupportedOperationException("Mana stones are passive items");
	}

	/**
	 * Checks if this item is a mana stone.
	 * 
	 * @return : always true for mana stones
	 */
	@Override
	public boolean isManaStone() {
		return true;
	}
}