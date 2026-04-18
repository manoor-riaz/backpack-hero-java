package fr.uge.backpackhero;

import java.util.Objects;

/**
 * Utility class to calculate costs of items in combat. This centralizes cost
 * calculation logic that was previously scattered in UI code.
 */
public class ItemCostCalcul {

	/**
	 * Private constructor to prevent instantiation of utility class.
	 */
	private ItemCostCalcul() {

	}

	/**
	 * Gets the energy point (EP) cost of an item.
	 *
	 * @param item the item to check
	 * @return the EP cost, or 0 if the item has no EP cost
	 */
	public static int getEpCost(Item item) {
		Objects.requireNonNull(item);

		return switch (item) {
		case Weapon w -> w.cost();
		case Armor a -> a.cost();
		case Consumables c -> c.cost();
		case Accessories a -> a.cost();
		default -> 0;
		};
	}

	/**
	 * Gets the mana cost of an item.
	 *
	 * @param item the item to check
	 * @return the mana cost, or 0 if the item has no mana cost
	 */
	public static int getManaCost(Item item) {
		Objects.requireNonNull(item);

		return switch (item) {
		case Weapon w -> w.manaCost();
		case Armor a -> a.manaCost();
		case Consumables c -> c.manaCost();
		case Accessories a -> a.manaCost();
		case MagicItem m -> m.manaCost();
		default -> 0;
		};
	}

	/**
	 * Checks if the hero can afford to use an item.
	 *
	 * @param item the item to use
	 * @param hero the hero using the item
	 * @return true if the hero has enough EP and mana
	 */
	public static boolean canAfford(Item item, Hero hero) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(hero);

		var epCost = getEpCost(item);
		var manaCost = getManaCost(item);

		if (hero.ep() < epCost) {
			return false;
		}

		if (manaCost > 0 && hero.backpack().getTotalMana() < manaCost) {
			return false;
		}

		return true;
	}

	/**
	 * Consumes the costs of using an item from the hero's resources.
	 *
	 * @param item the item being used
	 * @param hero the hero using the item
	 */
	public static void consumeCosts(Item item, Hero hero) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(hero);

		var epCost = getEpCost(item);
		var manaCost = getManaCost(item);

		hero.useEp(epCost);

		if (manaCost > 0) {
			hero.backpack().consumeMana(manaCost);
			hero.refreshManaFromBackpack();
		}
	}
}