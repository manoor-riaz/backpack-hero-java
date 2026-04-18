package fr.uge.backpackhero;

import module java.base;
import java.util.List;

/**
 * Manages status effects for a fighter. Handles applying, removing, and
 * processing effects each turn.
 */
public class EffectManager {

	private final HashMap<EffectType, Effect> effects;

	/**
	 * Creates a new effect manager with no effects.
	 */
	public EffectManager() {
		this.effects = new HashMap<>();
	}

	/**
	 * Applies an effect with a specified number of stacks. Some effects like CURSE
	 * only stack once.
	 * 
	 * @param type the effect type to apply
	 * @param acc  the number of stacks to add
	 * @throws NullPointerException if type is null
	 */
	public void applyEffect(EffectType type, int acc) {
		Objects.requireNonNull(type);
		if (acc <= 0) {
			return;
		}

		if (isSingleStackEffect(type)) {
			applySingleStackEffect(type);
		} else {
			applyStackableEffect(type, acc);
		}
	}

	/**
	 * Checks if an effect type only stacks once.
	 * 
	 * @param type the effect type
	 * @return true if the effect only stacks once
	 */
	private boolean isSingleStackEffect(EffectType type) {
		return type == EffectType.CURSE || type == EffectType.ROUGH_HIDE || type == EffectType.ZOMBIE;
	}

	/**
	 * Applies a single-stack effect.
	 * 
	 * @param type the effect type
	 */
	private void applySingleStackEffect(EffectType type) {
		if (!effects.containsKey(type)) {
			effects.put(type, new Effect(type));
		}
	}

	/**
	 * Applies a stackable effect.
	 * 
	 * @param type the effect type
	 * @param acc  the number of stacks to add
	 */
	private void applyStackableEffect(EffectType type, int acc) {
		if (effects.containsKey(type)) {
			effects.get(type).increaseAcc(acc);
		} else {
			effects.put(type, new Effect(type, acc));
		}
	}

	/**
	 * Removes an effect completely.
	 * 
	 * @param type the effect type to remove
	 * @throws NullPointerException if type is null
	 */
	public void removeEffect(EffectType type) {
		Objects.requireNonNull(type);
		effects.remove(type);
	}

