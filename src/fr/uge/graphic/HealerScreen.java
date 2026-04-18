package fr.uge.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.Hero;
import fr.uge.backpackhero.GameData;
import fr.uge.backpackhero.Gold;

/**
 * Represents the healer screen where the player can purchase healing services.
 * Available services include healing HP, increasing maximum HP, and removing
 * curses.
 */
public class HealerScreen implements Screen {
	private final GameData gameData;
	private final BufferedImage healerImage;
	private final List<Button> buttons = new ArrayList<>();
	private static final Color DARK_GREEN = new Color(30, 80, 50);
	private static final Color LIGHT_GREEN = new Color(100, 200, 150);

	private static final int HEAL_PRICE = 10;
	private static final int HEAL_AMOUNT = 20;
	private static final int MAX_HP_PRICE = 25;
	private static final int MAX_HP_INCREASE = 10;
	private static final int REMOVE_CURSE_PRICE = 30;

	private String errorMessage = null;
	private long errorMessageTime = 0;
	private static final int ERROR_MESSAGE_DURATION_MS = 2000;

	/**
	 * Constructs a new HealerScreen.
	 *
	 * @param gameData    the game data containing hero information
	 * @param healerImage the image representing the healer (can be null)
	 * @throws NullPointerException if gameData is null
	 */
	public HealerScreen(GameData gameData, BufferedImage healerImage) {
		this.gameData = Objects.requireNonNull(gameData, "gameData cannot be null");
		this.healerImage = healerImage;
	}

	/**
	 * Adds a button to the screen.
	 *
	 * @param button the button to add
	 * @throws NullPointerException if button is null
	 */
	public void addButton(Button button) {
		Objects.requireNonNull(button, "button cannot be null");
		buttons.add(button);
	}

	/**
	 * Creates and adds all healer buttons to the screen in a 2x2 grid layout. The
	 * buttons are positioned based on screen dimensions.
	 *
	 * @param buttonImage  the background image for buttons (can be null)
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 * @throws IllegalArgumentException if screenWidth or screenHeight is negative
	 */
	public void addHealButtons(BufferedImage buttonImage, int screenWidth, int screenHeight) {
		if (screenWidth < 0) {
			throw new IllegalArgumentException("screenWidth cannot be negative");
		}
		if (screenHeight < 0) {
			throw new IllegalArgumentException("screenHeight cannot be negative");
		}

		int btnWidth = Math.max(200, screenWidth / 5);
		int btnHeight = Math.max(50, screenHeight / 12);
		int centerX = screenWidth / 2;

		int gapX = Math.max(20, screenWidth / 50);
		int gapY = Math.max(80, screenHeight / 8);

		int row1Y = (screenHeight / 2) + Math.max(40, screenHeight / 12);
		int leftX = centerX - btnWidth - gapX / 2;
		int rightX = centerX + gapX / 2;

		addButton(new Button(leftX, row1Y, btnWidth, btnHeight, buttonImage));
		addButton(new Button(rightX, row1Y, btnWidth, btnHeight, buttonImage));

		int row2Y = row1Y + btnHeight + gapY;

		addButton(new Button(leftX, row2Y, btnWidth, btnHeight, buttonImage));
		addButton(new Button(rightX, row2Y, btnWidth, btnHeight, buttonImage));
	}

	/**
	 * Draws the complete healer screen including background, title, healer image,
	 * hero information, buttons, and any error messages.
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
		int currentGold = getCurrentGold(hero);

		drawBackground(graphics, width, height);
		drawTitle(graphics, width, height);
		drawHealerImage(graphics, width, height);
		drawHeroInfo(graphics, width, height, hero, currentGold);
		drawButtons(graphics);
		drawButtonLabels(graphics, width, height, hero, currentGold);
		drawErrorMessage(graphics, width, height);
	}

	/**
	 * Gets the current amount of gold the hero has.
	 *
	 * @param hero the hero
	 * @return the amount of gold, or 0 if no gold item exists
	 */
	private int getCurrentGold(Hero hero) {
		Gold gold = hero.backpack().getGold();
		return (gold != null) ? gold.purse() : 0;
	}

