package fr.uge.backpackhero;

import module java.base;
import java.util.ArrayList;

/**
 * Utility class for generating random items. Provides different item pools
 * based on rarity.
 */
public class GenerateItems {

	/**
	 * Creates a new instance of GenerateItems. This constructor does not perform
	 */
	public GenerateItems() {

	}

	private static final List<Item> COMMON_ITEMS = List.of(
			new Weapon("Épée en Bois", Rarity.COMMON, Shape.rectangle(new Dimension(3, 1)), 1, new Stats(6, 0, 0), 0),
			new Weapon("Dague", Rarity.COMMON, Shape.rectangle(new Dimension(2, 1)), 1, new Stats(4, 0, 0), 0),
			new Weapon("Hatchet", Rarity.COMMON, Shape.rectangle(new Dimension(2, 1)), 1, new Stats(4, 0, 0), 0),

			new Armor("Bouclier en Bois", Rarity.COMMON, Shape.rectangle(new Dimension(2, 2)), 1, new Stats(0, 7, 0),
					0),
			new Armor("Bouclier en Bois", Rarity.COMMON, Shape.rectangle(new Dimension(2, 2)), 1, new Stats(0, 7, 0),
					0),

			new Consumables("Pierre de Santé", Rarity.COMMON, Shape.rectangle(new Dimension(1, 1)), 0,
					new Stats(0, 0, 3), 0),
			new Consumables("Repas", Rarity.COMMON, new Shape(List.of(new Position(0, 0), new Position(0, 1))), 0,
					new Stats(0, 0, 5), 0),
			new Key(), new ManaStone(Rarity.COMMON, 1), new Gold(5));

