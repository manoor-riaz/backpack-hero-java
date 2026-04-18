package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.*;

/**
 * Screen representing the merchant shop where players can buy and sell items.
 * Players can purchase items from the shop inventory and sell items from their
 * backpack.
 */
public class MerchantScreen implements Screen {
	private final GameData gameData;
	private final BufferedImage merchantImage;
	private final BufferedImage buttonImage;
	private final BackpackView backpackView;
	private final List<Button> buttons = new ArrayList<>();
	private final List<ShopItem> shopItems = new ArrayList<>();
	private ShopItem selectedShopItem = null;
	private long lastClickTime = 0;

	private static final int DOUBLE_CLICK_THRESHOLD_MS = 300;
	private static final int SHOP_ITEM_TARGET_COUNT = 10;
	private static final int SHOP_ITEM_SIZE = 70;
	private static final int SHOP_ITEM_GAP = 10;
	private static final int SHOP_COLUMNS = 5;

	/**
	 * Constructs a new MerchantScreen.
	 *
	 * @param gameData      the game data containing hero and backpack information
	 * @param merchantImage the image representing the merchant (can be null)
	 * @param buttonImage   the background image for buttons (can be null)
	 * @throws NullPointerException if gameData is null
	 */
	public MerchantScreen(GameData gameData, BufferedImage merchantImage, BufferedImage buttonImage) {
		this.gameData = Objects.requireNonNull(gameData, "gameData cannot be null");
		this.merchantImage = merchantImage;
		this.buttonImage = buttonImage;

		this.backpackView = new BackpackView(gameData.hero().backpack(), 5, 7, 70);
		ItemImageManager.configureBackpackView(backpackView);
		backpackView.setShowLockedCells(false);

		List<Item> items = gameData.generateMerchantInventory(SHOP_ITEM_TARGET_COUNT);
		for (Item item : items) {
			shopItems.add(new ShopItem(item, 0, 0, 60, 60));
		}
	}

	/**
	 * Creates and adds the merchant buttons to the screen.
	 *
	 * @param buttonImage  the background image for buttons (can be null)
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 * @throws IllegalArgumentException if screenWidth or screenHeight is negative
	 */
	public void addMerchantButtons(BufferedImage buttonImage, int screenWidth, int screenHeight) {
		if (screenWidth < 0) {
			throw new IllegalArgumentException("screenWidth cannot be negative");
		}
		if (screenHeight < 0) {
			throw new IllegalArgumentException("screenHeight cannot be negative");
		}

		int btnWidth = Math.max(160, screenWidth / 6);
		int btnHeight = Math.max(40, screenHeight / 15);
		int quitBtnX = screenWidth - btnWidth - Math.max(25, screenWidth / 40);
		int quitBtnY = screenHeight - Math.max(60, screenHeight / 10);
		buttons.add(new Button(quitBtnX, quitBtnY, btnWidth, btnHeight, buttonImage));
	}

