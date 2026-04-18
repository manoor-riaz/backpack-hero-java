package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.*;
import fr.uge.backpackhero.Dimension;
import fr.uge.backpackhero.Shape;

/**
 * Starting equipment screen where the hero receives their initial gear. The
 * hero starts with a meal, wooden sword, and wooden shield.
 */
public class StartingGearScreen implements Screen {
	private final GameData gameData;
	private final BackpackView backpackView;
	private final BufferedImage buttonImage;
	private Button startButton;

	private static final int BACKPACK_ROWS = 8;
	private static final int BACKPACK_COLS = 6;
	private static final int BACKPACK_CELL_SIZE = 70;
	private static final int ITEM_START_X = 100;
	private static final int ITEM_START_Y = 200;
	private static final int ITEM_SPACING = 140;
	private static final int MEAL_HEALING = 15;
	private static final int SWORD_DAMAGE = 10;
	private static final int SHIELD_DEFENSE = 5;

	/**
	 * Constructs a new StartingGearScreen.
	 *
	 * @param gameData    the game data containing hero information
	 * @param buttonImage the background image for buttons (can be null)
	 * @throws NullPointerException if gameData is null
	 */
	public StartingGearScreen(GameData gameData, BufferedImage buttonImage) {
		this.gameData = Objects.requireNonNull(gameData, "gameData cannot be null");
		this.buttonImage = buttonImage;

		BackPack backpack = gameData.hero().backpack();
		this.backpackView = new BackpackView(backpack, BACKPACK_ROWS, BACKPACK_COLS, BACKPACK_CELL_SIZE);
		ItemImageManager.configureBackpackView(backpackView);
		backpackView.setShowLockedCells(false);

		generateStartingGear();
	}

	/**
	 * Generates and stages the starting equipment for the hero. Creates a meal,
	 * wooden sword, and wooden shield.
	 */
	private void generateStartingGear() {
		var squareShape = new Shape(List.of(new Position(0, 0), new Position(0, 1)));
		var swordShape = Shape.rectangle(new Dimension(3, 1));
		var shieldShape = Shape.rectangle(new Dimension(2, 2));

		Consumables meal = createMeal(squareShape);
		backpackView.addToStaging(meal, ITEM_START_X, ITEM_START_Y);

		Weapon sword = createSword(swordShape);
		backpackView.addToStaging(sword, ITEM_START_X, ITEM_START_Y + ITEM_SPACING);

		Armor shield = createShield(shieldShape);
		backpackView.addToStaging(shield, ITEM_START_X, ITEM_START_Y + ITEM_SPACING * 2 + 50);
	}

	/**
	 * Creates the starting meal item.
	 *
	 * @param shape the shape of the meal
	 * @return the meal consumable
	 */
	private Consumables createMeal(Shape shape) {
		return new Consumables("Repas", Rarity.COMMON, shape, 0, new Stats(0, 0, MEAL_HEALING), 0);
	}

	/**
	 * Creates the starting wooden sword.
	 *
	 * @param shape the shape of the sword
	 * @return the wooden sword weapon
	 */
	private Weapon createSword(Shape shape) {
		return new Weapon("Wooden Sword", Rarity.COMMON, shape, 1, new Stats(SWORD_DAMAGE, 0, 0), 0);
	}

	/**
	 * Creates the starting wooden shield.
	 *
	 * @param shape the shape of the shield
	 * @return the wooden shield armor
	 */
	private Armor createShield(Shape shape) {
		return new Armor("Bouclier en Bois", Rarity.COMMON, shape, 1, new Stats(0, SHIELD_DEFENSE, 0), 0);
	}

