package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a curse item that takes space in the backpack. Curses cannot be
 * used in combat or rotated. They are negative items placed by enemies that
 * occupy backpack space.
 */
public final class Curses implements Item {

	private final String name;
	private final Rarity rarity;
	private final Shape shape;

	/**
	 * Creates a new curse item.
	 * 
	 * @param name   the name of the curse
	 * @param rarity the rarity level
	 * @param shape  the shape of the curse
	 * @throws NullPointerException if any parameter is null
	 */
	public Curses(String name, Rarity rarity, Shape shape) {
		this.name = Objects.requireNonNull(name);
		this.rarity = Objects.requireNonNull(rarity);
		this.shape = Objects.requireNonNull(shape);
	}

	/**
	 * Returns the name of this curse.
	 * 
	 * @return the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the rarity of this curse.
	 * 
	 * @return the rarity
	 */
	@Override
	public Rarity rarity() {
		return rarity;
	}

	/**
	 * Returns the shape of this curse.
	 * 
	 * @return the shape
	 */
	@Override
	public Shape shape() {
		return shape;
	}

	/**
	 * Checks if this curse can be rotated. Curses cannot be rotated.
	 * 
	 * @return always false for curses
	 */
	@Override
	public boolean canRotate() {
		return false;
	}

	/**
	 * Checks if this item is a curse.
	 * 
	 * @return always true for curses
	 */
	@Override
	public boolean isCurse() {
		return true;
	}

	/**
	 * Attempts to use this curse in combat. Curses cannot be used.
	 * 
	 * @param hero  the hero
	 * @param enemy the enemy
	 * @throws UnsupportedOperationException curses cannot be used in combat
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		throw new UnsupportedOperationException("Cannot use a curse");
	}
}
