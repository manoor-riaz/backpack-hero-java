package fr.uge.backpackhero;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Calculates the final score for a hero. Score is based on HP, level, XP, and
 * item values.
 */
public class ScoreCalculator {

	/** Default constructor */
	public ScoreCalculator() {}
	
	
	private static final int HP_MULTIPLIER = 10;
	private static final int LEVEL_MULTIPLIER = 50;
	private static final int XP_MULTIPLIER = 2;

	/**
	 * Calculates the total score for a hero.
	 * 
	 * @param hero : the hero to calculate score for
	 * @return : the total score
	 */
	public static int calculateScore(Hero hero) {
		Objects.requireNonNull(hero);

		int hpScore = hero.maxhp() * HP_MULTIPLIER;
		int levelScore = hero.level() * LEVEL_MULTIPLIER;
		int xpScore = hero.xp() * XP_MULTIPLIER;
		int itemsScore = calculateItemsValue(hero.backpack());

		return hpScore + levelScore + xpScore + itemsScore;
	}

	/**
	 * Calculates the total value of items in the backpack. Each item is counted
	 * only once.
	 * 
	 * @param backpack : the backpack to evaluate
	 * @return : the total value of items
	 */
	private static int calculateItemsValue(BackPack backpack) {
		Set<Item> countedItems = new HashSet<>();
		int total = 0;

		for (int row = 0; row < backpack.height(); row++) {
			for (int col = 0; col < backpack.width(); col++) {
				Item item = backpack.itemAt(row, col);
				if (item != null && countedItems.add(item)) {
					total += item.rarity().sellingPrice();
				}
			}
		}

		return total;
	}

	/**
	 * Returns a detailed breakdown of the score calculation.
	 * 
	 * @param hero : the hero to analyze
	 * @return : a formatted string with score details
	 */
	public static String getScoreBreakdown(Hero hero) {
		Objects.requireNonNull(hero);

		int hpScore = hero.maxhp() * HP_MULTIPLIER;
		int levelScore = hero.level() * LEVEL_MULTIPLIER;
		int xpScore = hero.xp() * XP_MULTIPLIER;
		int itemsScore = calculateItemsValue(hero.backpack());

		return String.format(
				"HP Max: %d × %d = %d\n" + "Level: %d × %d = %d\n" + "XP: %d × %d = %d\n" + "Item value: %d\n"
						+ "TOTAL: %d",
				hero.maxhp(), HP_MULTIPLIER, hpScore, hero.level(), LEVEL_MULTIPLIER, levelScore, hero.xp(),
				XP_MULTIPLIER, xpScore, itemsScore, hpScore + levelScore + xpScore + itemsScore);
	}
}