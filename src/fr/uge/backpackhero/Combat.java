package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a combat between the hero and enemies. Manages turn order, combat
 * state, and rewards.
 */
public class Combat {

	private final Hero hero;
	private final ArrayList<EnemyBase> enemies;
	private final ArrayList<EnemyBase> charmedEnemies;
	private CombatState state;
	private final ArrayList<Curses> curses;
	private int accXp;

	/**
	 * Creates a new combat. Initializes enemy actions and applies start of turn
	 * effects.
	 * 
	 * @param hero    the hero fighting
	 * @param enemies the list of enemies to fight
	 * @throws NullPointerException     if any parameter is null
	 * @throws IllegalArgumentException if enemies list is empty
	 */
	public Combat(Hero hero, List<EnemyBase> enemies) {
		this.hero = Objects.requireNonNull(hero);
		Objects.requireNonNull(enemies);
		if (enemies.isEmpty()) {
			throw new IllegalArgumentException("You can't fight without enemies");
		}

		this.enemies = new ArrayList<>(enemies);
		this.charmedEnemies = new ArrayList<>();
		this.state = CombatState.PLAYER_TURN;
		this.curses = new ArrayList<>();

		initializeCombat();
	}

	/**
	 * Initializes the combat by setting up enemy actions and hero state.
	 */
	private void initializeCombat() {
		for (var enemy : this.enemies) {
			enemy.nextAction(new Random());
		}

		hero.statsNewTurn();
		ItemInteractions.passiveEffects(hero);
		applyStartOfTurnEffects(this.hero);
		ifDefeat();
	}

	/**
	 * Checks if all enemies are defeated and updates state to victory.
	 */
	private void ifVictory() {
		if (enemies.isEmpty()) {
			state = CombatState.VICTORY;
			removeCombatEffect();
			hero.statsNewTurn();
		}
	}

	/**
	 * Removes all combat effects from the hero and charmed enemies.
	 */
	private void removeCombatEffect() {
		hero.removeEffects();
		for (var enemy : charmedEnemies) {
			enemy.removeEffects();
		}
	}

	/**
	 * Ends the player's turn and applies end of turn effects.
	 */
	public void endPlayerTurn() {
		if (state != CombatState.PLAYER_TURN) {
			return;
		}

		applyEndOfTurnEffects(hero);

		ifDefeat();
		state = CombatState.ENEMY_TURN;
	}

	/**
	 * Executes the enemies turn. Each enemy performs all their actions.
	 * 
	 * @return list of curses cast this turn
	 */
	public List<Curses> enemiesTurn() {
		curses.clear();
		removeDeadEnemies();
		checkIfCharmedEnemies();

		if (state == CombatState.VICTORY) {
			return curses;
		}

		executeAllEnemyTurns();

		removeDeadEnemies();
		if (state == CombatState.VICTORY) {
			return curses;
		}

		nextTurn();
		return curses;
	}

	/**
	 * Executes turns for all enemies in combat.
	 */
	private void executeAllEnemyTurns() {
		for (var enemy : new ArrayList<>(enemies)) {
			if (!enemies.contains(enemy)) {
				continue;
			}

			if (enemy.isAlive()) {
				executeSingleEnemyTurn(enemy);
			}
		}
	}

	/**
	 * Executes a single enemy's turn including effects and actions.
	 * 
	 * @param enemy the enemy to execute turn for
	 */
	private void executeSingleEnemyTurn(EnemyBase enemy) {
		applyStartOfTurnEffects(enemy);

		if (!enemy.isAlive()) {
			return;
		}

		checkIfCharmedEnemies();

		enemyActions(enemy);
		applyEndOfTurnEffects(enemy);

		if (!enemy.isAlive()) {
			return;
		}

		checkIfCharmedEnemies();
	}

	/**
	 * Applies start of turn effects to a fighter.
	 * 
	 * @param fighter the fighter to apply effects to
	 * @throws NullPointerException if fighter is null
	 */
	private void applyStartOfTurnEffects(Fighter fighter) {
		Objects.requireNonNull(fighter);
		fighter.effects().startOfTurnEffects(fighter);
	}

	/**
	 * Applies end of turn effects to a fighter.
	 * 
	 * @param fighter the fighter to apply effects to
	 * @throws NullPointerException if fighter is null
	 */
	private void applyEndOfTurnEffects(Fighter fighter) {
		Objects.requireNonNull(fighter);
		fighter.effects().endOfTurnEffects(fighter);
	}

	/**
	 * Removes all dead enemies from combat and awards XP.
	 */
	private void removeDeadEnemies() {
		var deadEnemies = enemies.stream().filter(e -> !e.isAlive()).toList();

		for (var enemy : deadEnemies) {
			accXp += enemy.xp();
			enemies.remove(enemy);
		}

		ifVictory();
	}

	/**
	 * Executes all actions for an enemy.
	 * 
	 * @param enemy the enemy performing actions
	 */
	private void enemyActions(EnemyBase enemy) {
		var actions = enemy.getIntentions();

		if (actions.isEmpty()) {
			enemy.nextAction(new Random());
			enemy.resetShield();
			return;
		}

		for (var action : actions) {
			doAction(enemy, action);
		}

		enemy.nextAction(new Random());
		enemy.resetShield();
	}

	/**
	 * Performs a single enemy action.
	 * 
	 * @param enemy  the enemy performing the action
	 * @param action the action to perform
	 */
	private void doAction(EnemyBase enemy, ActionEnnemi action) {
		switch (action.type()) {
		case ATTACK -> doAttack(enemy, action.valeur());
		case DEFENSE -> enemy.takeShield(action.valeur());
		case CURSE -> curses.add(action.curse());
		case EFFECT -> doEffect(enemy, action);
		}
	}

