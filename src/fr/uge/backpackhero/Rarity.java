package fr.uge.backpackhero;

/**
 * Represents the rarity levels of items. Each rarity has associated selling and
 * purchase prices.
 */
public enum Rarity {
	/**
	 * Common items with selling price 3 and purchase price 6.
	 */
	COMMON(3, 6),

	/**
	 * Rare items with selling price 6 and purchase price 12.
	 */
	RARE(6, 12),

	/**
	 * Epic items with selling price 10 and purchase price 20.
	 */
	EPIC(10, 20),

	/**
	 * Legendary items with selling price 17 and purchase price 35.
	 */
	LEGENDARY(17, 35);

	private final int sellingPrice;
	private final int purchasePrice;

	/**
	 * Creates a rarity with specified prices.
	 * 
	 * @param sellPrice : the selling price
	 * @param buyPrice  : the purchase price
	 */
	Rarity(int sellPrice, int buyPrice) {
		this.sellingPrice = sellPrice;
		this.purchasePrice = buyPrice;
	}

	/**
	 * Returns the selling price for this rarity.
	 * 
	 * @return : the selling price
	 */
	public int sellingPrice() {
		return sellingPrice;
	}

	/**
	 * Returns the purchase price for this rarity.
	 * 
	 * @return : the purchase price
	 */
	public int purchasePrice() {
		return purchasePrice;
	}
}