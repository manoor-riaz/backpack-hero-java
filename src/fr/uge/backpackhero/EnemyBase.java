package fr.uge.backpackhero;

import module java.base;

/**
 * Represents an enemy that the hero fights in combat. Enemies have health,
 * shield, and can perform random actions including casting curses and applying
 * status effects.
 */
public final class EnemyBase implements Fighter {
	private final String name;
	private final int maxHp;
	private int hp;
	private int shieldpoint;
	private final int xp;
	private final ArrayList<ActionEnnemi> intentions;
	private final int attaqueMin;
	private final int attaqueMax;
	private final int defenseMin;
	private final int defenseMax;
	private final ArrayList<Curses> curses;
	private final int curseProba;
	private final EffectManager effects;
	private final ArrayList<EffectType> ownedEffects;
	private final int effectProba;

	/**
	 * Creates a new enemy with full capabilities.
	 * 
	 * @param name         the name of the enemy
	 * @param maxHp        the maximum health points
	 * @param xp           the experience points given when defeated
	 * @param attaqueMin   the minimum attack damage
	 * @param attaqueMax   the maximum attack damage
	 * @param defenseMin   the minimum defense shield
	 * @param defenseMax   the maximum defense shield
	 * @param curses       list of curses this enemy can cast
	 * @param curseProba   percentage chance to cast a curse (0-100)
	 * @param ownedEffects list of status effects this enemy can apply
	 * @param effectProba  percentage chance to apply a status effect (0-100)
	 * @throws NullPointerException     if any reference parameter is null
	 * @throws IllegalArgumentException if maxHp is not positive or probabilities
	 *                                  invalid
	 */
	public EnemyBase(String name, int maxHp, int xp, int attaqueMin, int attaqueMax, int defenseMin, int defenseMax,
			List<Curses> curses, int curseProba, List<EffectType> ownedEffects, int effectProba) {
		this.name = Objects.requireNonNull(name);
		Objects.requireNonNull(curses);
		Objects.requireNonNull(ownedEffects);

		if (maxHp <= 0) {
			throw new IllegalArgumentException("Hp cannot be <= 0");
		}
		if (curseProba < 0 || curseProba > 100) {
			throw new IllegalArgumentException("curseProba have to be between 0 and 100");
		}
		if (effectProba < 0 || effectProba > 100) {
			throw new IllegalArgumentException("effectProba have to be between 0 and 100");
		}

		this.maxHp = maxHp;
		this.hp = maxHp;
		this.shieldpoint = 0;
		this.xp = xp;
		this.attaqueMin = attaqueMin;
		this.attaqueMax = attaqueMax;
		this.defenseMin = defenseMin;
		this.defenseMax = defenseMax;
		this.curses = new ArrayList<>(curses);
		this.curseProba = curseProba;
		this.intentions = new ArrayList<>();
		this.ownedEffects = new ArrayList<>(ownedEffects);
		this.effectProba = effectProba;
		this.effects = new EffectManager();
	}

	/**
	 * Randomly chooses the next actions for this enemy.
	 * 
	 * @param random the random number generator
	 * @throws NullPointerException if random is null
	 */
	public void nextAction(Random random) {
		Objects.requireNonNull(random);

		intentions.clear();
		if (ifSkipTurn()) {
			return;
		}

		curseAction(random);
		effectAction(random);
		combatAction(random);
	}

	/**
	 * Checks if the enemy should skip their turn.
	 * 
	 * @return true if the enemy is sleeping or poisoned critically
	 */
	private boolean ifSkipTurn() {
		return effects.isSleeping() || effects.skipTurnBcOfPoison(hp);
	}

	/**
	 * Determines if the enemy will cast a curse this turn.
	 * 
	 * @param random the random number generator
	 */
	private void curseAction(Random random) {
		if (!curses.isEmpty() && random.nextInt(100) < curseProba) {
			var curseChoosen = curses.get(random.nextInt(curses.size()));
			intentions.add(new ActionEnnemi(curseChoosen));
		}
	}

	/**
	 * Determines if the enemy will apply a status effect this turn.
	 * 
	 * @param random the random number generator
	 */
	private void effectAction(Random random) {
		if (!ownedEffects.isEmpty() && random.nextInt(100) < effectProba) {
			var effectChosen = ownedEffects.get(random.nextInt(ownedEffects.size()));
			var acc = 1 + random.nextInt(3);
			intentions.add(new ActionEnnemi(effectChosen, acc));
		}
	}

	/**
	 * Determines the enemy's combat action (attack or defense).
	 * 
	 * @param random the random number generator
	 */
	private void combatAction(Random random) {
		if (random.nextBoolean()) {
			var damage = attaqueMin + random.nextInt(attaqueMax - attaqueMin + 1);
			damage = Math.max(0, damage + effects.damageModifier());
			intentions.add(new ActionEnnemi(Action.ATTACK, damage));
		} else {
			var shield = defenseMin + random.nextInt(defenseMax - defenseMin + 1);
			shield = Math.max(0, shield + effects.shieldModifier());
			intentions.add(new ActionEnnemi(Action.DEFENSE, shield));
		}
	}

