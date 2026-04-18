package fr.uge.backpackhero;

import module java.base;

/**
 * Utility class that handles interactions between items in the hero's backpack.
 * Manages weapon bonuses, armor bonuses, and passive effects based on
 * neighboring items. This class cannot be instantiated.
 */
public class ItemInteractions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ItemInteractions() {

	}

	/**
	 * Calculates bonus damage and applies effects when a weapon is used. Checks all
	 * neighboring items and applies their effects to the hero and enemy.
	 * 
	 * @param hero   the hero using the weapon
	 * @param weapon the weapon being used
	 * @param enemy  the enemy being attacked (can be null)
	 * @return the total bonus damage from neighboring items
	 * @throws NullPointerException if hero or weapon is null
	 */
	public static int weaponUse(Hero hero, Weapon weapon, EnemyBase enemy) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(weapon);

		var backpack = hero.backpack();
		var res = 0;

		var neighbourItems = backpack.neighbourItems(weapon);
		for (var neighbor : neighbourItems) {
			if (enemy != null) {
				enemyEffects(neighbor, enemy);
			}

			heroEffects(neighbor, hero);
			res += bonusDamage(neighbor);
		}

		return res;
	}

	/**
	 * Applies effects from a neighboring item to an enemy. Effects include poison,
	 * burn, freeze, weakness, sleep, and charm.
	 * 
	 * @param neighbor the neighboring item
	 * @param enemy    the enemy to apply effects to
	 */
	private static void enemyEffects(Item neighbor, EnemyBase enemy) {
		if (isPoisonWhetstone(neighbor) && enemy != null) {
			enemy.applyEffect(EffectType.POISON, 1);
		}
		if (isFireStone(neighbor) && enemy != null) {
			enemy.applyEffect(EffectType.BURN, 1);
		}
		if (isFrostStone(neighbor)) {
			enemy.applyEffect(EffectType.FREEZE, 2);
		}
		if (isWeaknessAmulet(neighbor)) {
			enemy.applyEffect(EffectType.WEAK, 2);
		}
		if (isDreamcatcher(neighbor)) {
			enemy.applyEffect(EffectType.SLEEP, 1);
		}
		if (isCharmedBracelet(neighbor)) {
			enemy.applyEffect(EffectType.CHARM, 15);
		}
	}

	/**
	 * Applies effects from a neighboring item to the hero. Effects include healing,
	 * rage, and dodge.
	 * 
	 * @param neighbor the neighboring item
	 * @param hero     the hero to apply effects to
	 */
	private static void heroEffects(Item neighbor, Hero hero) {
		if (isGemstoneHeart(neighbor)) {
			hero.healing(1);
		}
		if (isRageRing(neighbor)) {
			hero.applyEffect(EffectType.RAGE, 2);
		}
		if (isBlankie(neighbor)) {
			hero.applyEffect(EffectType.DODGE, 1);
		}
	}

	/**
	 * Calculates bonus damage from a neighboring item.
	 * 
	 * @param neighbor the neighboring item
	 * @return the bonus damage amount (2 for Rough Whetstone, 0 otherwise)
	 */
	private static int bonusDamage(Item neighbor) {
		if (isRoughWhetstone(neighbor)) {
			return 2;
		}
		return 0;
	}

	/**
	 * Calculates the total damage dealt by a weapon including special weapon
	 * effects. Hatchet deals 4 damage if hero has no armor, 1 otherwise. Glass
	 * Sword deals double damage.
	 * 
	 * @param hero   the hero using the weapon
	 * @param weapon the weapon being used
	 * @return the total damage including weapon special effects
	 * @throws NullPointerException if hero or weapon is null
	 */
	public static int weaponAllDamage(Hero hero, Weapon weapon) {
		Objects.requireNonNull(hero);
		Objects.requireNonNull(weapon);

		var allDamage = weapon.stats().damage();
		if (isHatchet(weapon)) {
			if (hero.backpack().ifHasArmor()) {
				return 1;
			}
			return 4;
		}
		if (isGlassSword(weapon)) {
			return allDamage * 2;
		}
		return allDamage;
	}

	/**
	 * Checks if a weapon should be removed after use.
	 * 
	 * @param weapon the weapon to check
	 * @return true if the weapon should be removed (Glass Sword), false otherwise
	 * @throws NullPointerException if weapon is null
	 */
	public static boolean ifRemoveAfterUse(Weapon weapon) {
		Objects.requireNonNull(weapon);
		return isGlassSword(weapon);
	}

	/**
	 * Applies passive effects from all items in the hero's backpack. Thorn Armor
	 * applies spikes effect. Heart Ring applies regeneration effect.
	 * 
	 * @param hero the hero to apply passive effects to
	 * @throws NullPointerException if hero is null
	 */
	public static void passiveEffects(Hero hero) {
		Objects.requireNonNull(hero);

		var backpack = hero.backpack();
		var items = backpack.items();

		for (var item : items) {
			if (isThornArmor(item)) {
				hero.applyEffect(EffectType.SPIKES, 3);
			}
			if (isHeartRing(item)) {
				hero.applyEffect(EffectType.REGEN, 2);
			}
		}
	}

	/**
	 * Checks if an item is a Gemstone Heart.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Gemstone Heart, false otherwise
	 */
	private static boolean isGemstoneHeart(Item item) {
		return item.name().equalsIgnoreCase("Gemstone Heart");
	}

	/**
	 * Checks if an item is a Rough Whetstone.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Rough Whetstone, false otherwise
	 */
	private static boolean isRoughWhetstone(Item item) {
		return item.name().equalsIgnoreCase("Rough Whetstone");
	}

	/**
	 * Checks if an item is a Poison Whetstone.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Poison Whetstone, false otherwise
	 */
	private static boolean isPoisonWhetstone(Item item) {
		return item.name().equalsIgnoreCase("Poison Whetstone");
	}

	/**
	 * Checks if an item is a Firestone.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Firestone, false otherwise
	 */
	private static boolean isFireStone(Item item) {
		return item.name().equalsIgnoreCase("Firestone");
	}

	/**
	 * Checks if an item is a Hatchet.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Hatchet, false otherwise
	 */
	private static boolean isHatchet(Item item) {
		return item.name().equalsIgnoreCase("Hatchet");
	}

	/**
	 * Checks if an item is a Glass Sword.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Glass Sword, false otherwise
	 */
	private static boolean isGlassSword(Item item) {
		return item.name().equalsIgnoreCase("Glass Sword");
	}

	/**
	 * Checks if an item is a Froststone.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Froststone, false otherwise
	 */
	private static boolean isFrostStone(Item item) {
		return item.name().equalsIgnoreCase("Froststone");
	}

	/**
	 * Checks if an item is an Amulet of Weakness.
	 * 
	 * @param item the item to check
	 * @return true if the item is an Amulet of Weakness, false otherwise
	 */
	private static boolean isWeaknessAmulet(Item item) {
		return item.name().equalsIgnoreCase("Amulet of Weakness");
	}

	/**
	 * Checks if an item is a Dreamcatcher.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Dreamcatcher, false otherwise
	 */
	private static boolean isDreamcatcher(Item item) {
		return item.name().equalsIgnoreCase("Dreamcatcher");
	}

	/**
	 * Checks if an item is a Charmed Bracelet.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Charmed Bracelet, false otherwise
	 */
	private static boolean isCharmedBracelet(Item item) {
		return item.name().equalsIgnoreCase("Charmed Bracelet");
	}

	/**
	 * Checks if an item is a Ring of Rage.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Ring of Rage, false otherwise
	 */
	private static boolean isRageRing(Item item) {
		return item.name().equalsIgnoreCase("Ring Of Rage");
	}

	/**
	 * Checks if an item is a Blankie.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Blankie, false otherwise
	 */
	private static boolean isBlankie(Item item) {
		return item.name().equalsIgnoreCase("Blankie");
	}

	/**
	 * Checks if an item is a Thorn Armor.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Thorn Armor, false otherwise
	 */
	private static boolean isThornArmor(Item item) {
		return item.name().equalsIgnoreCase("Thorn Armor");
	}

	/**
	 * Checks if an item is a Heart Ring.
	 * 
	 * @param item the item to check
	 * @return true if the item is a Heart Ring, false otherwise
	 */
	private static boolean isHeartRing(Item item) {
		return item.name().equalsIgnoreCase("Heart Ring");
	}

}