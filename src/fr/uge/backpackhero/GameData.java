package fr.uge.backpackhero;

import module java.base;

/**
 * Manages the game state and main game logic. Handles exploration, combat,
 * loot, and merchant interactions.
 */
public class GameData {

	private final Hero hero;
	private final Dungeon dungeon;
	private Room currentRoom;

	/**
	 * Creates a new game with the specified hero name.
	 *
	 * @param name the hero's name
	 * @throws NullPointerException if name is null
	 */
	public GameData(String name) {
		Objects.requireNonNull(name);
		this.hero = new Hero(name);
		this.dungeon = new Dungeon(3);
		this.dungeon.initDungeon(new Random());
		this.currentRoom = null;
	}

	/**
	 * Enters a room and saves it for later conversion.
	 * 
	 * @param room the room being entered
	 * @throws NullPointerException if room is null
	 */
	public void enterRoom(Room room) {
		Objects.requireNonNull(room);
		this.currentRoom = room;
	}

	/**
	 * Leaves the current room and returns to exploration. Automatically converts
	 * the room to a corridor (except EXIT rooms).
	 */
	public void leaveARoom() {
		if (currentRoom != null) {
			currentRoom.convertToCorridor();
			currentRoom = null;
		}
	}

	/**
	 * Gets the current room the hero is in.
	 * 
	 * @return the current room, or null if not in a room
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * Buys healing from a healer.
	 *
	 * @param price   the cost of healing
	 * @param healing the amount of hp to heal
	 * @return true if the purchase was successful
	 * @throws IllegalArgumentException if price or healing is negative
	 */
	public boolean buyHeal(int price, int healing) {
		if (price < 0 || healing < 0) {
			throw new IllegalArgumentException("Price and healing must be non-negative");
		}

		Gold gold = hero.backpack().getGold();
		if (gold == null || gold.purse() < price) {
			return false;
		}

		gold.spend(price);
		if (gold.purse() == 0) {
			hero.backpack().remove(gold);
		}
		hero.healing(healing);
		return true;
	}

	/**
	 * Buys additional maximum hp.
	 *
	 * @param price the cost
	 * @param nbHp  the amount of max hp to add
	 * @return true if the purchase was successful
	 * @throws IllegalArgumentException if price or nbHp is negative
	 */
	public boolean addMaxHp(int price, int nbHp) {
		if (price < 0 || nbHp < 0) {
			throw new IllegalArgumentException("Price and nbHp must be non-negative");
		}

		Gold gold = hero.backpack().getGold();
		if (gold == null || gold.purse() < price) {
			return false;
		}

		gold.spend(price);
		if (gold.purse() == 0) {
			hero.backpack().remove(gold);
		}
		hero.addMaxHp(nbHp);
		return true;
	}

	/**
	 * Generates a list of items for the merchant's shop inventory. Excludes gold
	 * and duplicates.
	 * 
	 * @param count number of items to generate
	 * @return list of items for the shop
	 */
	public List<Item> generateMerchantInventory(int count) {
		var inventory = new ArrayList<Item>();

		for (int i = 0; i < count; i++) {
			Item item = GenerateItems.generateADefaultItem();

			if (item.isGold() || inventory.stream().anyMatch(it -> it.name().equals(item.name()))) {
				i--;
				continue;
			}

			inventory.add(item);
		}

		return inventory;
	}

	/**
	 * Handles buying an item from the merchant.
	 * 
	 * @param item the item to buy
	 * @return true if purchase was successful
	 */
	public boolean buyFromMerchant(Item item) {
		Objects.requireNonNull(item);

		var price = item.rarity().purchasePrice();
		var gold = hero.backpack().getGold();

		if (gold == null || gold.purse() < price) {
			return false;
		}

		gold.spend(price);

		if (gold.purse() <= 0) {
			hero.backpack().remove(gold);
		}

		hero.refreshManaFromBackpack();
		return true;
	}

	/**
	 * Handles selling an item to the merchant.
	 * 
	 * @param item the item to sell
	 */
	public void sellToMerchant(Item item) {
		Objects.requireNonNull(item);

		var sellPrice = item.rarity().sellingPrice();
		hero.backpack().remove(item);

		var gold = hero.backpack().getGold();
		if (gold == null) {
			gold = new Gold(0);
			hero.backpack().freeAdd(gold);
		}
		gold.add(sellPrice);
	}

