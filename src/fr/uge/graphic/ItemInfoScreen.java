package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.uge.backpackhero.*;

/**
 * Screen that displays detailed information about an item. Shows item stats,
 * effects, rarity, and pricing information.
 */
public class ItemInfoScreen implements Screen {
	private final Item item;
	private final BufferedImage buttonImage;
	private final int returnScreenIndex;
	private Button returnButton;

	/**
	 * Constructs a new ItemInfoScreen.
	 *
	 * @param item              the item to display information about
	 * @param buttonImage       the background image for buttons (can be null)
	 * @param returnScreenIndex the index of the screen to return to when closed
	 * @throws NullPointerException if item is null
	 */
	public ItemInfoScreen(Item item, BufferedImage buttonImage, int returnScreenIndex) {
		this.item = Objects.requireNonNull(item, "item cannot be null");
		this.buttonImage = buttonImage;
		this.returnScreenIndex = returnScreenIndex;
	}

	/**
	 * Draws the complete item information screen including background, frame, item
	 * details, stats, and return button.
	 *
	 * @param graphics the graphics context to draw on
	 * @param width    the width of the drawing area
	 * @param height   the height of the drawing area
	 * @throws NullPointerException     if graphics is null
	 * @throws IllegalArgumentException if width or height is negative
	 */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics, "graphics cannot be null");
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}

		drawBackground(graphics, width, height);

		int frameWidth = Math.min(600, width - 100);
		int frameHeight = Math.min(500, height - 100);
		int frameX = (width - frameWidth) / 2;
		int frameY = (height - frameHeight) / 2;

		drawFrame(graphics, frameX, frameY, frameWidth, frameHeight);

		int contentX = frameX + 30;
		int contentY = frameY + 40;

		contentY = drawTitle(graphics, frameX, frameWidth, contentY);
		contentY = drawRarity(graphics, frameX, frameWidth, contentY);
		contentY = drawItemImage(graphics, frameX, frameWidth, contentY);
		contentY = drawItemType(graphics, contentX, contentY);
		contentY = drawItemStats(graphics, contentX, contentY, frameWidth - 60);

		drawReturnButton(graphics, frameX, frameY, frameWidth, frameHeight);
		drawInstructions(graphics, width, height);
	}

	/**
	 * Draws the dark background.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(30, 30, 40));
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Draws the central frame with rarity-colored border.
	 *
	 * @param graphics the graphics context
	 * @param x        the X coordinate of the frame
	 * @param y        the Y coordinate of the frame
	 * @param width    the width of the frame
	 * @param height   the height of the frame
	 */
	private void drawFrame(Graphics2D graphics, int x, int y, int width, int height) {
		Color rarityColor = getRarityColor(item.rarity());
		graphics.setColor(new Color(50, 50, 60));
		graphics.fillRoundRect(x, y, width, height, 20, 20);
		graphics.setColor(rarityColor);
		graphics.setStroke(new BasicStroke(4));
		graphics.drawRoundRect(x, y, width, height, 20, 20);
		graphics.setStroke(new BasicStroke(1));
	}

	/**
	 * Draws the item name title.
	 *
	 * @param graphics   the graphics context
	 * @param frameX     the X coordinate of the frame
	 * @param frameWidth the width of the frame
	 * @param y          the current Y coordinate
	 * @return the updated Y coordinate after drawing
	 */
	private int drawTitle(Graphics2D graphics, int frameX, int frameWidth, int y) {
		Color rarityColor = getRarityColor(item.rarity());
		graphics.setFont(new Font("Arial", Font.BOLD, 28));
		graphics.setColor(rarityColor);
		String title = item.name();
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, frameX + (frameWidth - titleWidth) / 2, y);
		return y + 20;
	}

	/**
	 * Draws the rarity text.
	 *
	 * @param graphics   the graphics context
	 * @param frameX     the X coordinate of the frame
	 * @param frameWidth the width of the frame
	 * @param y          the current Y coordinate
	 * @return the updated Y coordinate after drawing
	 */
	private int drawRarity(Graphics2D graphics, int frameX, int frameWidth, int y) {
		Color rarityColor = getRarityColor(item.rarity());
		graphics.setFont(new Font("Arial", Font.ITALIC, 16));
		graphics.setColor(rarityColor);
		String rarityText = "Rarity: " + item.rarity().name();
		int rarityWidth = graphics.getFontMetrics().stringWidth(rarityText);
		graphics.drawString(rarityText, frameX + (frameWidth - rarityWidth) / 2, y + 20);
		return y + 50;
	}

	/**
	 * Draws the item image or a placeholder if not available.
	 *
	 * @param graphics   the graphics context
	 * @param frameX     the X coordinate of the frame
	 * @param frameWidth the width of the frame
	 * @param y          the current Y coordinate
	 * @return the updated Y coordinate after drawing
	 */
	private int drawItemImage(Graphics2D graphics, int frameX, int frameWidth, int y) {
		BufferedImage itemImage = ItemImageManager.getItemImage(item.name());
		int imgSize = 100;
		int imgX = frameX + (frameWidth - imgSize) / 2;

		if (itemImage != null) {
			graphics.drawImage(itemImage, imgX, y, imgSize, imgSize, null);
		} else {
			drawPlaceholderImage(graphics, imgX, y, imgSize);
		}

		Color rarityColor = getRarityColor(item.rarity());
		graphics.setColor(rarityColor);
		graphics.drawRect(imgX, y, imgSize, imgSize);
		return y + imgSize + 30;
	}

	/**
	 * Draws a placeholder image when the item image is not available.
	 *
	 * @param graphics the graphics context
	 * @param x        the X coordinate
	 * @param y        the Y coordinate
	 * @param size     the size of the placeholder
	 */
	private void drawPlaceholderImage(Graphics2D graphics, int x, int y, int size) {
		graphics.setColor(Color.GRAY);
		graphics.fillRect(x, y, size, size);
		graphics.setColor(Color.WHITE);
		graphics.drawString("?", x + size / 2 - 5, y + size / 2 + 5);
	}

	/**
	 * Draws the item type text.
	 *
	 * @param graphics the graphics context
	 * @param x        the X coordinate
	 * @param y        the current Y coordinate
	 * @return the updated Y coordinate after drawing
	 */
	private int drawItemType(Graphics2D graphics, int x, int y) {
		graphics.setFont(new Font("Arial", Font.BOLD, 18));
		graphics.setColor(Color.WHITE);
		String typeText = "Type: " + getItemType();
		graphics.drawString(typeText, x, y);
		return y + 35;
	}

	/**
	 * Draws the return button.
	 *
	 * @param graphics    the graphics context
	 * @param frameX      the X coordinate of the frame
	 * @param frameY      the Y coordinate of the frame
	 * @param frameWidth  the width of the frame
	 * @param frameHeight the height of the frame
	 */
	private void drawReturnButton(Graphics2D graphics, int frameX, int frameY, int frameWidth, int frameHeight) {
		int btnWidth = 150;
		int btnHeight = 45;
		int btnX = frameX + (frameWidth - btnWidth) / 2;
		int btnY = frameY + frameHeight - btnHeight - 20;

		returnButton = new Button(btnX, btnY, btnWidth, btnHeight, buttonImage);
		returnButton.draw(graphics);

		drawReturnButtonLabel(graphics, btnX, btnY, btnWidth, btnHeight);
	}

	/**
	 * Draws the label on the return button.
	 *
	 * @param graphics  the graphics context
	 * @param btnX      the X coordinate of the button
	 * @param btnY      the Y coordinate of the button
	 * @param btnWidth  the width of the button
	 * @param btnHeight the height of the button
	 */
	private void drawReturnButtonLabel(Graphics2D graphics, int btnX, int btnY, int btnWidth, int btnHeight) {
		graphics.setFont(new Font("Arial", Font.BOLD, 16));
		graphics.setColor(Color.WHITE);
		String btnText = "RETURN";
		int btnTextWidth = graphics.getFontMetrics().stringWidth(btnText);
		graphics.drawString(btnText, btnX + (btnWidth - btnTextWidth) / 2,
				btnY + (btnHeight + graphics.getFontMetrics().getAscent()) / 2 - 2);
	}

	/**
	 * Draws the instructions for closing the screen.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawInstructions(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.ITALIC, 12));
		graphics.setColor(Color.GRAY);
		String hint = "Press 'i' or click Return to close";
		int hintWidth = graphics.getFontMetrics().stringWidth(hint);
		graphics.drawString(hint, (width - hintWidth) / 2, height - 20);
	}

	/**
	 * Gets the type name of the item based on its class.
	 *
	 * @return the item type as a string
	 */
	private String getItemType() {
		return switch (item) {
		case Weapon _ -> "Weapon";
		case Armor _ -> "Armor";
		case Consumables _ -> "Consumable";
		case Accessories _ -> "Accessory";
		case MagicItem _ -> "Magic Item";
		case ManaStone _ -> "Mana Stone";
		case Gold _ -> "Gold";
		case Key _ -> "Key";
		case Curses _ -> "Curse";
		default -> "Unknown";
		};
	}

	/**
	 * Draws the item statistics based on its type.
	 *
	 * @param graphics the graphics context
	 * @param x        the X coordinate
	 * @param y        the starting Y coordinate
	 * @param maxWidth the maximum width available for drawing
	 * @return the final Y coordinate after drawing all stats
	 */
	private int drawItemStats(Graphics2D graphics, int x, int y, int maxWidth) {
		int lineHeight = 25;

		graphics.setFont(new Font("Arial", Font.PLAIN, 16));
		graphics.setColor(new Color(200, 200, 200));

		y = switch (item) {
		case Weapon w -> drawWeaponStats(graphics, x, y, lineHeight, w);
		case Armor a -> drawArmorStats(graphics, x, y, lineHeight, a);
		case Consumables c -> drawConsumableStats(graphics, x, y, lineHeight, c);
		case Accessories a -> drawAccessoryStats(graphics, x, y, lineHeight, a);
		case MagicItem m -> drawMagicItemStats(graphics, x, y, lineHeight, m);
		case ManaStone m -> drawManaStoneStats(graphics, x, y, lineHeight, m);
		case Gold g -> drawGoldStats(graphics, x, y, lineHeight, g);
		case Key _ -> drawKeyStats(graphics, x, y, lineHeight);
		case Curses _ -> drawCurseStats(graphics, x, y, lineHeight);
		default -> y;
		};

		return drawPrices(graphics, x, y, lineHeight);
	}

	/**
	 * Draws statistics for a weapon item.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param weapon     the weapon item
	 * @return the updated Y coordinate
	 */
	private int drawWeaponStats(Graphics2D graphics, int x, int y, int lineHeight, Weapon weapon) {
		drawStatLine(graphics, x, y, "Energy cost", String.valueOf(weapon.cost()));
		y += lineHeight;
		if (weapon.manaCost() > 0) {
			drawStatLine(graphics, x, y, "Mana cost", String.valueOf(weapon.manaCost()));
			y += lineHeight;
		}
		drawStatLine(graphics, x, y, "Damage", String.valueOf(weapon.stats().damage()));
		y += lineHeight;
		if (weapon.stats().shield() > 0) {
			drawStatLine(graphics, x, y, "Shield", String.valueOf(weapon.stats().shield()));
			y += lineHeight;
		}
		if (weapon.stats().healing() > 0) {
			drawStatLine(graphics, x, y, "Healing", String.valueOf(weapon.stats().healing()));
			y += lineHeight;
		}
		return y;
	}

	/**
	 * Draws statistics for an armor item.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param armor      the armor item
	 * @return the updated Y coordinate
	 */
	private int drawArmorStats(Graphics2D graphics, int x, int y, int lineHeight, Armor armor) {
		drawStatLine(graphics, x, y, "Energy cost", String.valueOf(armor.cost()));
		y += lineHeight;
		if (armor.manaCost() > 0) {
			drawStatLine(graphics, x, y, "Mana cost", String.valueOf(armor.manaCost()));
			y += lineHeight;
		}
		drawStatLine(graphics, x, y, "Shield", String.valueOf(armor.stats().shield()));
		y += lineHeight;
		if (armor.cost() == 0) {
			graphics.setColor(new Color(100, 200, 100));
			graphics.drawString("Passive shield (automatic)", x, y);
			graphics.setColor(new Color(200, 200, 200));
			y += lineHeight;
		}
		return y;
	}

	/**
	 * Draws statistics for a consumable item.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param consumable the consumable item
	 * @return the updated Y coordinate
	 */
	private int drawConsumableStats(Graphics2D graphics, int x, int y, int lineHeight, Consumables consumable) {
		drawStatLine(graphics, x, y, "Energy cost", String.valueOf(consumable.cost()));
		y += lineHeight;
		if (consumable.stats().healing() > 0) {
			drawStatLine(graphics, x, y, "Healing", String.valueOf(consumable.stats().healing()));
			y += lineHeight;
		}
		if (consumable.stats().damage() > 0) {
			drawStatLine(graphics, x, y, "Damage", String.valueOf(consumable.stats().damage()));
			y += lineHeight;
		}
		if (consumable.stats().shield() > 0) {
			drawStatLine(graphics, x, y, "Shield", String.valueOf(consumable.stats().shield()));
			y += lineHeight;
		}
		graphics.setColor(new Color(255, 150, 100));
		graphics.drawString("Consumable (single use)", x, y);
		graphics.setColor(new Color(200, 200, 200));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for an accessory item.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param accessory  the accessory item
	 * @return the updated Y coordinate
	 */
	private int drawAccessoryStats(Graphics2D graphics, int x, int y, int lineHeight, Accessories accessory) {
		String effect = getAccessoryEffect(accessory.name());
		if (!effect.isEmpty()) {
			graphics.setColor(new Color(150, 150, 255));
			graphics.drawString("Effect: " + effect, x, y);
			graphics.setColor(new Color(200, 200, 200));
			y += lineHeight;
		}
		if (accessory.stats().damage() > 0) {
			drawStatLine(graphics, x, y, "Damage bonus", "+" + accessory.stats().damage());
			y += lineHeight;
		}
		if (accessory.stats().shield() > 0) {
			drawStatLine(graphics, x, y, "Shield bonus", "+" + accessory.stats().shield());
			y += lineHeight;
		}
		graphics.setColor(new Color(100, 200, 100));
		graphics.drawString("Permanent passive effect", x, y);
		graphics.setColor(new Color(200, 200, 200));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for a magic item.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param magicItem  the magic item
	 * @return the updated Y coordinate
	 */
	private int drawMagicItemStats(Graphics2D graphics, int x, int y, int lineHeight, MagicItem magicItem) {
		drawStatLine(graphics, x, y, "Mana cost", String.valueOf(magicItem.manaCost()));
		y += lineHeight;
		drawStatLine(graphics, x, y, "Damage", String.valueOf(magicItem.stats().damage()));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for a mana stone.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param manaStone  the mana stone item
	 * @return the updated Y coordinate
	 */
	private int drawManaStoneStats(Graphics2D graphics, int x, int y, int lineHeight, ManaStone manaStone) {
		drawStatLine(graphics, x, y, "Mana provided", String.valueOf(manaStone.mana()));
		y += lineHeight;
		graphics.setColor(new Color(100, 150, 255));
		graphics.drawString("Consumed when you use mana", x, y);
		graphics.setColor(new Color(200, 200, 200));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for gold.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @param gold       the gold item
	 * @return the updated Y coordinate
	 */
	private int drawGoldStats(Graphics2D graphics, int x, int y, int lineHeight, Gold gold) {
		drawStatLine(graphics, x, y, "Amount", String.valueOf(gold.purse()));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for a key.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @return the updated Y coordinate
	 */
	private int drawKeyStats(Graphics2D graphics, int x, int y, int lineHeight) {
		graphics.drawString("Allows opening locked gates", x, y);
		y += lineHeight;
		return y;
	}

	/**
	 * Draws statistics for a curse.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @return the updated Y coordinate
	 */
	private int drawCurseStats(Graphics2D graphics, int x, int y, int lineHeight) {
		graphics.setColor(new Color(200, 50, 50));
		graphics.drawString("Occupies space in your bag", x, y);
		y += lineHeight;
		graphics.drawString("Cannot be moved", x, y);
		y += lineHeight;
		graphics.drawString("Remove it at the healer", x, y);
		graphics.setColor(new Color(200, 200, 200));
		y += lineHeight;
		return y;
	}

	/**
	 * Draws the purchase and selling prices.
	 *
	 * @param graphics   the graphics context
	 * @param x          the X coordinate
	 * @param y          the starting Y coordinate
	 * @param lineHeight the height of each line
	 * @return the updated Y coordinate
	 */
	private int drawPrices(Graphics2D graphics, int x, int y, int lineHeight) {
		y += 10;
		graphics.setColor(new Color(255, 215, 0));
		graphics.drawString("Purchase price: " + item.rarity().purchasePrice() + " gold", x, y);
		y += lineHeight;
		graphics.drawString("Selling price: " + item.rarity().sellingPrice() + " gold", x, y);
		return y + lineHeight;
	}

	/**
	 * Draws a single stat line with label and value.
	 *
	 * @param graphics the graphics context
	 * @param x        the X coordinate
	 * @param y        the Y coordinate
	 * @param label    the label text
	 * @param value    the value text
	 */
	private void drawStatLine(Graphics2D graphics, int x, int y, String label, String value) {
		graphics.drawString(label + ": " + value, x, y);
	}

	/**
	 * Gets the effect description for an accessory based on its name.
	 *
	 * @param name the name of the accessory
	 * @return the effect description, or empty string if not found
	 */
	private String getAccessoryEffect(String name) {
		return switch (name) {
		case "Gemme de Coeur" -> "Increases max HP";
		case "Pierre a Aiguiser" -> "+2 damage to adjacent weapons";
		case "Pierre de Feu" -> "Burns enemies";
		case "Potion de Rage" -> "+1 damage per turn";
		case "Cape d'Esquive" -> "Chance to dodge attacks";
		case "Perle" -> "+4 damage to weapons";
		case "Fiole de Poison" -> "Poisons enemies";
		case "Pierre de Gel" -> "Freezes enemies";
		case "Amulette de Faiblesse" -> "Weakens enemies";
		case "Armure d'Epines" -> "Reflects damage";
		case "Anneau de Regeneration" -> "Regenerates HP";
		case "Talisman Tordu" -> "Damage and shield bonus";
		case "Miroir shield" -> "Reflects projectiles";
		case "Flute du Sommeil" -> "Puts enemies to sleep";
		case "Collier de Charme" -> "Charms enemies";
		default -> "";
		};
	}

	/**
	 * Gets the color associated with an item's rarity.
	 *
	 * @param rarity the rarity level
	 * @return the corresponding color
	 */
	private Color getRarityColor(Rarity rarity) {
		return switch (rarity) {
		case COMMON -> Color.WHITE;
		case RARE -> new Color(70, 130, 255);
		case EPIC -> new Color(180, 70, 255);
		case LEGENDARY -> new Color(255, 180, 0);
		};
	}

	/**
	 * Handles mouse click events on the item info screen. Closes the screen if the
	 * return button is clicked.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (returnButton != null && returnButton.contains(x, y)) {
			closeScreen(manager);
		}
	}

	/**
	 * Handles key press events. Pressing 'i' or 'I' closes the screen.
	 *
	 * @param key     the key that was pressed
	 * @param x       the X coordinate of the mouse
	 * @param y       the Y coordinate of the mouse
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleKeyPress(char key, int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (key == 'i' || key == 'I') {
			closeScreen(manager);
		}
	}

	/**
	 * Closes the item info screen and returns to the previous screen.
	 *
	 * @param manager the screen manager
	 */
	private void closeScreen(ScreenManager manager) {
		manager.removeLastScreen();
		manager.goToScreen(returnScreenIndex);
	}

	/**
	 * Handles mouse move events. This screen does not respond to mouse movements.
	 *
	 * @param x the X coordinate of the mouse
	 * @param y the Y coordinate of the mouse
	 */
	@Override
	public void handleMouseMove(int x, int y) {
	}

	/**
	 * Handles mouse release events. This screen does not respond to mouse releases.
	 *
	 * @param x       the X coordinate of the release
	 * @param y       the Y coordinate of the release
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");
	}

	/**
	 * Handles item rotation events. This screen does not support item rotation.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	@Override
	public void handleRotate(int x, int y) {
	}

	/**
	 * Handles right-click events. This screen does not respond to right-clicks.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	@Override
	public void handleRightClick(int x, int y) {
	}
}