	private static final List<Item> RARE_ITEMS = List.of(
			new Weapon("Épée de duel", Rarity.RARE, Shape.rectangle(new Dimension(3, 1)), 2, new Stats(10, 0, 0), 0),
			new Weapon("Warhammer", Rarity.RARE,
					new Shape(List.of(new Position(0, 0), new Position(0, 1), new Position(0, 2), new Position(1, 1))),
					2, new Stats(12, 0, 0), 0),

			new Armor("Bouclier du soldat", Rarity.RARE, Shape.rectangle(new Dimension(2, 2)), 1, new Stats(0, 12, 0),
					0),

			new MagicItem("Arbalete", Rarity.RARE, Shape.rectangle(new Dimension(1, 1)), new Stats(8, 0, 0), 1),

			new Accessories("Gemstone Heart", Rarity.RARE, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0),
					0),
			new Accessories("Rough Whetstone", Rarity.RARE, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0),
					0),
			new Accessories("Firestone", Rarity.RARE, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0), 0),
			new Accessories("Ring Of Rage", Rarity.RARE, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0),
					0),
			new Accessories("Blankie", Rarity.RARE, Shape.rectangle(new Dimension(1, 2)), 0, new Stats(0, 0, 0), 0),

			new Gold(15));

	private static final List<Item> EPIC_ITEMS = List.of(
			new Weapon("Masse", Rarity.EPIC, Shape.rectangle(new Dimension(2, 1)), 2, new Stats(15, 0, 0), 0),
			new Weapon("Glass Sword", Rarity.EPIC, Shape.rectangle(new Dimension(3, 1)), 1, new Stats(8, 0, 0), 0),
			new Weapon("Overgrown Axe", Rarity.EPIC,
					new Shape(List.of(new Position(0, 0), new Position(1, 0), new Position(2, 0), new Position(2, 1))),
					2, new Stats(18, 0, 0), 0),

			new Accessories("Perle", Rarity.EPIC, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(4, 0, 0), 0),
			new Accessories("Poison Whetstone", Rarity.EPIC, Shape.rectangle(new Dimension(1, 1)), 0,
					new Stats(0, 0, 0), 0),
			new Accessories("Froststone", Rarity.EPIC, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0), 0),
			new Accessories("Amulet of Weakness", Rarity.EPIC, Shape.rectangle(new Dimension(1, 1)), 0,
					new Stats(0, 0, 0), 0),
			new Accessories("Thorn Armor", Rarity.EPIC, Shape.rectangle(new Dimension(2, 2)), 1, new Stats(0, 5, 0), 0),
			new Accessories("Heart Ring", Rarity.EPIC, Shape.rectangle(new Dimension(1, 1)), 0, new Stats(0, 0, 0), 0),
			new Accessories("Talisman", Rarity.EPIC,
					new Shape(List.of(new Position(0, 1), new Position(0, 2), new Position(1, 0), new Position(1, 1))),
					0, new Stats(3, 3, 0), 0),

			new Gold(30));

	private static final List<Item> LEGENDARY_ITEMS = List.of(
			new Weapon("Lance brutale", Rarity.LEGENDARY, Shape.rectangle(new Dimension(3, 1)), 2, new Stats(25, 0, 0),
					0),
			new Weapon("Lame Sacree", Rarity.LEGENDARY,
					new Shape(List.of(new Position(0, 1), new Position(1, 0), new Position(1, 1), new Position(1, 2),
							new Position(2, 1))),
					3, new Stats(30, 5, 0), 0),

			new Accessories("Miroir shield", Rarity.LEGENDARY, Shape.rectangle(new Dimension(2, 1)), 1,
					new Stats(2, 6, 0), 0),
			new Accessories("Dreamcatcher", Rarity.LEGENDARY, Shape.rectangle(new Dimension(2, 1)), 0,
					new Stats(0, 0, 0), 0),
			new Accessories("Charmed Bracelet", Rarity.LEGENDARY, Shape.rectangle(new Dimension(1, 1)), 0,
					new Stats(0, 0, 0), 0),

			new Gold(50));

	private static final int[] PROBABILITIES_DEFAULT = { 60, 25, 12, 3 };

	private static final int[] PROBABILITIES_COMBAT_REWARD = { 50, 30, 15, 5 };

	private static final int[] PROBABILITIES_RARE_TREASURE = { 20, 40, 30, 10 };

	/**
	 * Generates a random item with default probabilities.
	 * 
	 * @return : a random item
	 */
	public static Item generateADefaultItem() {
		return generateARandomItem(PROBABILITIES_DEFAULT);
	}

	/**
	 * Generates a random item with custom probabilities.
	 * 
	 * @param probabilities : array of 4 probabilities for Common, Rare, Epic,
	 *                      Legendary
	 * @return : a random item
	 * @throws IllegalArgumentException : if probabilities array doesn't have
	 *                                  exactly 4 values
	 */
	public static Item generateARandomItem(int[] probabilities) {
		Objects.requireNonNull(probabilities);

		if (probabilities.length != 4) {
			throw new IllegalArgumentException("You must have 4 values of probabilities");
		}

		var rarity = selectARarity(probabilities, new Random());
		return generateItemOfRarity(rarity, new Random());
	}

	/**
	 * Selects a rarity based on probability distribution.
	 * 
	 * @param probabilities : the probability weights for each rarity
	 * @return : the selected rarity
	 */
	private static Rarity selectARarity(int[] probabilities, Random random) {
		var total = Arrays.stream(probabilities).sum();
		var proba = random.nextInt(total);

		Rarity[] rarities = { Rarity.COMMON, Rarity.RARE, Rarity.EPIC, Rarity.LEGENDARY };

		var sum = 0;
		for (int i = 0; i < probabilities.length; i++) {
			sum += probabilities[i];
			if (proba < sum) {
				return rarities[i];
			}
		}
		throw new IllegalStateException();
	}

	/**
	 * Generates a random item of the specified rarity.
	 * 
	 * @param rarity : the desired rarity
	 * @param random the random generator to use
	 * @return : a random item of that rarity
	 */
	public static Item generateItemOfRarity(Rarity rarity, Random random) {
		Objects.requireNonNull(rarity);
		Objects.requireNonNull(random);

		var rarityItems = switch (rarity) {
		case COMMON -> COMMON_ITEMS;
		case RARE -> RARE_ITEMS;
		case EPIC -> EPIC_ITEMS;
		case LEGENDARY -> LEGENDARY_ITEMS;
		};

		var selectedItem = rarityItems.get(random.nextInt(rarityItems.size()));
		return copyItem(selectedItem);
	}

	private static Item copyItem(Item item) {
		return switch (item) {
		case Weapon w -> new Weapon(w.name(), w.rarity(), w.shape(), w.cost(), w.stats(), w.manaCost());
		case Armor a -> new Armor(a.name(), a.rarity(), a.shape(), a.cost(), a.stats(), a.manaCost());
		case Consumables c -> new Consumables(c.name(), c.rarity(), c.shape(), c.cost(), c.stats(), c.manaCost());
		case Accessories a -> new Accessories(a.name(), a.rarity(), a.shape(), a.cost(), a.stats(), a.manaCost());
		case MagicItem m -> new MagicItem(m.name(), m.rarity(), m.shape(), m.stats(), m.manaCost());
		case ManaStone m -> new ManaStone(m.rarity(), m.mana());
		case Gold g -> new Gold(g.purse());
		case Key _ -> new Key();
		case Curses c -> new Curses(c.name(), c.rarity(), c.shape());
		};
	}

	/**
	 * Generates a list of items as combat reward.
	 * 
	 * @param nbItems : the number of items to generate
	 * @return : a list of random items
	 * @throws IllegalArgumentException : if nbItems is negative
	 */
	public static List<Item> generateCombatReward(int nbItems) {
		if (nbItems < 0) {
			throw new IllegalArgumentException("You can't have less than 0 items");
		}

		var res = new ArrayList<Item>();
		for (int i = 0; i < nbItems; i++) {
			res.add(generateARandomItem(PROBABILITIES_COMBAT_REWARD));
		}

		return res;
	}

	/**
	 * Generates a rare treasure item.
	 * 
	 * @return : a random item with rare treasure probabilities
	 */
	public static Item generateRareTreasure() {
		return generateARandomItem(PROBABILITIES_RARE_TREASURE);
	}

	/**
	 * Generates a default amount of gold.
	 * 
	 * @return : a Gold item with random amount between 10 and 30
	 */
	public static Gold generateDefaultGold() {
		return new Gold(10 + new Random().nextInt(21));
	}

}