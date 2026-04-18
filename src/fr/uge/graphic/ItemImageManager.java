package fr.uge.graphic;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Centralized manager for item images. Loads images once and provides them to
 * all BackpackView instances.
 */
public class ItemImageManager {
	private static final Map<String, BufferedImage> itemImages = new HashMap<>();

	/**
	 * This class is a static utility container.
	 */
	private ItemImageManager() {
	}

	/**
	 * Loads all item images from the image directory.
	 * 
	 * Images are expected to be in src/image/ directory. If you want to change
	 * image names, modify the second parameter of loadItemImage().
	 */
	public static void loadAllItemImages() {
		try {
			loadItemImage("Épée en Bois", "wooden_sword.png");
			loadItemImage("Dague", "dague.png");
			loadItemImage("Hatchet", "Hatchet.png");
			loadItemImage("Bouclier en Bois", "rough_buckler.png");
			loadItemImage("Pierre de Santé", "pierre.png");
			loadItemImage("Repas", "meal.png");

			loadItemImage("Épée de duel", "epee_duel.png");
			loadItemImage("Bouclier du soldat", "bouclier_soldat.png");
			loadItemImage("Arbalete", "arbalete.png");
			loadItemImage("Warhammer", "Warhammer.png");
			loadItemImage("Gemstone Heart", "Gemstone_Heart.png");
			loadItemImage("Rough Whetstone", "Rough_Whetstone.png");
			loadItemImage("Firestone", "Firestone.png");
			loadItemImage("Ring Of Rage", "Ring_Of_Rage.png");
			loadItemImage("Blankie", "Blankie.png");

			loadItemImage("Masse", "masse.png");
			loadItemImage("Glass Sword", "Glass_Sword.png");
			loadItemImage("Overgrown Axe", "Overgrown_Axe.png");
			loadItemImage("Perle", "perle.png");
			loadItemImage("Poison Whetstone", "Poison_Whetstone.png");
			loadItemImage("Froststone", "Froststone.png");
			loadItemImage("Amulet of Weakness", "Amulet_of_Weakness.png");
			loadItemImage("Thorn Armor", "Thorn_Armor.png");
			loadItemImage("Heart Ring", "Heart_Ring.png");
			loadItemImage("Talisman", "Talisman.png");

			loadItemImage("Lance brutale", "lance_brutale.png");
			loadItemImage("Lame Sacree", "Lame_Sacre.png");
			loadItemImage("Miroir shield", "miroir_shield.png");
			loadItemImage("Dreamcatcher", "Dreamcatcher.png");
			loadItemImage("Charmed Bracelet", "Charmed_Bracelet.png");

			loadItemImage("Key", "key.png");
			loadItemImage("Mana Stone", "mana_stone.png");
			loadItemImage("Gold", "gold.png");

			loadItemImage("Earthstone Blade", "earthstone_blade.png");
			loadItemImage("Knight Shield", "knightshield.png");
			loadItemImage("Cup", "cup.png");
			loadItemImage("Wooden Sword", "wooden_sword.png");
			loadItemImage("Rough Buckler", "rough_buckler.png");

			try {
				ImageLoader defaultLoader = new ImageLoader("src/image", "next.png");
				itemImages.put("Default", defaultLoader.getImage());
			} catch (Exception e) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to load a single item image.
	 *
	 * @param itemName : the name of the item
	 * @param fileName : the image file name
	 */
	private static void loadItemImage(String itemName, String fileName) {
		try {
			ImageLoader loader = new ImageLoader("src/image", fileName);
			itemImages.put(itemName, loader.getImage());
		} catch (Exception e) {

		}
	}

	/**
	 * Gets the image for an item by name.
	 *
	 * @param itemName : the name of the item
	 * @return the image, or the default image if not found
	 */
	public static BufferedImage getItemImage(String itemName) {
		Objects.requireNonNull(itemName, "itemName cannot be null");

		BufferedImage img = itemImages.get(itemName);
		if (img == null) {
			return itemImages.get("Default");
		}
		return img;
	}

	/**
	 * Configures a BackpackView with all loaded images.
	 *
	 * @param backpackView : the BackpackView to configure
	 */
	public static void configureBackpackView(BackpackView backpackView) {
		Objects.requireNonNull(backpackView, "backpackView cannot be null");

		for (var entry : itemImages.entrySet()) {
			backpackView.addItemImage(entry.getKey(), entry.getValue());
		}
	}

}