	/**
	 * Draws the complete merchant screen including title, merchant image, gold
	 * information, backpack, shop items, and buttons.
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

		Hero hero = gameData.hero();

		graphics.setColor(new Color(80, 50, 20));
		graphics.fillRect(0, 0, width, height);

		drawTitle(graphics, width, height);
		drawMerchantImage(graphics, width, height);
		drawGoldInfo(graphics, width, height, hero);

		positionAndDrawBackpack(graphics, width, height);
		drawShopItems(graphics, width, Math.max(200, height / 3), hero.backpack().getGoldTotal());
		drawButtons(graphics);
	}

	/**
	 * Draws the merchant title at the top of the screen.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawTitle(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(255, 215, 0));
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(28, height / 20)));
		String title = "THE MERCHANT";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, Math.max(50, height / 12));
	}

	/**
	 * Draws the merchant image if available.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawMerchantImage(Graphics2D graphics, int width, int height) {
		if (merchantImage != null) {
			int imgSize = Math.max(100, Math.min(width, height) / 8);
			int imgX = (width / 2) - (imgSize / 2);
			graphics.drawImage(merchantImage, imgX, Math.max(60, height / 10), imgSize, imgSize, null);
		}
	}

	/**
	 * Draws the player's current gold amount.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 * @param hero     the hero
	 */
	private void drawGoldInfo(Graphics2D graphics, int width, int height, Hero hero) {
		int currentGold = hero.backpack().getGoldTotal();
		graphics.setColor(new Color(255, 215, 0));
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(18, height / 35)));
		String goldInfo = "Gold: " + currentGold;
		int goldWidth = graphics.getFontMetrics().stringWidth(goldInfo);
		graphics.drawString(goldInfo, (width - goldWidth) / 2, Math.max(180, height / 4));
	}

	/**
	 * Positions and draws the hero's backpack view.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void positionAndDrawBackpack(Graphics2D graphics, int width, int height) {
		int backpackViewX = (width / 2) - (backpackView.getWidth() / 2);
		int backpackViewY = height - backpackView.getHeight() - Math.max(60, height / 150);
		backpackView.setPosition(backpackViewX, backpackViewY);

		backpackView.draw(graphics, gameData);
	}

	/**
	 * Draws all shop items in a grid layout.
	 *
	 * @param g           the graphics context
	 * @param screenWidth the width of the screen
	 * @param shopStartY  the Y coordinate where the shop starts
	 * @param currentGold the player's current gold amount
	 */
	private void drawShopItems(Graphics2D g, int screenWidth, int shopStartY, int currentGold) {
		int itemBoxSize = Math.min(SHOP_ITEM_SIZE, screenWidth / 18);
		int gap = SHOP_ITEM_GAP;
		int cols = SHOP_COLUMNS;

		int totalShopWidth = cols * itemBoxSize + (cols - 1) * gap;
		int startX = (screenWidth / 2) - (totalShopWidth / 2);

		for (int i = 0; i < shopItems.size(); i++) {
			ShopItem shopItem = shopItems.get(i);
			calculateShopItemPosition(shopItem, i, cols, startX, shopStartY, itemBoxSize, gap);
			drawSingleShopItem(g, shopItem, itemBoxSize, currentGold);
		}
	}

	/**
	 * Calculates and sets the position of a shop item in the grid.
	 *
	 * @param shopItem    the shop item to position
	 * @param index       the index of the item in the list
	 * @param cols        the number of columns in the grid
	 * @param startX      the starting X coordinate of the grid
	 * @param shopStartY  the starting Y coordinate of the grid
	 * @param itemBoxSize the size of each item box
	 * @param gap         the gap between items
	 */
	private void calculateShopItemPosition(ShopItem shopItem, int index, int cols, int startX, int shopStartY,
			int itemBoxSize, int gap) {
		int row = index / cols;
		int col = index % cols;
		shopItem.setX(startX + col * (itemBoxSize + gap));
		shopItem.setY(shopStartY + row * (itemBoxSize + gap + Math.max(50, itemBoxSize / 2)));
		shopItem.setWidth(itemBoxSize);
		shopItem.setHeight(itemBoxSize);
	}

	/**
	 * Draws a single shop item with its image, price, and availability status.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item to draw
	 * @param itemBoxSize the size of the item box
	 * @param currentGold the player's current gold amount
	 */
	private void drawSingleShopItem(Graphics2D g, ShopItem shopItem, int itemBoxSize, int currentGold) {
		if (shopItem.isSold()) {
			drawSoldItem(g, shopItem, itemBoxSize);
			return;
		}

		Item item = shopItem.getItem();
		int price = item.rarity().purchasePrice();

		drawItemHighlightIfSelected(g, shopItem, itemBoxSize);
		drawItemBox(g, shopItem, itemBoxSize, item);
		drawItemImage(g, shopItem, itemBoxSize, item);
		drawItemPrice(g, shopItem, itemBoxSize, price);
		drawUnavailableOverlay(g, shopItem, itemBoxSize, currentGold, price);
	}

	/**
	 * Draws a sold item with a grayed-out appearance.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the sold shop item
	 * @param itemBoxSize the size of the item box
	 */
	private void drawSoldItem(Graphics2D g, ShopItem shopItem, int itemBoxSize) {
		g.setColor(new Color(50, 50, 50));
		g.fillRect(shopItem.getX(), shopItem.getY(), itemBoxSize, itemBoxSize);
		g.setColor(Color.GRAY);
		g.drawString("SOLD", shopItem.getX() + 5, shopItem.getY() + itemBoxSize / 2);
	}

	/**
	 * Draws a highlight around the selected shop item.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item
	 * @param itemBoxSize the size of the item box
	 */
	private void drawItemHighlightIfSelected(Graphics2D g, ShopItem shopItem, int itemBoxSize) {
		if (shopItem == selectedShopItem) {
			g.setColor(new Color(255, 255, 255, 100));
			g.fillRoundRect(shopItem.getX() - 5, shopItem.getY() - 5, itemBoxSize + 10, itemBoxSize + 10, 10, 10);
		}
	}

	/**
	 * Draws the box background and rarity-colored border for a shop item.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item
	 * @param itemBoxSize the size of the item box
	 * @param item        the item
	 */
	private void drawItemBox(Graphics2D g, ShopItem shopItem, int itemBoxSize, Item item) {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(shopItem.getX(), shopItem.getY(), itemBoxSize, itemBoxSize);
		g.setColor(getRarityColor(item.rarity()));
		g.setStroke(new BasicStroke(2));
		g.drawRect(shopItem.getX(), shopItem.getY(), itemBoxSize, itemBoxSize);
		g.setStroke(new BasicStroke(1));
	}

	/**
	 * Draws the item image inside the shop item box.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item
	 * @param itemBoxSize the size of the item box
	 * @param item        the item
	 */
	private void drawItemImage(Graphics2D g, ShopItem shopItem, int itemBoxSize, Item item) {
		BufferedImage itemImage = ItemImageManager.getItemImage(item.name());
		if (itemImage != null) {
			g.drawImage(itemImage, shopItem.getX() + 5, shopItem.getY() + 5, itemBoxSize - 10, itemBoxSize - 10, null);
		}
	}

	/**
	 * Draws the price below the shop item.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item
	 * @param itemBoxSize the size of the item box
	 * @param price       the price of the item
	 */
	private void drawItemPrice(Graphics2D g, ShopItem shopItem, int itemBoxSize, int price) {
		g.setColor(new Color(255, 215, 0));
		g.setFont(new Font("Arial", Font.BOLD, Math.max(12, itemBoxSize / 5)));
		g.drawString(price + " Gold", shopItem.getX() + 2,
				shopItem.getY() + itemBoxSize + Math.max(15, itemBoxSize / 4));
	}

	/**
	 * Draws a dark overlay on items that cannot be afforded.
	 *
	 * @param g           the graphics context
	 * @param shopItem    the shop item
	 * @param itemBoxSize the size of the item box
	 * @param currentGold the player's current gold amount
	 * @param price       the price of the item
	 */
	private void drawUnavailableOverlay(Graphics2D g, ShopItem shopItem, int itemBoxSize, int currentGold, int price) {
		if (currentGold < price) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(shopItem.getX(), shopItem.getY(), itemBoxSize, itemBoxSize);
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, Math.max(10, itemBoxSize / 6)));
			g.drawString("X", shopItem.getX() + itemBoxSize / 2 - 5, shopItem.getY() + itemBoxSize / 2 + 5);
		}
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
		case RARE -> Color.BLUE;
		case EPIC -> new Color(128, 0, 128);
		case LEGENDARY -> new Color(255, 165, 0);
		};
	}

	/**
	 * Draws all buttons with their labels.
	 *
	 * @param graphics the graphics context
	 */
	private void drawButtons(Graphics2D graphics) {
		for (var button : buttons) {
			button.draw(graphics);
		}

		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, 16)));

		for (int i = 0; i < buttons.size(); i++) {
			Button btn = buttons.get(i);
			drawButtonLabel(graphics, btn);
		}
	}

	/**
	 * Draws the label on a button.
	 *
	 * @param graphics the graphics context
	 * @param btn      the button
	 */
	private void drawButtonLabel(Graphics2D graphics, Button btn) {
		graphics.setColor(Color.BLACK);
		String label = "LEAVE SHOP";
		int textWidth = graphics.getFontMetrics().stringWidth(label);
		graphics.drawString(label, btn.getX() + (btn.getWidth() - textWidth) / 2,
				btn.getY() + (btn.getHeight() + graphics.getFontMetrics().getAscent()) / 2);
	}

	/**
	 * Handles mouse click events on the merchant screen. Processes clicks on
	 * buttons, shop items, and backpack.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		long currentTime = System.currentTimeMillis();
		boolean isDoubleClick = (currentTime - lastClickTime) < DOUBLE_CLICK_THRESHOLD_MS;
		lastClickTime = currentTime;

		if (handleQuitButtonClick(x, y, manager))
			return;
		if (handleShopItemClick(x, y))
			return;
		handleBackpackClick(x, y, isDoubleClick);
	}

	/**
	 * Handles clicks on the quit button.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @return true if the quit button was clicked
	 */
	private boolean handleQuitButtonClick(int x, int y, ScreenManager manager) {
		if (!buttons.isEmpty() && buttons.get(0).contains(x, y)) {
			manager.goToScreen(1);
			return true;
		}
		return false;
	}

	/**
	 * Handles clicks on shop items. Single click selects an item, clicking again
	 * attempts to purchase it.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 * @return true if a shop item was clicked
	 */
	private boolean handleShopItemClick(int x, int y) {
		for (ShopItem shopItem : shopItems) {
			if (!shopItem.isSold() && shopItem.contains(x, y)) {
				if (selectedShopItem == shopItem) {
					tryBuyItem(shopItem, x, y);
				} else {
					selectedShopItem = shopItem;
				}
				return true;
			}
		}
		selectedShopItem = null;
		return false;
	}

	/**
	 * Handles clicks on the backpack area. Single click starts dragging, double
	 * click sells an item.
	 *
	 * @param x             the X coordinate of the click
	 * @param y             the Y coordinate of the click
	 * @param isDoubleClick whether this is a double click
	 */
	private void handleBackpackClick(int x, int y, boolean isDoubleClick) {
		if (backpackView.contains(x, y)) {
			if (isDoubleClick) {
				handleSellFromBackpack(x, y);
			} else {
				backpackView.startDrag(x, y);
			}
		} else {
			if (isDoubleClick) {
				backpackView.handleDoubleClick(x, y);
			} else {
				backpackView.startDrag(x, y);
			}
		}
	}

	/**
	 * Handles selling an item from the backpack through double-clicking.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	private void handleSellFromBackpack(int x, int y) {
		int col = (x - backpackView.getX()) / (backpackView.getItemSize() + 5);
		int row = (y - backpackView.getY()) / (backpackView.getItemSize() + 5);
		Item clickedItem = gameData.hero().backpack().itemAt(row, col);
		if (clickedItem != null && !clickedItem.isGold()) {
			sellItem(clickedItem);
		}
	}

	/**
	 * Attempts to purchase a shop item. Deducts gold and adds the item to the
	 * staging area if successful.
	 *
	 * @param shopItem the shop item to purchase
	 * @param mouseX   the X coordinate of the mouse
	 * @param mouseY   the Y coordinate of the mouse
	 */
	private void tryBuyItem(ShopItem shopItem, int mouseX, int mouseY) {
		if (gameData.buyFromMerchant(shopItem.getItem())) {
			backpackView.addToStaging(shopItem.getItem(), mouseX - 30, mouseY - 30);
			shopItem.setSold(true);
			selectedShopItem = null;
		}
	}

	/**
	 * Sells an item from the backpack and adds gold.
	 *
	 * @param item the item to sell
	 */
	private void sellItem(Item item) {
		gameData.sellToMerchant(item);
	}

	/**
	 * Handles mouse move events. Updates drag position if an item is being dragged.
	 *
	 * @param x the X coordinate of the mouse
	 * @param y the Y coordinate of the mouse
	 */
	@Override
	public void handleMouseMove(int x, int y) {
		if (backpackView.isDragging()) {
			backpackView.updateDrag(x, y);
		}
	}

	/**
	 * Handles mouse release events. Ends dragging if an item is being dragged.
	 *
	 * @param x       the X coordinate of the release
	 * @param y       the Y coordinate of the release
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (backpackView.isDragging()) {
			backpackView.endDrag(x, y);
		}
	}

	/**
	 * Handles item rotation events. Rotates the item being dragged or the item at
	 * the specified position.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	@Override
	public void handleRotate(int x, int y) {
		if (backpackView.isDragging()) {
			backpackView.rotateItemUnderMouse();
		} else {
			backpackView.rotateItemAt(x, y);
		}
	}

	/**
	 * Handles right-click events. Triggers a double-click action on the backpack
	 * view.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	@Override
	public void handleRightClick(int x, int y) {
		backpackView.handleDoubleClick(x, y);
	}

	/**
	 * Handles key press events. Pressing 'i' or 'I' opens the item info screen for
	 * the selected item.
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
			Item selected = getItemAtPosition(x, y);

			if (selected != null) {
				openItemInfoScreen(selected, manager);
			}
		}
	}

	/**
	 * Gets the item at the specified position, checking backpack first then shop
	 * items.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return the item at the position, or null if none found
	 */
	private Item getItemAtPosition(int x, int y) {
		Item selected = backpackView.getSelectedItem(x, y);

		if (selected == null) {
			for (ShopItem shopItem : shopItems) {
				if (!shopItem.isSold() && shopItem.contains(x, y)) {
					return shopItem.getItem();
				}
			}
		}

		return selected;
	}

	/**
	 * Opens the item info screen for the specified item.
	 *
	 * @param item    the item to display information about
	 * @param manager the screen manager
	 */
	private void openItemInfoScreen(Item item, ScreenManager manager) {
		int currentIndex = manager.getCurrentScreenIndex();
		manager.addScreen(new ItemInfoScreen(item, buttonImage, currentIndex));
		manager.goToScreen(manager.getScreenCount() - 1);
	}
}