	/**
	 * Draws the complete starting gear screen including title, instructions, item
	 * legends, backpack, and start button.
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
		drawTitle(graphics, width);
		drawInstructions(graphics, width);
		drawItemLegends(graphics);
		drawBackpack(graphics, width, height);
		drawStartButton(graphics, width, height);
	}

	/**
	 * Draws the background.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(30, 30, 50));
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Draws the title.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 */
	private void drawTitle(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.BOLD, 32));
		graphics.setColor(new Color(255, 215, 0));
		String title = "JOURNEY PREPARATION";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, 60);
	}

	/**
	 * Draws the instructions.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 */
	private void drawInstructions(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.PLAIN, 18));
		graphics.setColor(Color.WHITE);
		String instruction = "Place your starting equipment in your bag";
		int instrWidth = graphics.getFontMetrics().stringWidth(instruction);
		graphics.drawString(instruction, (width - instrWidth) / 2, 100);
	}

	/**
	 * Draws the item descriptions on the left side.
	 *
	 * @param graphics the graphics context
	 */
	private void drawItemLegends(Graphics2D graphics) {
		graphics.setFont(new Font("Arial", Font.BOLD, 16));
		int legendX = 30;
		int legendY = 180;

		graphics.setColor(new Color(255, 100, 100));
		graphics.drawString("Repas: restores 15 HP (2x1)", legendX, legendY);

		graphics.setColor(new Color(200, 200, 255));
		graphics.drawString("Wooden Sword: 10 damage (3x1)", legendX, legendY + 140);

		graphics.setColor(new Color(150, 200, 255));
		graphics.drawString("Bouclier en Bois: 5 shield (2x2)", legendX, legendY + 330);
	}

	/**
	 * Positions and draws the backpack view.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackpack(Graphics2D graphics, int width, int height) {
		int backpackX = width - backpackView.getWidth() - 50;
		int backpackY = (height - backpackView.getHeight()) / 2;
		backpackView.setPosition(backpackX, backpackY);
		backpackView.draw(graphics, gameData);
	}

	/**
	 * Draws the start button with its label.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawStartButton(Graphics2D graphics, int width, int height) {
		createStartButtonIfNeeded(width, height);

		startButton.draw(graphics);
		drawStartButtonLabel(graphics);
	}

	/**
	 * Creates the start button if it doesn't exist yet.
	 *
	 * @param width  the width of the screen
	 * @param height the height of the screen
	 */
	private void createStartButtonIfNeeded(int width, int height) {
		if (startButton == null) {
			int btnWidth = 250;
			int btnHeight = 60;
			int btnX = (width - btnWidth) / 2;
			int btnY = height - 100;
			startButton = new Button(btnX, btnY, btnWidth, btnHeight, buttonImage);
		}
	}

	/**
	 * Draws the label on the start button.
	 *
	 * @param graphics the graphics context
	 */
	private void drawStartButtonLabel(Graphics2D graphics) {
		graphics.setFont(new Font("Arial", Font.BOLD, 18));
		graphics.setColor(Color.WHITE);
		String btnText = "START ADVENTURE";
		int btnTextWidth = graphics.getFontMetrics().stringWidth(btnText);
		int textX = startButton.getX() + (startButton.getWidth() - btnTextWidth) / 2;
		int textY = startButton.getY() + (startButton.getHeight() + graphics.getFontMetrics().getAscent()) / 2;
		graphics.drawString(btnText, textX, textY);
	}

	/**
	 * Handles mouse click events on the starting gear screen. Processes start
	 * button clicks and item dragging.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (handleStartButtonClick(x, y, manager)) {
			return;
		}

		backpackView.startDrag(x, y);
	}

	/**
	 * Handles clicks on the start button.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @return true if the button was clicked
	 */
	private boolean handleStartButtonClick(int x, int y, ScreenManager manager) {
		if (startButton != null && startButton.contains(x, y)) {
			mergeRemainingItems();
			manager.removeScreen(manager.getCurrentScreenIndex());
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
	 * Merges remaining staged items into the backpack. Attempts to automatically
	 * place all staged items.
	 */
	private void mergeRemainingItems() {
		var stagedItemsCopy = new java.util.ArrayList<>(backpackView.getStagedItems());
		for (var staged : stagedItemsCopy) {
			gameData.hero().backpack().freeAdd(staged.getItem());
			backpackView.removeFromStaging(staged.getItem());
		}
	}
}