package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Represents an action that an enemy plans to perform. Contains the type of
 * action and its associated value, curse, or effect.
 */
public class ActionEnnemi {

	private final Action type;
	private final int valeur;
	private final Curses curse;
	private final EffectType effect;
	private final int effectAcc;

	/**
	 * Creates a new enemy action with all parameters.
	 * 
	 * @param type      the type of action (attack, defense, curse, or effect)
	 * @param valeur    the value of the action (damage or shield points)
	 * @param curse     the curse to cast, or null if not a curse action
	 * @param effect    the status effect to apply, or null if not an effect action
	 * @param effectAcc the number of effect stacks to apply
	 * @throws NullPointerException     if type is null
	 * @throws IllegalArgumentException if valeur or effectAcc is negative
	 */
	public ActionEnnemi(Action type, int valeur, Curses curse, EffectType effect, int effectAcc) {
		this.type = Objects.requireNonNull(type);
		if (valeur < 0) {
			throw new IllegalArgumentException("The value of the action must be >= 0");
		}
		this.valeur = valeur;
		this.curse = curse;
		this.effect = effect;
		if (effectAcc < 0) {
			throw new IllegalArgumentException("The value of the effect account must be >= 0");
		}
		this.effectAcc = effectAcc;
	}

	/**
	 * Creates a new enemy action without curse or effect.
	 * 
	 * @param type   the type of action (attack or defense)
	 * @param valeur the value of the action (damage or shield points)
	 * @throws NullPointerException     if type is null
	 * @throws IllegalArgumentException if valeur is negative
	 */
	public ActionEnnemi(Action type, int valeur) {
		this(type, valeur, null, null, 0);
	}

	/**
	 * Creates a new enemy curse action.
	 * 
	 * @param curse the curse to cast
	 * @throws NullPointerException if curse is null
	 */
	public ActionEnnemi(Curses curse) {
		this(Action.CURSE, 0, Objects.requireNonNull(curse), null, 0);
	}

	/**
	 * Creates a new enemy effect action.
	 * 
	 * @param effect the status effect to apply
	 * @param acc    the number of stacks to apply
	 * @throws NullPointerException     if effect is null
	 * @throws IllegalArgumentException if acc is negative
	 */
	public ActionEnnemi(EffectType effect, int acc) {
		this(Action.EFFECT, 0, null, Objects.requireNonNull(effect), acc);
	}

	/**
	 * Returns the type of action.
	 * 
	 * @return the action type
	 */
	public Action type() {
		return type;
	}

	/**
	 * Returns the value of the action.
	 * 
	 * @return the damage or shield value
	 */
	public int valeur() {
		return valeur;
	}

	/**
	 * Returns the curse associated with this action.
	 * 
	 * @return the curse, or null if not a curse action
	 */
	public Curses curse() {
		return curse;
	}

	/**
	 * Returns the effect type associated with this action.
	 * 
	 * @return the effect type, or null if not an effect action
	 */
	public EffectType effect() {
		return effect;
	}

	/**
	 * Returns the number of effect stacks.
	 * 
	 * @return the effect stack count
	 */
	public int effectAcc() {
		return effectAcc;
	}
}