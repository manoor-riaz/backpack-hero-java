package fr.uge.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uge.backpackhero.*;

/**
 * Screen implementation for displaying the map and hero information. Handles
 * both map view and backpack view, with toggle functionality.
 */
public class MapHeroScreen implements Screen {
	private final BufferedImage heroImage;
	private final GameData gameData;
	private Floor floor;
	private final List<Button> buttons = new ArrayList<>();
	private Position heroPosition;

	private static final Color DARK_GRAY = new Color(50, 50, 50);
	private static final Color GRID_COLOR = new Color(100, 100, 100);
	private static final Color LOCKED_GRID_OVERLAY = new Color(70, 70, 70);

	private final BufferedImage healerImage;
	private final BufferedImage buttonImage;
	private final BufferedImage merchantImage;

	private int gridX, gridY, cellSize;

	private boolean showingBackpack = false;
	private final BackpackView backpackView;
	private Button toggleBackpackButton;
	private long lastClickTime = 0;

	private String errorMessage = null;
	private long errorMessageTime = 0;
	private static final int ERROR_MESSAGE_DURATION_MS = 2000;

	/**
	 * Constructs a new MapHeroScreen.
	 * 
	 * @param heroImage     the hero character image
	 * @param gameData      the current game data
	 * @param floor         the current floor
	 * @param healerImage   the healer room image
	 * @param merchantImage the merchant room image
	 * @param buttonImage   the button background image
	 * @throws NullPointerException if heroImage, gameData, or floor is null
	 */
	public MapHeroScreen(BufferedImage heroImage, GameData gameData, Floor floor, BufferedImage healerImage,
			BufferedImage merchantImage, BufferedImage buttonImage) {
		this.heroImage = Objects.requireNonNull(heroImage);
		this.gameData = Objects.requireNonNull(gameData);
		this.floor = Objects.requireNonNull(floor);
		this.heroPosition = floor.startPos();
		this.healerImage = healerImage;
		this.merchantImage = merchantImage;
		this.buttonImage = buttonImage;

		this.backpackView = new BackpackView(gameData.hero().backpack(), 8, 6, 70);
		ItemImageManager.configureBackpackView(backpackView);
	}

	/**
	 * Adds a button to the screen.
	 * 
	 * @param button the button to add
	 * @throws NullPointerException if button is null
	 */
	public void addButton(Button button) {
		Objects.requireNonNull(button);
		buttons.add(button);
	}

	/**
	 * Determines the color of a room based on its type and state.
	 * 
	 * @param type         the type of room
	 * @param isVisited    whether the room has been visited
	 * @param isAccessible whether the room is accessible
	 * @return the appropriate color for the room
	 * @throws NullPointerException if type is null
	 */
	private Color getRoomColor(TypeRoom type, boolean isVisited, boolean isAccessible) {
		Objects.requireNonNull(type);
		if (isVisited)
			return getDarkenedRoomColor(type);
		if (isAccessible)
			return getBaseRoomColor(type);
		return getVeryDarkRoomColor(type);
	}

	/**
	 * Gets a darkened version of the base room color for visited rooms.
	 * 
	 * @param type the type of room
	 * @return the darkened color
	 */
	private Color getDarkenedRoomColor(TypeRoom type) {
		Color base = getBaseRoomColor(type);
		return new Color(base.getRed() / 2, base.getGreen() / 2, base.getBlue() / 2);
	}

	/**
	 * Gets a very dark version of the base room color for inaccessible rooms.
	 * 
	 * @param type the type of room
	 * @return the very dark color
	 */
	private Color getVeryDarkRoomColor(TypeRoom type) {
		Color base = getBaseRoomColor(type);
		return new Color(base.getRed() / 3, base.getGreen() / 3, base.getBlue() / 3);
	}

	/**
	 * Gets the base color for a room type.
	 * 
	 * @param type the type of room
	 * @return the base color
	 * @throws NullPointerException if type is null
	 */
	private Color getBaseRoomColor(TypeRoom type) {
		Objects.requireNonNull(type);
		return switch (type) {
		case ENEMY -> new Color(200, 50, 50);
		case MERCHANT -> new Color(255, 200, 50);
		case HEALER -> new Color(50, 200, 100);
		case TREASURE -> new Color(255, 215, 0);
		case EXIT -> new Color(100, 100, 255);
		case SURPRISE -> new Color(200, 100, 255);
		case CORRIDOR -> new Color(150, 150, 150);
		};
	}

	/**
	 * Gets the symbol representing a room type.
	 * 
	 * @param type the type of room
	 * @return the symbol string
	 * @throws NullPointerException if type is null
	 */
	private String getRoomSymbol(TypeRoom type) {
		Objects.requireNonNull(type);
		return switch (type) {
		case ENEMY -> "⚔";
		case MERCHANT -> "$";
		case HEALER -> "+";
		case TREASURE -> "💎";
		case EXIT -> "🚪";
		case SURPRISE -> "?";
		case CORRIDOR -> "";
		};
	}

