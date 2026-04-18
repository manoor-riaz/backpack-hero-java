package fr.uge.backpackhero;

import module java.base;

/**
 * Represents gold currency in the game. Gold can be spent and added to.
 */
public final class Gold implements Item {

	private final Rarity rarity;
	private final Shape shape;
	private int purse;

	/**
	 * Creates a new gold item with the specified amount.
	 * 
	 * @param gold : the initial amount of gold
	 * @throws IllegalArgumentException : if gold is negative
	 */
	public Gold(int gold) {
		if (gold < 0) {
			throw new IllegalArgumentException("Gold amount cannot be negative");
		}

		this.rarity = Rarity.COMMON;
		this.shape = new Shape(List.of(new Position(0, 0)));
		this.purse = gold;
	}

	/**
	 * Returns the name with the current gold amount.
	 * 
	 * @return : the name showing the amount of gold
	 */
	@Override
	public String name() {
		return purse + " Gold";
	}

	/**
	 * Returns the rarity of gold.
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
	 * Returns the current amount of gold.
	 * 
	 * @return : the gold amount
	 */
	@Override
	public int purse() {
		return purse;
	}

	/**
	 * Spends the specified amount of gold.
	 * 
	 * @param amount : the amount to spend
	 * @return : true if enough gold was available, false otherwise
	 * @throws IllegalArgumentException : if amount is negative
	 */
	public boolean spend(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can't be negative");
		}
		if (purse < amount) {
			return false;
		}
		purse -= amount;
		return true;
	}

	/**
	 * Adds the specified amount of gold.
	 * 
	 * @param amount : the amount to add
	 * @throws IllegalArgumentException : if amount is negative
	 */
	public void add(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount can't be negative");
		}
		purse += amount;
	}

	/**
	 * Attempts to use gold in combat.
	 * 
	 * @param hero  : the hero
	 * @param enemy : the enemy
	 * @throws UnsupportedOperationException : gold cannot be used in combat
	 */
	@Override
	public void use(Hero hero, EnemyBase enemy) {
		throw new UnsupportedOperationException("You can't use gold in combat");
	}

	/**
	 * Checks if this item is gold.
	 * 
	 * @return : always true for gold
	 */
	@Override
	public boolean isGold() {
		return true;
	}

}