	/**
	 * Displays a temporary error message at the center of the screen. The message
	 * automatically disappears after the duration expires.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawErrorMessage(Graphics2D graphics, int width, int height) {
		long currentTime = System.currentTimeMillis();
		if (errorMessage != null && (currentTime - errorMessageTime) < ERROR_MESSAGE_DURATION_MS) {
			drawErrorMessageBox(graphics, width, height);
			drawErrorMessageText(graphics, width, height);
		} else if (errorMessage != null) {
			errorMessage = null;
		}
	}

	/**
	 * Draws the semi-transparent background box for the error message.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawErrorMessageBox(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(0, 0, 0, 180));
		int msgWidth = Math.max(300, width / 3);
		int msgHeight = Math.max(60, height / 12);
		int msgX = (width - msgWidth) / 2;
		int msgY = (height - msgHeight) / 2;
		graphics.fillRoundRect(msgX, msgY, msgWidth, msgHeight, 15, 15);
	}

	/**
	 * Draws the error message text centered in the error box.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawErrorMessageText(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(255, 100, 100));
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(18, height / 35)));

		int msgHeight = Math.max(60, height / 12);
		int msgY = (height - msgHeight) / 2;

		int textWidth = graphics.getFontMetrics().stringWidth(errorMessage);
		graphics.drawString(errorMessage, (width - textWidth) / 2,
				msgY + (msgHeight + graphics.getFontMetrics().getAscent()) / 2);
	}

	/**
	 * Draws the background of the healer screen in dark green.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(DARK_GREEN);
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Draws the title "HEALER" at the top of the screen.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawTitle(Graphics2D graphics, int width, int height) {
		graphics.setColor(LIGHT_GREEN);
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(32, height / 18)));
		String title = "HEALER";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, Math.max(65, height / 10));
	}

	/**
	 * Draws the healer image if available.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 */
	private void drawHealerImage(Graphics2D graphics, int width, int height) {
		if (healerImage != null) {
			int imgWidth = Math.max(160, Math.min(width, height) / 6);
			int imgHeight = imgWidth;
			int imgX = (width / 2) - (imgWidth / 2);
			int imgY = Math.max(100, height / 8);
			graphics.drawImage(healerImage, imgX, imgY, imgWidth, imgHeight, null);
		}
	}