	/**
	 * Draws the screen content.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 * @throws NullPointerException if graphics is null
	 */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics);

		Hero hero = gameData.hero();

		graphics.setColor(DARK_GRAY);
		graphics.fillRect(0, 0, width, height);

		if (!showingBackpack) {
			drawMapView(graphics, width, height, hero);
		} else {
			drawBackpackView(graphics, width, height);
		}

		drawToggleButton(graphics, width, height);
		drawErrorMessage(graphics, width, height);
	}

	/**
	 * Displays a temporary error message at the center of the screen.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
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
	 * Draws the background box for the error message.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
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
	 * Draws the error message text.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawErrorMessageText(Graphics2D graphics, int width, int height) {
		int msgHeight = Math.max(60, height / 12);
		int msgY = (height - msgHeight) / 2;

		graphics.setColor(new Color(255, 100, 100));
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(18, height / 35)));
		int textWidth = graphics.getFontMetrics().stringWidth(errorMessage);
		graphics.drawString(errorMessage, (width - textWidth) / 2,
				msgY + (msgHeight + graphics.getFontMetrics().getAscent()) / 2);
	}

	/**
	 * Draws the toggle backpack button.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawToggleButton(Graphics2D graphics, int width, int height) {
		int btnW = Math.max(160, width / 6);
		int btnH = Math.max(45, height / 12);
		int btnX = width - btnW - Math.max(20, width / 50);
		int btnY = Math.max(15, height / 50);

		toggleBackpackButton = new Button(btnX, btnY, btnW, btnH, buttonImage);
		toggleBackpackButton.draw(graphics);

		drawToggleButtonText(graphics, width, height);
	}

	/**
	 * Draws the text on the toggle button.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawToggleButtonText(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(14, height / 40)));
		graphics.setColor(Color.WHITE);

		String toggleText = showingBackpack ? "CLOSE BACKPACK" : "OPEN BACKPACK";
		int toggleWidth = graphics.getFontMetrics().stringWidth(toggleText);
		graphics.drawString(toggleText,
				toggleBackpackButton.getX() + (toggleBackpackButton.getWidth() - toggleWidth) / 2,
				toggleBackpackButton.getY()
						+ (toggleBackpackButton.getHeight() + graphics.getFontMetrics().getAscent()) / 2);
	}

	/**
	 * Draws the map view with hero panel and map panel.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 * @param hero     the hero character
	 */
	private void drawMapView(Graphics2D graphics, int width, int height, Hero hero) {
		int leftPanelWidth = width / 3;

		drawHeroPanel(graphics, width, height, hero, leftPanelWidth);
		drawMapPanel(graphics, width, height, leftPanelWidth);
	}

	/**
	 * Draws the hero information panel.
	 * 
	 * @param graphics       the graphics context to draw on
	 * @param width          the screen width
	 * @param height         the screen height
	 * @param hero           the hero character
	 * @param leftPanelWidth the width of the left panel
	 */
	private void drawHeroPanel(Graphics2D graphics, int width, int height, Hero hero, int leftPanelWidth) {
		drawHeroImage(graphics, width, height, leftPanelWidth);
		drawHeroName(graphics, width, height, hero, leftPanelWidth);
		drawHeroStats(graphics, width, height, hero);
	}

	/**
	 * Draws the hero image.
	 * 
	 * @param graphics       the graphics context to draw on
	 * @param width          the screen width
	 * @param height         the screen height
	 * @param leftPanelWidth the width of the left panel
	 */
	private void drawHeroImage(Graphics2D graphics, int width, int height, int leftPanelWidth) {
		int heroImageWidth = Math.max(140, width / 6);
		int heroImageHeight = Math.max(190, height / 3);
		int heroImageX = (leftPanelWidth / 2) - (heroImageWidth / 2);
		int heroImageY = height - heroImageHeight - Math.max(120, height / 4);

		graphics.drawImage(heroImage, heroImageX, heroImageY, heroImageWidth, heroImageHeight, null);
	}

	/**
	 * Draws the hero name.
	 * 
	 * @param graphics       the graphics context to draw on
	 * @param width          the screen width
	 * @param height         the screen height
	 * @param hero           the hero character
	 * @param leftPanelWidth the width of the left panel
	 */
	private void drawHeroName(Graphics2D graphics, int width, int height, Hero hero, int leftPanelWidth) {
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(18, height / 25)));

		int heroImageHeight = Math.max(190, height / 3);
		int heroImageY = height - heroImageHeight - Math.max(120, height / 4);

		int nameX = (leftPanelWidth / 2) - (graphics.getFontMetrics().stringWidth(hero.name()) / 2);
		graphics.drawString(hero.name(), nameX, heroImageY - Math.max(15, height / 40));
	}

	/**
	 * Draws the hero statistics.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 * @param hero     the hero character
	 */
	private void drawHeroStats(Graphics2D graphics, int width, int height, Hero hero) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(14, height / 45)));
		int statsY = height - Math.max(110, height / 5);
		int statsX = Math.max(15, width / 80);
		int lineHeight = Math.max(22, height / 25);

		drawHealthStat(graphics, hero, statsX, statsY);
		drawShieldStat(graphics, hero, statsX, statsY, lineHeight);
		drawEnergyStat(graphics, hero, statsX, statsY, lineHeight);
		drawManaStat(graphics, hero, statsX, statsY, lineHeight);
	}

	/**
	 * Draws the health statistic.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param hero     the hero character
	 * @param statsX   the x-coordinate for stats
	 * @param statsY   the y-coordinate for stats
	 */
	private void drawHealthStat(Graphics2D graphics, Hero hero, int statsX, int statsY) {
		graphics.setColor(new Color(255, 100, 100));
		graphics.drawString("PV: " + hero.hp() + "/" + hero.maxhp(), statsX, statsY);
	}

	/**
	 * Draws the shield statistic.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param hero       the hero character
	 * @param statsX     the x-coordinate for stats
	 * @param statsY     the y-coordinate for stats
	 * @param lineHeight the height between lines
	 */
	private void drawShieldStat(Graphics2D graphics, Hero hero, int statsX, int statsY, int lineHeight) {
		graphics.setColor(new Color(100, 200, 255));
		graphics.drawString("Shield: " + hero.shieldpoint(), statsX, statsY + lineHeight);
	}

	/**
	 * Draws the energy statistic.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param hero       the hero character
	 * @param statsX     the x-coordinate for stats
	 * @param statsY     the y-coordinate for stats
	 * @param lineHeight the height between lines
	 */
	private void drawEnergyStat(Graphics2D graphics, Hero hero, int statsX, int statsY, int lineHeight) {
		graphics.setColor(new Color(255, 215, 0));
		graphics.drawString("Energy: " + hero.ep() + "/3", statsX, statsY + lineHeight * 2);
	}

	/**
	 * Draws the mana statistic.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param hero       the hero character
	 * @param statsX     the x-coordinate for stats
	 * @param statsY     the y-coordinate for stats
	 * @param lineHeight the height between lines
	 */
	private void drawManaStat(Graphics2D graphics, Hero hero, int statsX, int statsY, int lineHeight) {
		graphics.setColor(new Color(150, 100, 255));
		graphics.drawString("Mana: " + hero.mp(), statsX, statsY + lineHeight * 3);
	}

	/**
	 * Draws the map panel containing the floor grid.
	 * 
	 * @param graphics       the graphics context to draw on
	 * @param width          the screen width
	 * @param height         the screen height
	 * @param leftPanelWidth the width of the left panel
	 */
	private void drawMapPanel(Graphics2D graphics, int width, int height, int leftPanelWidth) {
		calculateMapDimensions(width, height, leftPanelWidth);
		drawFloorTitle(graphics, height);
		drawRoomGrid(graphics);
		drawMapLegend(graphics, width, height);
	}

	/**
	 * Calculates the dimensions and position of the map grid.
	 * 
	 * @param width          the screen width
	 * @param height         the screen height
	 * @param leftPanelWidth the width of the left panel
	 */
	private void calculateMapDimensions(int width, int height, int leftPanelWidth) {
		int mapX = leftPanelWidth + Math.max(20, width / 25);
		int mapY = Math.max(20, height / 25);
		int mapWidth = width - leftPanelWidth - Math.max(220, width / 5);
		int mapHeight = height - Math.max(80, height / 10);

		int cellWidth = mapWidth / floor.width();
		int cellHeight = mapHeight / floor.height();
		cellSize = Math.min(cellWidth, cellHeight);

		int gridWidth = cellSize * floor.width();
		int gridHeight = cellSize * floor.height();
		gridX = mapX + (mapWidth - gridWidth) / 2;
		gridY = mapY + (mapHeight - gridHeight) / 2;
	}

	/**
	 * Draws the floor title.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param height   the screen height
	 */
	private void drawFloorTitle(Graphics2D graphics, int height) {
		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(16, height / 30)));
		String floorTitle = "Floor " + floor.floorNumber();
		graphics.drawString(floorTitle, gridX, gridY - Math.max(15, height / 40));
	}

	/**
	 * Draws the room grid.
	 * 
	 * @param graphics the graphics context to draw on
	 */
	private void drawRoomGrid(Graphics2D graphics) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(14, cellSize / 5)));

		for (int row = 0; row < floor.height(); row++) {
			for (int col = 0; col < floor.width(); col++) {
				Room room = floor.getRoom(row, col);
				if (room != null) {
					drawSingleRoom(graphics, room, row, col);
				}
			}
		}
	}

	/**
	 * Draws a single room cell in the map grid.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param room     the room to draw
	 * @param row      the row index
	 * @param col      the column index
	 * @throws NullPointerException if graphics or room is null
	 */
	private void drawSingleRoom(Graphics2D graphics, Room room, int row, int col) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(room);

		int x = gridX + col * cellSize;
		int y = gridY + row * cellSize;

		if (heroPosition.row() == row && heroPosition.col() == col) {
			drawHeroRoom(graphics, x, y);
			return;
		}

		boolean isAccessible = floor.isRoomAccessible(room, heroPosition);
		drawRoomBackground(graphics, room, x, y, isAccessible);
		drawRoomBorder(graphics, room, x, y, isAccessible);
		drawRoomSymbol(graphics, room, x, y, isAccessible);
		drawGridIndicator(graphics, room, x, y);
	}

	/**
	 * Draws the hero's current room.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param x        the x-coordinate
	 * @param y        the y-coordinate
	 */
	private void drawHeroRoom(Graphics2D graphics, int x, int y) {
		graphics.setColor(DARK_GRAY);
		graphics.fillRect(x, y, cellSize - 2, cellSize - 2);
		graphics.setColor(Color.YELLOW);
		graphics.drawRect(x, y, cellSize - 2, cellSize - 2);

		drawHeroRoomText(graphics, x, y);
	}

	/**
	 * Draws the "HERO" text in the hero's room.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param x        the x-coordinate
	 * @param y        the y-coordinate
	 */
	private void drawHeroRoomText(Graphics2D graphics, int x, int y) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(12, cellSize / 6)));
		String heroText = "HERO";
		int textWidth = graphics.getFontMetrics().stringWidth(heroText);
		graphics.drawString(heroText, x + (cellSize - textWidth) / 2,
				y + (cellSize + graphics.getFontMetrics().getAscent()) / 2);
	}

	/**
	 * Draws the background color of a room.
	 * 
	 * @param graphics     the graphics context to draw on
	 * @param room         the room
	 * @param x            the x-coordinate
	 * @param y            the y-coordinate
	 * @param isAccessible whether the room is accessible
	 */
	private void drawRoomBackground(Graphics2D graphics, Room room, int x, int y, boolean isAccessible) {
		Color roomColor = getRoomColor(room.getEffectiveType(), room.isVisited(), isAccessible);
		graphics.setColor(roomColor);
		graphics.fillRect(x, y, cellSize - 2, cellSize - 2);
	}

	/**
	 * Draws the border of a room.
	 * 
	 * @param graphics     the graphics context to draw on
	 * @param room         the room
	 * @param x            the x-coordinate
	 * @param y            the y-coordinate
	 * @param isAccessible whether the room is accessible
	 */
	private void drawRoomBorder(Graphics2D graphics, Room room, int x, int y, boolean isAccessible) {
		if (room.isVisited()) {
			graphics.setColor(new Color(0, 200, 0));
		} else if (room.hasGrid() && !room.isGridUnlocked()) {
			graphics.setColor(LOCKED_GRID_OVERLAY);
		} else {
			graphics.setColor(GRID_COLOR);
		}
		graphics.drawRect(x, y, cellSize - 2, cellSize - 2);
	}

	/**
	 * Draws the symbol representing the room type.
	 * 
	 * @param graphics     the graphics context to draw on
	 * @param room         the room
	 * @param x            the x-coordinate
	 * @param y            the y-coordinate
	 * @param isAccessible whether the room is accessible
	 */
	private void drawRoomSymbol(Graphics2D graphics, Room room, int x, int y, boolean isAccessible) {
		String symbol = getRoomSymbol(room.getEffectiveType());
		if (!symbol.isEmpty()) {
			setSymbolColor(graphics, room, isAccessible);
			drawSymbolCentered(graphics, symbol, x, y);
		}
	}

	/**
	 * Draws a symbol centered in a cell.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param symbol   the symbol to draw
	 * @param x        the x-coordinate of the cell
	 * @param y        the y-coordinate of the cell
	 */
	private void drawSymbolCentered(Graphics2D graphics, String symbol, int x, int y) {
		int symbolWidth = graphics.getFontMetrics().stringWidth(symbol);
		int symbolX = x + (cellSize - symbolWidth) / 2;
		int symbolY = y + (cellSize + graphics.getFontMetrics().getAscent()) / 2;
		graphics.drawString(symbol, symbolX, symbolY);
	}

	/**
	 * Sets the appropriate color for a room symbol.
	 * 
	 * @param graphics     the graphics context
	 * @param room         the room
	 * @param isAccessible whether the room is accessible
	 */
	private void setSymbolColor(Graphics2D graphics, Room room, boolean isAccessible) {
		if (room.isVisited()) {
			graphics.setColor(new Color(100, 255, 100));
		} else if (isAccessible) {
			graphics.setColor(Color.WHITE);
		} else {
			graphics.setColor(new Color(100, 100, 100));
		}
	}

	/**
	 * Draws the grid lock indicator if applicable.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param room     the room
	 * @param x        the x-coordinate
	 * @param y        the y-coordinate
	 */
	private void drawGridIndicator(Graphics2D graphics, Room room, int x, int y) {
		if (room.hasGrid() && !room.isGridUnlocked()) {
			graphics.setFont(new Font("Arial", Font.BOLD, Math.max(12, cellSize / 6)));
			graphics.setColor(Color.WHITE);
			String gSym = "⛓";
			int w = graphics.getFontMetrics().stringWidth(gSym);
			graphics.drawString(gSym, x + (cellSize - w) / 2, y + cellSize - Math.max(6, cellSize / 10));
		}
	}

	/**
	 * Draws the map legend showing room types.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawMapLegend(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(12, height / 60)));
		int legendX = gridX;
		int legendY = gridY + floor.height() * cellSize + Math.max(20, height / 25);
		int legendSpacing = Math.max(80, width / 12);

		drawFirstLegendRow(graphics, legendX, legendY, legendSpacing);
		drawSecondLegendRow(graphics, legendX, legendY, legendSpacing, height);
	}

	/**
	 * Draws the first row of legend items.
	 * 
	 * @param graphics      the graphics context to draw on
	 * @param legendX       the x-coordinate for the legend
	 * @param legendY       the y-coordinate for the legend
	 * @param legendSpacing the spacing between items
	 */
	private void drawFirstLegendRow(Graphics2D graphics, int legendX, int legendY, int legendSpacing) {
		drawLegendItem(graphics, legendX, legendY, getRoomColor(TypeRoom.ENEMY, false, true), "Ennemy");
		drawLegendItem(graphics, legendX + legendSpacing, legendY, getRoomColor(TypeRoom.TREASURE, false, true),
				"Treasure");
		drawLegendItem(graphics, legendX + legendSpacing * 2, legendY, getRoomColor(TypeRoom.MERCHANT, false, true),
				"$ Merchant");
		drawLegendItem(graphics, legendX + legendSpacing * 3, legendY, getRoomColor(TypeRoom.HEALER, false, true),
				"+ Healer");
	}

	/**
	 * Draws the second row of legend items.
	 * 
	 * @param graphics      the graphics context to draw on
	 * @param legendX       the x-coordinate for the legend
	 * @param legendY       the y-coordinate for the first row
	 * @param legendSpacing the spacing between items
	 * @param height        the screen height
	 */
	private void drawSecondLegendRow(Graphics2D graphics, int legendX, int legendY, int legendSpacing, int height) {
		int legendY2 = legendY + Math.max(18, height / 35);
		drawLegendItem(graphics, legendX, legendY2, getRoomColor(TypeRoom.EXIT, false, true), "Exit");
		drawLegendItem(graphics, legendX + legendSpacing, legendY2, LOCKED_GRID_OVERLAY, "Grid");
	}

	/**
	 * Draws the backpack view screen.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackpackView(Graphics2D graphics, int width, int height) {
		drawBackpackTitle(graphics, width, height);
		drawBackpackSubtitle(graphics, width, height);
		drawBackpackGrid(graphics, width, height);
	}

	/**
	 * Draws the backpack title.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackpackTitle(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.BOLD, Math.max(22, height / 20)));
		graphics.setColor(new Color(255, 215, 0));
		String title = "BACKPACK MANAGEMENT";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, Math.max(60, height / 9));
	}

	/**
	 * Draws the backpack subtitle with instructions.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackpackSubtitle(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.PLAIN, Math.max(12, height / 45)));
		graphics.setColor(Color.WHITE);
		String subtitle = "Drag an item out of the bag • R for rotation";
		int subtitleWidth = graphics.getFontMetrics().stringWidth(subtitle);
		graphics.drawString(subtitle, (width - subtitleWidth) / 2, Math.max(90, height / 7));
	}

	/**
	 * Draws the backpack grid.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackpackGrid(Graphics2D graphics, int width, int height) {
		int backpackX = (width - backpackView.getWidth()) / 2;
		int backpackY = (height - backpackView.getHeight()) / 2;
		backpackView.setPosition(backpackX, backpackY);
		backpackView.draw(graphics, gameData);
	}

	/**
	 * Draws a single legend item with color box and text.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param x        the x-coordinate
	 * @param y        the y-coordinate
	 * @param color    the color for the legend box
	 * @param text     the legend text
	 * @throws NullPointerException if graphics, color, or text is null
	 */
	private void drawLegendItem(Graphics2D graphics, int x, int y, Color color, String text) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(color);
		Objects.requireNonNull(text);

		graphics.setColor(color);
		graphics.fillRect(x, y - 10, 15, 15);
		graphics.setColor(Color.WHITE);
		graphics.drawString(text, x + 20, y);
	}

	/**
	 * Handles click events on the screen.
	 * 
	 * @param x       the x-coordinate of the click
	 * @param y       the y-coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		long currentTime = System.currentTimeMillis();
		boolean isDoubleClick = (currentTime - lastClickTime) < 300;
		lastClickTime = currentTime;

		if (handleToggleBackpackClick(x, y))
			return;
		if (showingBackpack) {
			handleBackpackModeClick(x, y, isDoubleClick);
			return;
		}

		if (handleButtonsClick(x, y, manager))
			return;
		handleMapClick(x, y, manager);
	}

	/**
	 * Handles clicks on the toggle backpack button.
	 * 
	 * @param x the x-coordinate of the click
	 * @param y the y-coordinate of the click
	 * @return true if the button was clicked, false otherwise
	 */
	private boolean handleToggleBackpackClick(int x, int y) {
		if (toggleBackpackButton != null && toggleBackpackButton.contains(x, y)) {
			if (showingBackpack) {
				backpackView.cancelDrag();
				backpackView.clearStaging();
			}

			showingBackpack = !showingBackpack;
			return true;
		}
		return false;
	}

	/**
	 * Handles clicks in backpack mode.
	 * 
	 * @param x             the x-coordinate of the click
	 * @param y             the y-coordinate of the click
	 * @param isDoubleClick whether this is a double-click
	 */
	private void handleBackpackModeClick(int x, int y, boolean isDoubleClick) {
		if (isDoubleClick) {
			backpackView.handleDoubleClick(x, y);
		} else {
			backpackView.startDrag(x, y);
		}
	}

	/**
	 * Handles clicks on navigation buttons.
	 * 
	 * @param x       the x-coordinate of the click
	 * @param y       the y-coordinate of the click
	 * @param manager the screen manager
	 * @return true if a button was clicked, false otherwise
	 */
	private boolean handleButtonsClick(int x, int y, ScreenManager manager) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).contains(x, y)) {
				handleButtonAction(i, manager);
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles the action for a specific button.
	 * 
	 * @param buttonIndex the index of the button
	 * @param manager     the screen manager
	 */
	private void handleButtonAction(int buttonIndex, ScreenManager manager) {
		if (buttonIndex == 0) {
			manager.nextScreen();
		} else if (buttonIndex == 1) {
			manager.previousScreen();
		}
	}

	/**
	 * Handles clicks on the map grid.
	 * 
	 * @param x       the x-coordinate of the click
	 * @param y       the y-coordinate of the click
	 * @param manager the screen manager
	 */
	private void handleMapClick(int x, int y, ScreenManager manager) {
		if (x >= gridX && y >= gridY) {
			int col = (x - gridX) / cellSize;
			int row = (y - gridY) / cellSize;
			if (isValidMapPosition(row, col)) {
				processRoomClick(row, col, manager);
			}
		}
	}

	/**
	 * Checks if a map position is valid.
	 * 
	 * @param row the row index
	 * @param col the column index
	 * @return true if valid, false otherwise
	 */
	private boolean isValidMapPosition(int row, int col) {
		return row >= 0 && row < floor.height() && col >= 0 && col < floor.width();
	}

	/**
	 * Processes a click on a room in the map.
	 * 
	 * @param row     the row of the clicked room
	 * @param col     the column of the clicked room
	 * @param manager the screen manager
	 */
	private void processRoomClick(int row, int col, ScreenManager manager) {
		Room clickedRoom = floor.getRoom(row, col);

		if (clickedRoom == null) {
			return;
		}

		java.util.List<Position> path = findSimplePath(heroPosition, new Position(row, col));

		if (!validatePath(path)) {
			return;
		}

		if (!unlockGridsOnPath(path)) {
			return;
		}

		moveHeroAndHandleRoom(path, manager);
	}

	/**
	 * Validates that a path is accessible and follows corridor rules.
	 * 
	 * @param path the path to validate
	 * @return true if valid, false otherwise
	 */
	private boolean validatePath(java.util.List<Position> path) {
		if (path == null || path.isEmpty()) {
			showErrorMessage("Invalid move");
			return false;
		}

		if (!isPathAccessible(path)) {
			showErrorMessage("Invalid move");
			return false;
		}

		if (!isValidCorridorPath(path)) {
			showErrorMessage("Invalid move");
			return false;
		}

		return true;
	}

	/**
	 * Attempts to unlock all grids on a path.
	 * 
	 * @param path the path to check
	 * @return true if all grids were unlocked or none exist, false if locked
	 */
	private boolean unlockGridsOnPath(java.util.List<Position> path) {
		for (int i = 1; i < path.size(); i++) {
			Position pos = path.get(i);
			Room room = floor.getRoom(pos.row(), pos.col());

			if (room != null && room.hasGrid() && !room.isGridUnlocked()) {
				if (!room.unlockGrid(gameData.hero())) {
					showErrorMessage("Locked cell - Key needed");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Moves the hero and handles the destination room.
	 * 
	 * @param path    the path to follow
	 * @param manager the screen manager
	 */
	private void moveHeroAndHandleRoom(java.util.List<Position> path, ScreenManager manager) {
		Position destination = path.get(path.size() - 1);
		Room destinationRoom = floor.getRoom(destination.row(), destination.col());

		heroPosition = destination;

		if (destinationRoom.isConvertedToCorridor() || destinationRoom.type() == TypeRoom.CORRIDOR) {
			return;
		}

		destinationRoom.markVisited();
		gameData.enterRoom(destinationRoom);
		handleRoomType(destinationRoom, manager);
	}

	/**
	 * Displays a temporary error message.
	 * 
	 * @param message the error message to display
	 * @throws NullPointerException if message is null
	 */
	private void showErrorMessage(String message) {
		Objects.requireNonNull(message);
		this.errorMessage = message;
		this.errorMessageTime = System.currentTimeMillis();
	}

	/**
	 * Finds a simple path between two positions (horizontal then vertical
	 * movement).
	 * 
	 * @param start the starting position
	 * @param end   the ending position
	 * @return the path as a list of positions
	 * @throws NullPointerException if start or end is null
	 */
	private java.util.List<Position> findSimplePath(Position start, Position end) {
		Objects.requireNonNull(start);
		Objects.requireNonNull(end);

		java.util.List<Position> path = new java.util.ArrayList<>();
		path.add(start);

		addHorizontalMovements(path, start, end);
		addVerticalMovements(path, end);

		return path;
	}

	/**
	 * Adds horizontal movements to the path.
	 * 
	 * @param path  the path to add to
	 * @param start the starting position
	 * @param end   the ending position
	 */
	private void addHorizontalMovements(java.util.List<Position> path, Position start, Position end) {
		int currentRow = start.row();
		int currentCol = start.col();

		while (currentCol != end.col()) {
			if (currentCol < end.col()) {
				currentCol++;
			} else {
				currentCol--;
			}
			path.add(new Position(currentRow, currentCol));
		}
	}

	/**
	 * Adds vertical movements to the path.
	 * 
	 * @param path the path to add to
	 * @param end  the ending position
	 */
	private void addVerticalMovements(java.util.List<Position> path, Position end) {
		Position last = path.get(path.size() - 1);
		int currentRow = last.row();
		int currentCol = last.col();

		while (currentRow != end.row()) {
			if (currentRow < end.row()) {
				currentRow++;
			} else {
				currentRow--;
			}
			path.add(new Position(currentRow, currentCol));
		}
	}

	/**
	 * Checks if each position in the path is accessible from the previous one.
	 * 
	 * @param path the path to check
	 * @return true if all positions are accessible, false otherwise
	 */
	private boolean isPathAccessible(java.util.List<Position> path) {
		for (int i = 1; i < path.size(); i++) {
			if (!isStepAccessible(path, i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if a single step in the path is accessible.
	 * 
	 * @param path      the full path
	 * @param stepIndex the index of the step to check
	 * @return true if accessible, false otherwise
	 */
	private boolean isStepAccessible(java.util.List<Position> path, int stepIndex) {
		Position prev = path.get(stepIndex - 1);
		Position curr = path.get(stepIndex);

		Room currRoom = floor.getRoom(curr.row(), curr.col());
		if (currRoom == null) {
			return false;
		}

		if (!floor.isRoomAccessible(currRoom, prev)) {
			return false;
		}

		int distance = Math.abs(curr.row() - prev.row()) + Math.abs(curr.col() - prev.col());
		return distance == 1;
	}

	/**
	 * Checks that the path contains only corridors except for the final
	 * destination.
	 * 
	 * @param path the path to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidCorridorPath(java.util.List<Position> path) {
		if (path.size() <= 1) {
			return true;
		}

		for (int i = 1; i < path.size() - 1; i++) {
			Position pos = path.get(i);
			Room room = floor.getRoom(pos.row(), pos.col());

			if (room.type() != TypeRoom.CORRIDOR && !room.isConvertedToCorridor()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Handles the action for different room types.
	 * 
	 * @param clickedRoom the room that was clicked
	 * @param manager     the screen manager
	 */
	private void handleRoomType(Room clickedRoom, ScreenManager manager) {
		if (clickedRoom.isConvertedToCorridor()) {
			return;
		}

		var screenInfo = manager.getScreenInfo();
		if (screenInfo == null)
			return;

		int screenWidth = screenInfo.getWidth();
		int screenHeight = screenInfo.getHeight();

		switch (clickedRoom.type()) {
		case HEALER -> openHealerScreen(screenWidth, screenHeight, manager);
		case ENEMY -> openEnemyScreen(clickedRoom, screenWidth, screenHeight, manager);
		case TREASURE -> openTreasureScreen(manager);
		case MERCHANT -> openMerchantScreen(screenWidth, screenHeight, manager);
		case CORRIDOR -> {
		}
		case EXIT -> handleExitRoom(manager);
		case SURPRISE -> System.out.println("Rencontre surprise !");
		}
	}

	/**
	 * Opens the healer screen.
	 * 
	 * @param screenWidth  the screen width
	 * @param screenHeight the screen height
	 * @param manager      the screen manager
	 */
	private void openHealerScreen(int screenWidth, int screenHeight, ScreenManager manager) {
		HealerScreen healerScreen = new HealerScreen(gameData, healerImage);
		healerScreen.addHealButtons(buttonImage, screenWidth, screenHeight);
		manager.addScreen(healerScreen);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Opens the enemy combat screen.
	 * 
	 * @param clickedRoom  the room containing enemies
	 * @param screenWidth  the screen width
	 * @param screenHeight the screen height
	 * @param manager      the screen manager
	 */
	private void openEnemyScreen(Room clickedRoom, int screenWidth, int screenHeight, ScreenManager manager) {
		BufferedImage mainBackground = loadMainBackground();
		Combat combat = gameData.startCombat(clickedRoom);
		EnemyScreen enemyScreen = new EnemyScreen(gameData, combat, heroImage, mainBackground, buttonImage);
		enemyScreen.addActionButtons(screenWidth, screenHeight);
		manager.addScreen(enemyScreen);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Loads the main background image.
	 * 
	 * @return the loaded image, or null if loading fails
	 */
	private BufferedImage loadMainBackground() {
		try {
			return new ImageLoader("image", "main_background.jpg").getImage();
		} catch (RuntimeException e) {
			System.err.println("Impossible de charger main_background.jpg: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Opens the treasure screen.
	 * 
	 * @param manager the screen manager
	 */
	private void openTreasureScreen(ScreenManager manager) {
		ImageLoader buttonImageLoader = new ImageLoader("image", "button.png");
		TreasureScreen treasureScreen = new TreasureScreen(gameData, buttonImageLoader);
		manager.addScreen(treasureScreen);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Opens the merchant screen.
	 * 
	 * @param screenWidth  the screen width
	 * @param screenHeight the screen height
	 * @param manager      the screen manager
	 */
	private void openMerchantScreen(int screenWidth, int screenHeight, ScreenManager manager) {
		MerchantScreen merchantScreen = new MerchantScreen(gameData, merchantImage, buttonImage);
		merchantScreen.addMerchantButtons(buttonImage, screenWidth, screenHeight);
		manager.addScreen(merchantScreen);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Handles the exit room, progressing to the next floor or showing victory
	 * screen.
	 * 
	 * @param manager the screen manager
	 */
	private void handleExitRoom(ScreenManager manager) {
		Floor nextFloor = gameData.progressToNextFloor();

		if (nextFloor != null) {
			floor = nextFloor;
			heroPosition = floor.startPos();
		} else {
			showVictoryScreen(manager);
		}
	}

	/**
	 * Shows the victory screen with the hero's scores.
	 * 
	 * @param manager the screen manager
	 */
	private void showVictoryScreen(ScreenManager manager) {
		BufferedImage backgroundImg = loadMainBackground();
		GameOverScreen victoryScreen = new GameOverScreen(gameData.hero(), backgroundImg, manager.getHallOfFame(),
				true);
		manager.addScreen(victoryScreen);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Handles mouse movement events.
	 * 
	 * @param x the x-coordinate of the mouse
	 * @param y the y-coordinate of the mouse
	 */
	@Override
	public void handleMouseMove(int x, int y) {
		if (showingBackpack && backpackView.isDragging()) {
			backpackView.updateDrag(x, y);
		}
	}

	/**
	 * Handles mouse release events.
	 * 
	 * @param x       the x-coordinate where the mouse was released
	 * @param y       the y-coordinate where the mouse was released
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		if (showingBackpack && backpackView.isDragging()) {
			backpackView.endDrag(x, y);
		}
	}

	/**
	 * Handles rotation events (R key press).
	 * 
	 * @param x the x-coordinate of the mouse
	 * @param y the y-coordinate of the mouse
	 */
	@Override
	public void handleRotate(int x, int y) {
		if (showingBackpack) {
			if (backpackView.isDragging()) {
				backpackView.rotateItemUnderMouse();
			} else {
				backpackView.rotateItemAt(x, y);
			}
		}
	}

	/**
	 * Handles right-click events.
	 * 
	 * @param x the x-coordinate of the click
	 * @param y the y-coordinate of the click
	 */
	@Override
	public void handleRightClick(int x, int y) {
		if (showingBackpack) {
			backpackView.handleDoubleClick(x, y);
		}
	}

	/**
	 * Handles key press events.
	 * 
	 * @param key     the character key that was pressed
	 * @param x       the x-coordinate of the mouse
	 * @param y       the y-coordinate of the mouse
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleKeyPress(char key, int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		if (showingBackpack && (key == 'i' || key == 'I')) {
			Item selected = backpackView.getSelectedItem(x, y);
			if (selected != null) {
				int currentIndex = manager.getCurrentScreenIndex();
				manager.addScreen(new ItemInfoScreen(selected, buttonImage, currentIndex));
				manager.goToScreen(manager.getScreenCount() - 1);
			}
		}
	}
}