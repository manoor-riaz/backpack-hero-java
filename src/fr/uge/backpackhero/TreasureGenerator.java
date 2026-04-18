package fr.uge.backpackhero;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Utility class for generating random treasures. Treasures contain a random
 * selection of items and gold. This class cannot be instantiated.
 */
public final class TreasureGenerator {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private TreasureGenerator() {

	}

	/**
	 * Generates a random treasure containing 3 to 5 items plus gold. The treasure
	 * includes: - Between 3 and 5 random default items - A gold item worth between
	 * 5 and 15 gold
	 * 
	 * @param random the random number generator to use
	 * @return a new Treasure with randomly generated items
	 * @throws NullPointerException if random is null
	 */
	public static Treasure generate(Random random) {
		Objects.requireNonNull(random);
		var items = new ArrayList<Item>();
		int itemCount = 3 + random.nextInt(3);

		for (int i = 0; i < itemCount; i++) {
			items.add(GenerateItems.generateADefaultItem());
		}

		items.add(new Gold(5 + random.nextInt(11)));

		return Treasure.of(items);
	}
}