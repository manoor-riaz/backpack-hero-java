package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import fr.uge.backpackhero.*;

/**
 * Screen displaying a discovered treasure chest with random items. Players can
 * organize the treasure items into their backpack.
 */
public class TreasureScreen implements Screen {
	private final GameData gameData;
	private final BackpackView backpackView;
	private final BufferedImage buttonImage;
	private Button continueButton;

	private static final int BACKPACK_ROWS = 8;
	private static final int BACKPACK_COLS = 6;
	private static final int BACKPACK_CELL_SIZE = 60;
	private static final int ITEM_START_X = 100;
	private static final int ITEM_START_Y = 150;
	private static final int ITEM_SPACING = 100;
	private static final int ITEM_COLUMNS = 3;
	private static final int GOLD_OFFSET_Y = 250;

	/**
	 * Constructs a new TreasureScreen.
	 *
	 * @param gameData          the game data containing hero information
	 * @param buttonImageLoader the image loader for the button
	 * @throws NullPointerException if gameData or buttonImageLoader is null
	 */
	public TreasureScreen(GameData gameData, ImageLoader buttonImageLoader) {
		this.gameData = Objects.requireNonNull(gameData, "gameData cannot be null");
		Objects.requireNonNull(buttonImageLoader, "buttonImageLoader cannot be null");
		this.buttonImage = buttonImageLoader.getImage();

		BackPack backpack = gameData.hero().backpack();
		this.backpackView = new BackpackView(backpack, BACKPACK_ROWS, BACKPACK_COLS, BACKPACK_CELL_SIZE);
		ItemImageManager.configureBackpackView(backpackView);
		backpackView.setShowLockedCells(false);

		generateAndStageItems();
	}

	/**
	 * Generates treasure loot from GameData and stages it for the player.
	 */
	private void generateAndStageItems() {
		var lootItems = gameData.generateTreasureLoot();

		for (int i = 0; i < lootItems.size(); i++) {
			var item = lootItems.get(i);

			switch (item) {
			case Gold _ -> backpackView.addToStaging(item, ITEM_START_X, ITEM_START_Y + GOLD_OFFSET_Y);
			default -> {
				int itemX = ITEM_START_X + (i % ITEM_COLUMNS) * ITEM_SPACING;
				int itemY = ITEM_START_Y + (i / ITEM_COLUMNS) * ITEM_SPACING;
				backpackView.addToStaging(item, itemX, itemY);
			}
			}
		}
	}

	/**
	 * Draws the complete treasure screen including title, backpack, and continue
	 * button.
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

		createContinueButtonIfNeeded(width, height);

		drawBackground(graphics, width, height);
		drawTitle(graphics, width, height);
		drawBackpack(graphics, width, height);
		drawContinueButton(graphics, height);
	}

	/**
	 * Creates the continue button if it doesn't exist yet.
	 *
	 * @param width  the width of the screen
	 * @param height the height of the screen
	 */
	private void createContinueButtonIfNeeded(int width, int height) {
		if (continueButton == null) {
			int btnWidth = Math.max(160, width / 6);
			int btnHeight = Math.max(50, height / 12);
			int btnX = (width - btnWidth) / 2;
			int btnY = height - Math.max(80, height / 8);
			continueButton = new Button(btnX, btnY, btnWidth, btnHeight, buttonImage);
		}
	}

	/**
	 * Draws the dark background.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(40, 40, 40));
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Draws the title text.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawTitle(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(24, height / 20)));
		graphics.setColor(new Color(255, 215, 0));
		String title = "TREASURE DISCOVERED!";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, Math.max(50, height / 12));
	}

	/**
	 * Positions and draws the backpack view.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackpack(Graphics2D graphics, int width, int height) {
		int backpackX = width - backpackView.getWidth() - Math.max(40, width / 20);
		int backpackY = (height - backpackView.getHeight()) / 2;
		backpackView.setPosition(backpackX, backpackY);

		backpackView.draw(graphics, gameData);
	}

	/**
	 * Draws the continue button with its label.
	 *
	 * @param graphics the graphics context
	 * @param height   the height of the screen
	 */
	private void drawContinueButton(Graphics2D graphics, int height) {
		if (continueButton == null) {
			return;
		}

		continueButton.draw(graphics);
		drawContinueButtonLabel(graphics, height);
	}

	/**
	 * Draws the label on the continue button.
	 *
	 * @param graphics the graphics context
	 * @param height   the height of the screen
	 */
	private void drawContinueButtonLabel(Graphics2D graphics, int height) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, height / 40)));
		graphics.setColor(Color.WHITE);
		String btnText = "CONTINUE";
		int textWidth = graphics.getFontMetrics().stringWidth(btnText);
		int textX = continueButton.getX() + (continueButton.getWidth() - textWidth) / 2;
		int textY = continueButton.getY() + (continueButton.getHeight() + graphics.getFontMetrics().getAscent()) / 2;
		graphics.drawString(btnText, textX, textY);
	}

	/**
	 * Handles mouse click events on the treasure screen. Processes continue button
	 * clicks and item dragging.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (handleContinueButtonClick(x, y, manager)) {
			return;
		}

		backpackView.startDrag(x, y);
	}

	/**
	 * Handles clicks on the continue button.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @return true if the button was clicked
	 */
	private boolean handleContinueButtonClick(int x, int y, ScreenManager manager) {
		if (continueButton != null && continueButton.contains(x, y)) {
			backpackView.mergeAllStagedGold(gameData::addGoldToBackpack);
			gameData.leaveARoom();
			manager.goToScreen(1);
			return true;
		}
		return false;
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
			Item selected = backpackView.getSelectedItem(x, y);
			if (selected != null) {
				openItemInfoScreen(selected, manager);
			}
		}
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