	/**
	 * Removes one curse from the hero's backpack and deducts the price.
	 * 
	 * @param price the cost to remove a curse
	 * @return true if a curse was successfully removed, false otherwise
	 * @throws IllegalArgumentException if price is negative
	 */
	public boolean removeCurse(int price) {
		if (price < 0) {
			throw new IllegalArgumentException("Price must be non-negative");
		}

		int curseCount = 0;
		for (int row = 0; row < hero.backpack().height(); row++) {
			for (int col = 0; col < hero.backpack().width(); col++) {
				var item = hero.backpack().itemAt(row, col);
				if (item != null && item.isCurse()) {
					curseCount++;
				}
			}
		}

		if (curseCount == 0) {
			return false;
		}

		Gold gold = hero.backpack().getGold();
		if (gold == null || gold.purse() < price) {
			return false;
		}

		Item curseToRemove = null;
		for (int row = 0; row < hero.backpack().height() && curseToRemove == null; row++) {
			for (int col = 0; col < hero.backpack().width() && curseToRemove == null; col++) {
				var item = hero.backpack().itemAt(row, col);
				if (item != null && item.isCurse()) {
					curseToRemove = item;
				}
			}
		}

		if (curseToRemove != null) {
			gold.spend(price);
			if (gold.purse() == 0) {
				hero.backpack().remove(gold);
			}
			hero.backpack().remove(curseToRemove);
			return true;
		}

		return false;
	}

	/**
	 * Counts the number of curses in the hero's backpack.
	 * 
	 * @return the number of curses
	 */
	public int countCurses() {
		int count = 0;
		for (int row = 0; row < hero.backpack().height(); row++) {
			for (int col = 0; col < hero.backpack().width(); col++) {
				var item = hero.backpack().itemAt(row, col);
				if (item != null && item.isCurse()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Generates treasure loot with random items. All gold items are consolidated
	 * into a single Gold item.
	 * 
	 * @return list of items (non-gold items + one consolidated Gold if any)
	 */
	public List<Item> generateTreasureLoot() {
		int minItems = 4;
		int maxExtraItems = 4;
		int nbItems = minItems + new Random().nextInt(maxExtraItems);

		var loot = new ArrayList<Item>();
		int totalGold = 0;

		for (int i = 0; i < nbItems; i++) {
			var item = GenerateItems.generateADefaultItem();

			switch (item) {
			case Gold goldItem -> totalGold += goldItem.purse();
			default -> loot.add(item);
			}
		}

		if (totalGold > 0) {
			loot.add(new Gold(totalGold));
		}

		return loot;
	}

	/**
	 * Adds gold to the hero's backpack. If gold already exists, merges it.
	 * Otherwise creates a new Gold item.
	 * 
	 * @param amount the amount of gold to add
	 * @throws IllegalArgumentException if amount is negative
	 */
	public void addGoldToBackpack(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Amount must be non-negative");
		}

		if (amount == 0) {
			return;
		}

		Gold existingGold = hero.backpack().getGold();
		if (existingGold == null) {
			hero.backpack().freeAdd(new Gold(amount));
		} else {
			existingGold.add(amount);
		}
	}

	/**
	 * Applies combat victory rewards. Awards XP to hero and returns loot items.
	 * 
	 * @param combat the completed combat
	 * @return the combat reward (XP and loot items)
	 * @throws IllegalStateException if combat is not won
	 */
	public CombatReward applyCombatVictory(Combat combat) {
		Objects.requireNonNull(combat);

		CombatReward reward = combat.reward();

		combat.heroGainsXp();

		return reward;
	}

	/**
	 * Starts a combat with the enemies in the given room.
	 * 
	 * @param room the room containing enemies
	 * @return the created combat instance
	 * @throws NullPointerException     if room is null
	 * @throws IllegalArgumentException if room has no enemies
	 */
	public Combat startCombat(Room room) {
		Objects.requireNonNull(room);

		List<EnemyBase> enemies = room.getEnemies();
		if (enemies == null || enemies.isEmpty()) {
			throw new IllegalArgumentException("Room has no enemies to fight");
		}

		return new Combat(hero, enemies);
	}

	/**
	 * Progresses to the next floor in the dungeon.
	 * 
	 * @return the new floor if progression succeeded, null if dungeon is complete
	 */
	public Floor progressToNextFloor() {
		boolean success = dungeon.nextFloor();

		if (success) {
			return dungeon.currentFloor();
		}

		return null;
	}

	/**
	 * Returns the hero.
	 *
	 * @return the hero
	 */
	public Hero hero() {
		return hero;
	}

	/**
	 * Returns the dungeon.
	 *
	 * @return the dungeon
	 */
	public Dungeon dungeon() {
		return dungeon;
	}
}