	/**
	 * Decreases an effect's stack count by one.
	 * 
	 * @param type the effect type to decrease
	 * @return true if the effect still has stacks remaining
	 * @throws NullPointerException if type is null
	 */
	public boolean decreaseEffect(EffectType type) {
		Objects.requireNonNull(type);
		var effect = effects.get(type);
		if (effect != null) {
			if (effect.decreaseAcc()) {
				removeEffect(type);
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Checks if a fighter has an active effect.
	 * 
	 * @param type the effect type to check
	 * @return true if the effect is active
	 * @throws NullPointerException if type is null
	 */
	public boolean containsEffect(EffectType type) {
		Objects.requireNonNull(type);
		return (effects.containsKey(type) && effects.get(type).isActive());
	}

	/**
	 * Returns the number of stacks of an effect.
	 * 
	 * @param type the effect type
	 * @return the stack count, or 0 if not present
	 * @throws NullPointerException if type is null
	 */
	public int getAcc(EffectType type) {
		Objects.requireNonNull(type);
		var effect = effects.get(type);
		if (effect != null) {
			return effect.acc();
		}
		return 0;
	}

	/**
	 * Returns all active effects.
	 * 
	 * @return list of all effects
	 */
	public List<Effect> allEffects() {
		return new ArrayList<>(effects.values());
	}

	/**
	 * Applies start of turn effects (burn, regen) to a fighter.
	 * 
	 * @param target the fighter to apply effects to
	 * @return total damage dealt
	 * @throws NullPointerException if target is null
	 */
	public int startOfTurnEffects(Fighter target) {
		Objects.requireNonNull(target);
		var totalDamage = 0;

		totalDamage += applyBurnEffect(target);
		applyRegenEffect(target);

		return totalDamage;
	}

	/**
	 * Applies burn effect damage.
	 * 
	 * @param target the fighter to damage
	 * @return the damage dealt
	 */
	private int applyBurnEffect(Fighter target) {
		if (containsEffect(EffectType.BURN)) {
			var burnDamage = getAcc(EffectType.BURN);
			target.takeDamage(burnDamage, false);
			return burnDamage;
		}
		return 0;
	}

	/**
	 * Applies regeneration effect healing.
	 * 
	 * @param target the fighter to heal
	 */
	private void applyRegenEffect(Fighter target) {
		if (containsEffect(EffectType.REGEN)) {
			var regenPoint = getAcc(EffectType.REGEN);
			target.healing(regenPoint);
		}
	}

	/**
	 * Applies end of turn effects (poison) and decreases all effects.
	 * 
	 * @param target the fighter to apply effects to
	 * @return total damage dealt
	 * @throws NullPointerException if target is null
	 */
	public int endOfTurnEffects(Fighter target) {
		Objects.requireNonNull(target);
		var totalDamage = 0;

		if (containsEffect(EffectType.POISON)) {
			var poisonDamage = getAcc(EffectType.POISON);
			target.takePoisonDamage(poisonDamage);
			totalDamage += poisonDamage;
		}

		decreaseAllEffects();

		return totalDamage;
	}

	/**
	 * Decreases all effects by one stack.
	 */
	public void decreaseAllEffects() {
		var effectToRemove = new ArrayList<EffectType>();

		for (var entry : effects.entrySet()) {
			var effect = entry.getValue();
			if (effect.decreaseAcc()) {
				effectToRemove.add(entry.getKey());
			}
		}
		for (var type : effectToRemove) {
			effects.remove(type);
		}
	}

	/**
	 * Removes all effects.
	 */
	public void removeAllEffects() {
		effects.clear();
	}

	/**
	 * Calculates the damage modifier from effects.
	 * 
	 * @return the damage modifier (positive or negative)
	 */
	public int damageModifier() {
		var res = 0;
		res += getAcc(EffectType.RAGE);
		res -= getAcc(EffectType.WEAK);
		return res;
	}

	/**
	 * Calculates the shield modifier from effects.
	 * 
	 * @return the shield modifier (positive or negative)
	 */
	public int shieldModifier() {
		var res = 0;
		res += getAcc(EffectType.HASTE);
		res -= getAcc(EffectType.SLOW);
		return res;
	}

	/**
	 * Returns the freeze damage bonus.
	 * 
	 * @return the freeze damage
	 */
	public int freezeDamage() {
		return getAcc(EffectType.FREEZE);
	}

	/**
	 * Attempts to dodge an attack. Consumes one dodge stack if available.
	 * 
	 * @return true if the attack was dodged
	 */
	public boolean tryToDodge() {
		if (containsEffect(EffectType.DODGE)) {
			decreaseEffect(EffectType.DODGE);
			return true;
		}
		return false;
	}

	/**
	 * Returns the spike damage value.
	 * 
	 * @return the spike damage
	 */
	public int spikeDamage() {
		return getAcc(EffectType.SPIKES);
	}

	/**
	 * Checks if the fighter is sleeping.
	 * 
	 * @return true if sleeping
	 */
	public boolean isSleeping() {
		return containsEffect(EffectType.SLEEP);
	}

	/**
	 * Checks if the fighter is charmed based on their HP.
	 * 
	 * @param hp the fighter's current HP
	 * @return true if charmed
	 */
	public boolean isCharmed(int hp) {
		return (getAcc(EffectType.CHARM) > hp);
	}

	/**
	 * Checks if the fighter has rough hide.
	 * 
	 * @return true if has rough hide
	 */
	public boolean hasRoughHide() {
		return containsEffect(EffectType.ROUGH_HIDE);
	}

	/**
	 * Checks if the fighter is a zombie.
	 * 
	 * @return true if zombie
	 */
	public boolean isZombie() {
		return containsEffect(EffectType.ZOMBIE);
	}

	/**
	 * Checks if the fighter should skip their turn due to poison.
	 * 
	 * @param hp the fighter's current HP
	 * @return true if should skip turn
	 */
	public boolean skipTurnBcOfPoison(int hp) {
		return (getAcc(EffectType.POISON) >= hp);
	}
}
