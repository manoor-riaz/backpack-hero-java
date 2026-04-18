package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.*;

/**
 * Screen displaying rewards after completing a room. Players can drag items
 * into their backpack and unlock new cells.
 */
public class RewardScreen implements Screen {
	private final Hero hero;
	private final GameData gameData;
	private final BackpackView backpackView;
	private final BufferedImage buttonImage;
	private Button continueButton;

	private static final int BACKPACK_ROWS = 3;
	private static final int BACKPACK_COLS = 5;
	private static final int BACKPACK_CELL_SIZE = 70;
	private static final int REWARD_START_X = 100;
	private static final int REWARD_START_Y = 200;
	private static final int REWARD_SPACING = 110;
	private static final int REWARD_COLUMNS = 3;

	/**
	 * Constructs a new RewardScreen.
	 *
	 * @param gameData    the game data containing hero information
	 * @param rewards     the list of reward items to display
	 * @param buttonImage the background image for buttons (can be null)
	 * @throws NullPointerException if gameData or rewards is null
	 */
	public RewardScreen(GameData gameData, List<Item> rewards, BufferedImage buttonImage) {
		Objects.requireNonNull(gameData, "gameData cannot be null");
		Objects.requireNonNull(rewards, "rewards cannot be null");

		this.hero = gameData.hero();
		this.gameData = gameData;
		this.buttonImage = buttonImage;

		this.backpackView = new BackpackView(hero.backpack(), BACKPACK_ROWS, BACKPACK_COLS, BACKPACK_CELL_SIZE);
		ItemImageManager.configureBackpackView(backpackView);
		backpackView.setShowLockedCells(true);

		stageRewards(rewards);
	}

	/**
	 * Stages all reward items in the staging area for the player to organize.
	 *
	 * @param rewards the list of reward items
	 */
	private void stageRewards(List<Item> rewards) {
		for (int i = 0; i < rewards.size(); i++) {
			int itemX = REWARD_START_X + (i % REWARD_COLUMNS) * REWARD_SPACING;
			int itemY = REWARD_START_Y + (i / REWARD_COLUMNS) * REWARD_SPACING;
			backpackView.addToStaging(rewards.get(i), itemX, itemY);
		}
	}

	/**
	 * Draws the complete reward screen including title, instructions, backpack, and
	 * continue button.
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
		drawSubtitle(graphics, width, height);
		drawUnlockMessage(graphics, width, height);
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
		graphics.setColor(new Color(30, 30, 30));
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
		String title = "NEW REWARDS!";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, Math.max(50, height / 12));
	}

	/**
	 * Draws the subtitle instructions.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawSubtitle(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(13, height / 50)));
		graphics.setColor(Color.WHITE);
		String subtitle = "Drag items into your bag or leave them";
		int subtitleWidth = graphics.getFontMetrics().stringWidth(subtitle);
		graphics.drawString(subtitle, (width - subtitleWidth) / 2, Math.max(85, height / 8));
	}

	/**
	 * Draws the unlock message if there are cells to unlock.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawUnlockMessage(Graphics2D graphics, int width, int height) {
		int pendingCells = hero.backpack().getNbCells();
		if (pendingCells > 0) {
			graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, height / 35)));
			graphics.setColor(new Color(255, 215, 0));
			String unlockMsg = createUnlockMessage(pendingCells);
			int msgWidth = graphics.getFontMetrics().stringWidth(unlockMsg);
			graphics.drawString(unlockMsg, (width - msgWidth) / 2, Math.max(115, height / 6));
		}
	}

	/**
	 * Creates the unlock message text based on the number of pending cells.
	 *
	 * @param pendingCells the number of cells to unlock
	 * @return the formatted message
	 */
	private String createUnlockMessage(int pendingCells) {
		String plural = (pendingCells > 1) ? "s" : "";
		return "Click on golden cells to expand your bag! (" + pendingCells + " remaining" + plural + ")";
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
	 * Draws the continue button with appropriate styling based on unlock status.
	 *
	 * @param graphics the graphics context
	 * @param height   the height of the screen
	 */
	private void drawContinueButton(Graphics2D graphics, int height) {
		if (continueButton == null) {
			return;
		}

		int pendingCells = hero.backpack().getNbCells();

		continueButton.draw(graphics);

		if (pendingCells > 0) {
			drawDisabledButtonOverlay(graphics);
		}

		drawContinueButtonLabel(graphics, height, pendingCells > 0);
	}

	/**
	 * Draws a semi-transparent overlay on the disabled button.
	 *
	 * @param graphics the graphics context
	 */
	private void drawDisabledButtonOverlay(Graphics2D graphics) {
		graphics.setColor(new Color(0, 0, 0, 150));
		graphics.fillRect(continueButton.getX(), continueButton.getY(), continueButton.getWidth(),
				continueButton.getHeight());
	}

	/**
	 * Draws the label on the continue button.
	 *
	 * @param graphics   the graphics context
	 * @param height     the height of the screen
	 * @param isDisabled whether the button is disabled
	 */
	private void drawContinueButtonLabel(Graphics2D graphics, int height, boolean isDisabled) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, height / 40)));
		graphics.setColor(isDisabled ? Color.GRAY : Color.WHITE);

		String btnText = "CONTINUE";
		int textWidth = graphics.getFontMetrics().stringWidth(btnText);
		int textX = continueButton.getX() + (continueButton.getWidth() - textWidth) / 2;
		int textY = continueButton.getY() + (continueButton.getHeight() + graphics.getFontMetrics().getAscent()) / 2;
		graphics.drawString(btnText, textX, textY);
	}

	/**
	 * Handles mouse click events on the reward screen. Processes cell unlocking,
	 * continue button, and item dragging.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (backpackView.tryUnlockCell(x, y)) {
			return;
		}

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
			if (hero.backpack().getNbCells() > 0) {
				return true;
			}
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
	 * Handles mouse release events. Ends dragging and merges gold if necessary.
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
			handleDragEnd(x, y);
		}
	}

	/**
	 * Handles the end of a drag operation. Merges gold items if a gold item was
	 * placed in the backpack.
	 *
	 * @param x the X coordinate of the release
	 * @param y the Y coordinate of the release
	 */
	private void handleDragEnd(int x, int y) {
		Item draggedItem = backpackView.getDraggedItem();
		boolean wasInBackpack = hero.backpack().contains(draggedItem);

		backpackView.endDrag(x, y);

		if (shouldMergeGold(draggedItem, wasInBackpack)) {
			mergeGoldItem((Gold) draggedItem, wasInBackpack);
		}
	}

	/**
	 * Checks if a dragged item should trigger gold merging.
	 *
	 * @param draggedItem   the item that was dragged
	 * @param wasInBackpack whether the item was in the backpack before dragging
	 * @return true if gold merging should occur
	 */
	private boolean shouldMergeGold(Item draggedItem, boolean wasInBackpack) {
		return draggedItem != null && draggedItem.isGold() && draggedItem.isGold();
	}

	/**
	 * Merges a gold item with existing gold in the backpack.
	 *
	 * @param newGold       the new gold item
	 * @param wasInBackpack whether the gold was in the backpack before dragging
	 */
	private void mergeGoldItem(Gold newGold, boolean wasInBackpack) {
		boolean isNowInBackpack = hero.backpack().contains(newGold);

		if (isNowInBackpack && !wasInBackpack) {
			Gold existingGold = hero.backpack().getGold();

			if (existingGold != null && existingGold != newGold) {
				hero.backpack().remove(newGold);
				existingGold.add(newGold.purse());
			}
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