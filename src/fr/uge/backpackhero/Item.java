package fr.uge.backpackhero;

/**
 * Represents any item that can be placed in the backpack. Items have a name,
 * rarity, dimension, and can be used or rotated.
 */
public sealed interface Item permits Weapon, Armor, MagicItem, Accessories, Consumables, Gold, Curses, Key, ManaStone {

	/**
	 * Returns the name of the item.
	 * 
	 * @return : the item name
	 */
	String name();

	/**
	 * Returns the rarity of the item.
	 * 
	 * @return : the rarity
	 */
	Rarity rarity();

	/**
	 * Returns the dimension of the item.
	 * 
	 * @return : the dimension
	 */
	default Dimension dimension() {
		var cells = shape().turnItem(Orientation.ORIENTATION_0);
		return shape().dimensionOfItem(cells);
	}

	/**
	 * Uses the item in combat or on the hero.
	 * 
	 * @param hero  : the hero using the item
	 * @param enemy : the target enemy (can be null for some items)
	 */
	void use(Hero hero, EnemyBase enemy);

	/**
	 * Returns the shape of this item
	 * 
	 * @return the shape
	 */
	Shape shape();

	/**
	 * Checks if this item can be rotated.
	 * 
	 * @return : true by default
	 */
	default boolean canRotate() {
		return true;
	}

	/**
	 * Checks if this item is gold.
	 * 
	 * @return : false by default
	 */
	default boolean isGold() {
		return false;
	}

	/**
	 * Checks if this item is consumable.
	 * 
	 * @return : false by default
	 */
	default boolean isConsumable() {
		return false;
	}

	/**
	 * Checks if this item is a key.
	 * 
	 * @return : false by default
	 */
	default boolean isKey() {
		return false;
	}

	/**
	 * Checks if this item is a mana stone.
	 * 
	 * @return : false by default
	 */
	default boolean isManaStone() {
		return false;
	}

	/**
	 * Checks if this item is a curse.
	 * 
	 * @return : false by default
	 */
	default boolean isCurse() {
		return false;
	}

	/**
	 * Returns the purchase price of the item.
	 * 
	 * @return : the price based on rarity
	 */
	default int price() {
		return rarity().purchasePrice();
	}

	/**
	 * Returns the selling price of the item.
	 * 
	 * @return : the selling price based on rarity
	 */
	default int sellPrice() {
		return rarity().sellingPrice();
	}

	/**
	 * Returns the stats of the item.
	 * 
	 * @return : the stats, or (0, 0, 0) if not applicable
	 */
	default Stats stats() {
		return new Stats(0, 0, 0);
	}

	/**
	 * Checks if this item can be moved/dragged by the player. Curses cannot be
	 * moved once placed.
	 * 
	 * @return true if the item can be moved, false otherwise
	 */
	default boolean isMovable() {
		return !isCurse();
	}

	/**
	 * Checks if this item is an armor
	 * 
	 * @return true if armor, false otherwise
	 */
	default boolean isArmor() {
		return false;
	}

	/**
	 * Checks if this item is a weapon
	 * 
	 * @return true if weapon, false otherwise
	 */
	default boolean isWeapon() {
		return false;
	}

	/**
	 * Returns the damage bonus provided by this accessory
	 * 
	 * @return the damage bonus
	 */
	default int accessoryDamageBonus() {
		return 0;
	}

	/**
	 * Returns the shield bonus provided by this accessory
	 * 
	 * @return the shield bonus
	 */
	default int accessoryShieldBonus() {
		return 0;
	}

	/**
	 * Returns the gold value of this item
	 * 
	 * @return the gold value
	 */
	default int purse() {
		return 0;
	}

}