	/**
	 * Performs an attack action, dealing damage and handling spike damage.
	 * 
	 * @param enemy  the attacking enemy
	 * @param damage the damage amount
	 */
	private void doAttack(EnemyBase enemy, int damage) {
		hero.takeDamage(damage, false);
		if (hero.spikeDamage() > 0) {
			enemy.takeDamage(hero.spikeDamage(), false);
		}
	}

	/**
	 * Applies a status effect action.
	 * 
	 * @param enemy  the enemy applying the effect
	 * @param action the effect action
	 */
	private void doEffect(EnemyBase enemy, ActionEnnemi action) {
		var effect = action.effect();
		var acc = action.effectAcc();
		if (effect.isPositive()) {
			enemy.applyEffect(effect, acc);
		} else {
			hero.applyEffect(effect, acc);
		}
	}

	/**
	 * Advances to the next turn and resets hero stats.
	 */
	private void nextTurn() {
		hero.statsNewTurn();

		applyStartOfTurnEffects(hero);

		ifDefeat();
		state = CombatState.PLAYER_TURN;
		ifDefeat();
	}

	/**
	 * Checks if the hero is defeated and updates combat state.
	 */
	private void ifDefeat() {
		if (!hero.isAlive()) {
			state = CombatState.DEFEAT;
		}
	}

	/**
	 * Checks for charmed enemies and removes them from combat.
	 */
	private void checkIfCharmedEnemies() {
		var ennemiesToCharm = new ArrayList<EnemyBase>();
		for (var enemy : enemies) {
			if (enemy.isAlive() && enemy.isCharmed()) {
				ennemiesToCharm.add(enemy);
			}
		}

		charmEnemies(ennemiesToCharm);

		ifVictory();
	}

	/**
	 * Charms a list of enemies, removing them from combat.
	 * 
	 * @param ennemiesToCharm the enemies to charm
	 */
	private void charmEnemies(List<EnemyBase> ennemiesToCharm) {
		for (var enemy : ennemiesToCharm) {
			accXp += enemy.xp();
			enemy.takePoisonDamage(enemy.hp());
			enemies.remove(enemy);
			charmedEnemies.add(enemy);
		}
	}

	/**
	 * Grants accumulated XP to the hero and resets the counter.
	 */
	public void heroGainsXp() {
		if (accXp > 0) {
			hero.gainXp(accXp);
			accXp = 0;
		}
	}

	/**
	 * Returns the reward for winning the combat.
	 * 
	 * @return the combat reward with XP and loot
	 * @throws IllegalStateException if the combat is not won yet
	 */
	public CombatReward reward() {
		if (state != CombatState.VICTORY) {
			throw new IllegalStateException("You haven't won the fight yet.");
		}

		var xp = accXp;

		var itemInLoot = 3 + new Random().nextInt(3);
		List<Item> loot = GenerateItems.generateCombatReward(itemInLoot);

		return new CombatReward(xp, loot);
	}

	/**
	 * Checks if the combat is over.
	 * 
	 * @return true if the combat ended (victory or defeat)
	 */
	public boolean itsEnd() {
		return (state == CombatState.DEFEAT || state == CombatState.VICTORY);
	}

	/**
	 * Returns the hero in this combat.
	 * 
	 * @return the hero
	 */
	public Hero hero() {
		return hero;
	}

	/**
	 * Returns the list of enemies in this combat.
	 * 
	 * @return the enemies list
	 */
	public List<EnemyBase> enemies() {
		return List.copyOf(enemies);
	}

	/**
	 * Removes an enemy from combat. If the enemy is dead, awards XP.
	 * 
	 * @param enemi the enemy to remove
	 * @throws NullPointerException if enemi is null
	 */
	public void remove(EnemyBase enemi) {
		Objects.requireNonNull(enemi);

		if (!enemi.isAlive() && enemies.contains(enemi)) {
			accXp += enemi.xp();
		}

		enemies.remove(enemi);
		ifVictory();
	}

	/**
	 * Cleans up all dead enemies from combat, awarding XP for each. This is useful
	 * after effects like poison or burn may have killed enemies.
	 */
	public void cleanupDeadEnemies() {
		removeDeadEnemies();
	}

	/**
	 * Gets the first alive enemy, or null if no enemies are alive.
	 * 
	 * @return the first alive enemy, or null
	 */
	public EnemyBase getFirstAliveEnemy() {
		return enemies.stream().filter(EnemyBase::isAlive).findFirst().orElse(null);
	}

	/**
	 * Tries to use an item in combat. Checks costs, consumes resources, and applies
	 * effects.
	 * 
	 * @param item   the item to use
	 * @param target the target enemy (can be null for self-targeted items)
	 * @return true if the item was successfully used, false otherwise
	 * @throws NullPointerException if item is null
	 */
	public boolean tryUseItem(Item item, EnemyBase target) {
		Objects.requireNonNull(item);

		if (item.isCurse()) {
			return false;
		}

		if (!ItemCostCalcul.canAfford(item, hero)) {
			return false;
		}

		ItemCostCalcul.consumeCosts(item, hero);

		useItemOnTarget(item, target);

		if (item.isConsumable()) {
			hero.backpack().remove(item);
		}

		return true;
	}

	/**
	 * Uses an item on a target, applying its effects.
	 * 
	 * @param item   the item to use
	 * @param target the target (can be null for self-targeted items)
	 */
	private void useItemOnTarget(Item item, EnemyBase target) {
		switch (item) {
		case Weapon w -> {
			if (target != null) {
				w.use(hero, target);
			}
		}
		case MagicItem m -> {
			if (target != null) {
				m.use(hero, target);
			}
		}
		case Armor a -> a.use(hero, null);
		case Consumables c -> c.use(hero, null);
		default -> {
		}
		}
	}
}