	/**
	 * Draws the hero's current HP and gold information.
	 *
	 * @param graphics    the graphics context
	 * @param width       the width of the screen
	 * @param height      the height of the screen
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 */
	private void drawHeroInfo(Graphics2D graphics, int width, int height, Hero hero, int currentGold) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(18, height / 35)));
		int infoY = Math.max(290, height / 2 - 60);

		drawHpInfo(graphics, width, hero, infoY);
		drawGoldInfo(graphics, width, height, currentGold, infoY);
	}

	/**
	 * Draws the HP information showing current and maximum health.
	 *
	 * @param graphics the graphics context
	 * @param width    the width of the screen
	 * @param hero     the hero
	 * @param infoY    the Y coordinate for the information
	 */
	private void drawHpInfo(Graphics2D graphics, int width, Hero hero, int infoY) {
		graphics.setColor(new Color(255, 100, 100));
		String hpInfo = "HP: " + hero.hp() + " / " + hero.maxhp();
		int hpWidth = graphics.getFontMetrics().stringWidth(hpInfo);
		graphics.drawString(hpInfo, (width - hpWidth) / 2, infoY);
	}

	/**
	 * Draws the gold information showing the current gold amount.
	 *
	 * @param graphics    the graphics context
	 * @param width       the width of the screen
	 * @param height      the height of the screen
	 * @param currentGold the hero's current gold amount
	 * @param infoY       the Y coordinate for the information
	 */
	private void drawGoldInfo(Graphics2D graphics, int width, int height, int currentGold, int infoY) {
		graphics.setColor(new Color(255, 215, 0));
		String goldInfo = "Gold: " + currentGold;
		int goldWidth = graphics.getFontMetrics().stringWidth(goldInfo);
		graphics.drawString(goldInfo, (width - goldWidth) / 2, infoY + Math.max(30, height / 22));
	}

	/**
	 * Draws all buttons on the screen.
	 *
	 * @param graphics the graphics context
	 */
	private void drawButtons(Graphics2D graphics) {
		for (var button : buttons) {
			button.draw(graphics);
		}
	}

	/**
	 * Draws labels and information for all buttons.
	 *
	 * @param graphics    the graphics context
	 * @param width       the width of the screen
	 * @param height      the height of the screen
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 */
	private void drawButtonLabels(Graphics2D graphics, int width, int height, Hero hero, int currentGold) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, height / 40)));

		for (int i = 0; i < buttons.size(); i++) {
			Button btn = buttons.get(i);
			drawSingleButtonLabel(graphics, width, height, btn, i, hero, currentGold);
		}
	}

	/**
	 * Draws the label, description, and price for a single button.
	 *
	 * @param graphics    the graphics context
	 * @param width       the width of the screen
	 * @param height      the height of the screen
	 * @param btn         the button to draw the label for
	 * @param buttonIndex the index of the button
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 */
	private void drawSingleButtonLabel(Graphics2D graphics, int width, int height, Button btn, int buttonIndex,
			Hero hero, int currentGold) {
		ButtonInfo info = getButtonInfo(buttonIndex, hero, currentGold);

		drawButtonText(graphics, height, btn, info.getLabel(), info.getLabelColor());
		if (!info.getDescription().isEmpty()) {
			drawButtonDescription(graphics, width, height, btn, info.getDescription(), info.getLabelColor());
		}
		if (!info.getPriceStr().isEmpty()) {
			drawButtonPrice(graphics, width, height, btn, info.getPriceStr(), info.getLabelColor());
		}
	}

	/**
	 * Gets the display information for a button based on its index.
	 *
	 * @param buttonIndex the index of the button
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 * @return the button information
	 */
	private ButtonInfo getButtonInfo(int buttonIndex, Hero hero, int currentGold) {
		return switch (buttonIndex) {
		case 0 -> createHealButtonInfo(hero, currentGold);
		case 1 -> createMaxHpButtonInfo(currentGold);
		case 2 -> createRemoveCurseButtonInfo(hero, currentGold);
		default -> new ButtonInfo("QUIT", "", "", Color.WHITE);
		};
	}

	/**
	 * Creates the button information for the heal button.
	 *
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 * @return the button information
	 */
	private ButtonInfo createHealButtonInfo(Hero hero, int currentGold) {
		String label = "HEAL";
		String description = "+" + HEAL_AMOUNT + " HP";
		String priceStr = HEAL_PRICE + " Gold";
		Color labelColor = Color.WHITE;

		if (hero.hp() >= hero.maxhp()) {
			labelColor = Color.GRAY;
		} else if (currentGold < HEAL_PRICE) {
			labelColor = Color.RED;
		}

		return new ButtonInfo(label, description, priceStr, labelColor);
	}

	/**
	 * Creates the button information for the max HP increase button.
	 *
	 * @param currentGold the hero's current gold amount
	 * @return the button information
	 */
	private ButtonInfo createMaxHpButtonInfo(int currentGold) {
		String label = "MAX HP";
		String description = "+" + MAX_HP_INCREASE + " Maximum HP";
		String priceStr = MAX_HP_PRICE + " Gold";
		Color labelColor = (currentGold < MAX_HP_PRICE) ? Color.RED : Color.WHITE;

		return new ButtonInfo(label, description, priceStr, labelColor);
	}

	/**
	 * Creates the button information for the remove curse button.
	 *
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 * @return the button information
	 */
	private ButtonInfo createRemoveCurseButtonInfo(Hero hero, int currentGold) {
		String label = "PURIFY";
		int curseCount = gameData.countCurses();
		String description = curseCount + " curse" + (curseCount > 1 ? "s" : "");
		String priceStr = REMOVE_CURSE_PRICE + " Gold";
		Color labelColor = Color.WHITE;

		if (curseCount == 0) {
			labelColor = Color.GRAY;
		} else if (currentGold < REMOVE_CURSE_PRICE) {
			labelColor = Color.RED;
		}

		return new ButtonInfo(label, description, priceStr, labelColor);
	}

	/**
	 * Draws the main text label on a button.
	 *
	 * @param graphics   the graphics context
	 * @param height     the height of the screen
	 * @param btn        the button
	 * @param label      the label text
	 * @param labelColor the color of the label
	 */
	private void drawButtonText(Graphics2D graphics, int height, Button btn, String label, Color labelColor) {
		graphics.setColor(labelColor);

		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(14, height / 45)));
		int labelWidth = graphics.getFontMetrics().stringWidth(label);
		int labelX = btn.getX() + (btn.getWidth() - labelWidth) / 2;
		int labelY = btn.getY() + (btn.getHeight() + graphics.getFontMetrics().getAscent()) / 2;
		graphics.drawString(label, labelX, labelY);
	}

	/**
	 * Draws the description text below a button.
	 *
	 * @param graphics    the graphics context
	 * @param width       the width of the screen
	 * @param height      the height of the screen
	 * @param btn         the button
	 * @param description the description text
	 * @param labelColor  the color of the label (unused but kept for consistency)
	 */
	private void drawButtonDescription(Graphics2D graphics, int width, int height, Button btn, String description,
			Color labelColor) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(12, height / 55)));
		graphics.setColor(Color.LIGHT_GRAY);
		int descWidth = graphics.getFontMetrics().stringWidth(description);
		int descX = btn.getX() + (btn.getWidth() - descWidth) / 2;
		int descY = btn.getY() + btn.getHeight() + Math.max(15, height / 45);
		graphics.drawString(description, descX, descY);
	}

	/**
	 * Draws the price text below the button description.
	 *
	 * @param graphics   the graphics context
	 * @param width      the width of the screen
	 * @param height     the height of the screen
	 * @param btn        the button
	 * @param priceStr   the price string
	 * @param labelColor the color of the label (unused but kept for consistency)
	 */
	private void drawButtonPrice(Graphics2D graphics, int width, int height, Button btn, String priceStr,
			Color labelColor) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(13, height / 50)));
		graphics.setColor(new Color(255, 215, 0));
		int priceWidth = graphics.getFontMetrics().stringWidth(priceStr);
		int priceX = btn.getX() + (btn.getWidth() - priceWidth) / 2;
		int priceY = btn.getY() + btn.getHeight() + Math.max(38, height / 22);
		graphics.drawString(priceStr, priceX, priceY);
	}

	/**
	 * Handles mouse click events on the healer screen. Processes clicks on buttons
	 * to perform corresponding actions.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		Hero hero = gameData.hero();
		int currentGold = getCurrentGold(hero);

		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).contains(x, y)) {
				handleButtonAction(i, hero, currentGold, manager);
				break;
			}
		}
	}

	/**
	 * Handles the action associated with a specific button.
	 *
	 * @param buttonIndex the index of the button clicked
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 * @param manager     the screen manager
	 */
	private void handleButtonAction(int buttonIndex, Hero hero, int currentGold, ScreenManager manager) {
		switch (buttonIndex) {
		case 0 -> handleHealButton(hero, currentGold);
		case 1 -> handleMaxHpButton(currentGold);
		case 2 -> handleRemoveCurseButton(hero, currentGold);
		case 3 -> handleQuitButton(manager);
		}
	}

	/**
	 * Handles the heal button action. Heals the hero if they have enough gold and
	 * are not at full HP.
	 *
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 */
	private void handleHealButton(Hero hero, int currentGold) {
		if (hero.hp() >= hero.maxhp()) {
			showErrorMessage("HP already at maximum");
		} else if (currentGold < HEAL_PRICE) {
			showErrorMessage("Not enough gold");
		} else {
			gameData.buyHeal(HEAL_PRICE, HEAL_AMOUNT);
		}
	}

	/**
	 * Handles the max HP increase button action. Increases the hero's maximum HP if
	 * they have enough gold.
	 *
	 * @param currentGold the hero's current gold amount
	 */
	private void handleMaxHpButton(int currentGold) {
		if (currentGold < MAX_HP_PRICE) {
			showErrorMessage("Not enough gold");
		} else {
			gameData.addMaxHp(MAX_HP_PRICE, MAX_HP_INCREASE);
		}
	}

	/**
	 * Handles the remove curse button action. Removes one curse from the hero's
	 * backpack if they have enough gold and curses exist.
	 *
	 * @param hero        the hero
	 * @param currentGold the hero's current gold amount
	 */
	private void handleRemoveCurseButton(Hero hero, int currentGold) {
		if (!gameData.removeCurse(REMOVE_CURSE_PRICE)) {
			int curseCount = gameData.countCurses();
			if (curseCount == 0) {
				showErrorMessage("No curse to remove");
			} else {
				showErrorMessage("Not enough gold");
			}
		}
	}

	/**
	 * Displays a temporary error message.
	 *
	 * @param message the error message to display
	 * @throws NullPointerException if message is null
	 */
	private void showErrorMessage(String message) {
		Objects.requireNonNull(message, "message cannot be null");
		this.errorMessage = message;
		this.errorMessageTime = System.currentTimeMillis();
	}

	/**
	 * Handles the quit button action. Returns to the map screen without converting
	 * the healer room to a corridor.
	 *
	 * @param manager the screen manager
	 */
	private void handleQuitButton(ScreenManager manager) {
		manager.goToScreen(1);
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