	/**
	 * Applies damage to this enemy. Damage is first absorbed by shield, then
	 * health.
	 * 
	 * @param damage     the amount of damage to take
	 * @param projectile whether the damage is from a projectile
	 * @throws IllegalArgumentException if damage is negative
	 */
	@Override
	public void takeDamage(int damage, boolean projectile) {
		if (damage < 0) {
			throw new IllegalArgumentException("Les dégâts ne peuvent pas être négatifs");
		}

		if (!isAlive()) {
			return;
		}

		if (effects.tryToDodge()) {
			return;
		}

		var absorbedDamage = calculateAbsorbedDamage(damage, projectile);
		applyDamageToShieldAndHp(absorbedDamage);
	}

	/**
	 * Calculates the final damage after modifiers.
	 * 
	 * @param damage     the base damage
	 * @param projectile whether the damage is from a projectile
	 * @return the modified damage
	 */
	private int calculateAbsorbedDamage(int damage, boolean projectile) {
		var absorbedDamage = damage;
		if (projectile && effects.hasRoughHide()) {
			absorbedDamage = absorbedDamage / 2;
		}
		absorbedDamage += effects.freezeDamage();
		return absorbedDamage;
	}

	/**
	 * Applies damage to shield first, then HP.
	 * 
	 * @param damage the damage to apply
	 */
	private void applyDamageToShieldAndHp(int damage) {
		if (shieldpoint >= damage) {
			shieldpoint -= damage;
			return;
		}

		var realDamage = damage - shieldpoint;
		shieldpoint = 0;
		hp = Math.max(0, hp - realDamage);
	}

	/**
	 * Applies poison damage that bypasses shield.
	 * 
	 * @param damage the poison damage
	 * @throws IllegalArgumentException if damage is negative
	 */
	@Override
	public void takePoisonDamage(int damage) {
		if (damage < 0) {
			throw new IllegalArgumentException("Damage cannot be < 0");
		}
		if (!isAlive()) {
			return;
		}
		hp = Math.max(0, hp - damage);
	}

	/**
	 * Adds shield points to this enemy.
	 * 
	 * @param shield the amount of shield to add
	 * @throws IllegalArgumentException if shield is negative
	 */
	@Override
	public void takeShield(int shield) {
		if (shield < 0) {
			throw new IllegalArgumentException("Shield point cannot be negative");
		}
		var realShield = Math.max(0, shield + effects.shieldModifier());
		shieldpoint += realShield;
	}

	/**
	 * Heals this enemy. If the enemy is a zombie, healing deals damage instead.
	 * 
	 * @param heal the amount to heal
	 * @throws IllegalArgumentException if heal is negative
	 */
	@Override
	public void healing(int heal) {
		if (heal < 0) {
			throw new IllegalArgumentException("Heal point cannot be negative");
		}

		if (effects.isZombie()) {
			takePoisonDamage(heal);
			return;
		}

		hp += heal;
		if (hp > maxHp) {
			hp = maxHp;
		}
	}

	/**
	 * Resets the enemy's shield to zero.
	 */
	public void resetShield() {
		shieldpoint = 0;
	}

	/**
	 * Returns the enemy's planned actions for this turn.
	 * 
	 * @return the planned actions
	 */
	public List<ActionEnnemi> getIntentions() {
		return List.copyOf(intentions);
	}

	/**
	 * Returns the name of this enemy.
	 * 
	 * @return the name
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Returns the current health points.
	 * 
	 * @return the current hp
	 */
	@Override
	public int hp() {
		return hp;
	}

	/**
	 * Returns the maximum health points.
	 * 
	 * @return the maximum hp
	 */
	@Override
	public int maxhp() {
		return maxHp;
	}

	/**
	 * Returns the current shield points.
	 * 
	 * @return the current shield
	 */
	@Override
	public int shieldpoint() {
		return shieldpoint;
	}

	/**
	 * Returns the experience points given when defeated.
	 * 
	 * @return the xp
	 */
	public int xp() {
		return xp;
	}

	/**
	 * Returns the effect manager for this enemy.
	 * 
	 * @return the effect manager
	 */
	@Override
	public EffectManager effects() {
		return effects;
	}

	/**
	 * Applies a status effect to this enemy.
	 * 
	 * @param type the effect type
	 * @param acc  the number of stacks
	 */
	public void applyEffect(EffectType type, int acc) {
		effects.applyEffect(type, acc);
	}

	/**
	 * Removes all effects from this enemy.
	 */
	public void removeEffects() {
		effects.removeAllEffects();
	}

	/**
	 * Checks if this enemy is charmed.
	 * 
	 * @return true if charmed
	 */
	public boolean isCharmed() {
		return effects.isCharmed(hp);
	}

}