package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a status effect applied to a fighter. Effects have a type and a
 * stack count that determines their strength.
 */
public class Effect {

	private final EffectType effect;
	private int acc;

	/**
	 * Creates a new effect with a specified stack count.
	 * 
	 * @param type the type of effect
	 * @param acc  the number of stacks
	 * @throws NullPointerException     if type is null
	 * @throws IllegalArgumentException if acc is negative
	 */
	public Effect(EffectType type, int acc) {
		this.effect = Objects.requireNonNull(type);
		if (acc < 0) {
			throw new IllegalArgumentException("Stacks have to be sup than 0");
		}
		this.acc = acc;
	}

	/**
	 * Creates a new effect with one stack.
	 * 
	 * @param type the type of effect
	 * @throws NullPointerException if type is null
	 */
	public Effect(EffectType type) {
		this(type, 1);
	}

	/**
	 * Returns the effect type.
	 * 
	 * @return the effect type
	 */
	public EffectType effect() {
		return effect;
	}

	/**
	 * Returns the number of stacks.
	 * 
	 * @return the stack count
	 */
	public int acc() {
		return acc;
	}

	/**
	 * Increases the stack count by the specified amount.
	 * 
	 * @param nbAdded the number of stacks to add
	 * @throws IllegalArgumentException if nbAdded is negative
	 */
	public void increaseAcc(int nbAdded) {
		if (nbAdded < 0) {
			throw new IllegalArgumentException("The parameter cannot be negative");
		}
		this.acc += nbAdded;
	}

	/**
	 * Decreases the stack count by one if the effect decreases.
	 * 
	 * @return true if the effect has no more stacks
	 */
	public boolean decreaseAcc() {
		if (effect.decrease() && acc > 0) {
			acc--;
		}
		return (acc <= 0);
	}

	/**
	 * Checks if the effect is active.
	 * 
	 * @return true if the effect has at least one stack
	 */
	public boolean isActive() {
		return (acc > 0);
	}
}
