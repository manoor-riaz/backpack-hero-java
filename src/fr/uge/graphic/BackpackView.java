package fr.uge.graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.uge.backpackhero.BackPack;
import fr.uge.backpackhero.Dimension;
import fr.uge.backpackhero.GameData;
import fr.uge.backpackhero.Gold;
import fr.uge.backpackhero.Item;
import fr.uge.backpackhero.Orientation;
import fr.uge.backpackhero.Position;
import fr.uge.backpackhero.Room;
import fr.uge.backpackhero.TypeRoom;

/**
 * Graphical view component for rendering a backpack and its contents. Handles
 * the visual representation of items, staging area, drag-and-drop interactions,
 * and cell unlocking mechanics.
 */
public class BackpackView {
	private final BackPack backpack;
	private final int itemSize;

	private int x, y;

	private Item draggedItem = null;
	private int dragCurrentX, dragCurrentY;
	private Position originalPosition = null;
	private Orientation draggedOrientation = Orientation.ORIENTATION_0;
	private boolean draggedFromStaging = false;

	private final List<StagedItem> stagedItems = new ArrayList<>();
	private final Map<String, BufferedImage> itemImages = new HashMap<>();
	private boolean showLockedCells = false;

	/**
	 * Constructs a new BackpackView.
	 * 
	 * @param backpack the backpack to display
	 * @param rows     the number of rows (unused but kept for compatibility)
	 * @param cols     the number of columns (unused but kept for compatibility)
	 * @param itemSize the size of each cell in pixels
	 * @throws NullPointerException     if backpack is null
	 * @throws IllegalArgumentException if itemSize is not positive
	 */
	public BackpackView(BackPack backpack, int rows, int cols, int itemSize) {
		this.backpack = Objects.requireNonNull(backpack);
		if (itemSize <= 0) {
			throw new IllegalArgumentException("itemSize must be > 0");
		}
		this.itemSize = itemSize;
	}

	/**
	 * Sets the current room type for display purposes.
	 * 
	 * @param room the type of room to set
	 * @throws NullPointerException if room is null
	 */
	public void setCurrentRoom(TypeRoom room) {
		Objects.requireNonNull(room);
	}

	/**
	 * Sets whether locked cells should be displayed.
	 * 
	 * @param show true to show locked cells, false to hide them
	 */
	public void setShowLockedCells(boolean show) {
		this.showLockedCells = show;
	}

	/**
	 * Associates an image with an item name for rendering.
	 * 
	 * @param itemName the name of the item
	 * @param image    the image to use for rendering
	 * @throws NullPointerException if itemName or image is null
	 */
	public void addItemImage(String itemName, BufferedImage image) {
		Objects.requireNonNull(itemName);
		Objects.requireNonNull(image);
		itemImages.put(itemName, image);
	}

	/**
	 * Sets the screen position of the backpack view.
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void setPosition(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("x and y must be >= 0");
		}

		this.x = x;
		this.y = y;
	}

	/**
	 * Draws the complete backpack view including grid, items, and instructions.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param gameData the current game data
	 * @throws NullPointerException if graphics or gameData is null
	 */
	public void draw(Graphics2D graphics, GameData gameData) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(gameData);

		drawTitle(graphics);

		int actualRows = backpack.height();
		int actualCols = backpack.width();

		drawCells(graphics, actualRows, actualCols);
		drawItems(graphics);
		drawInstructions(graphics, gameData, actualRows, actualCols);
		drawUnlockCounter(graphics, actualRows);
	}

	/**
	 * Draws the backpack title.
	 * 
	 * @param graphics the graphics context to draw on
	 */
	private void drawTitle(Graphics2D graphics) {
		graphics.setFont(new Font("Arial", Font.BOLD, 24));
		graphics.setColor(Color.WHITE);
		graphics.drawString("BACKPACK", x, y - 10);
	}

	/**
	 * Draws all cells in the backpack grid.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param actualRows the number of rows to draw
	 * @param actualCols the number of columns to draw
	 */
	private void drawCells(Graphics2D graphics, int actualRows, int actualCols) {
		for (int row = 0; row < actualRows; row++) {
			for (int col = 0; col < actualCols; col++) {
				drawSingleCell(graphics, row, col);
			}
		}
	}

	/**
	 * Draws a single cell in the backpack grid.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param row      the row index of the cell
	 * @param col      the column index of the cell
	 */
	private void drawSingleCell(Graphics2D graphics, int row, int col) {
		boolean unlocked = backpack.isCellUnlocked(row, col);

		if (!unlocked && !showLockedCells) {
			return;
		}

		int cellX = x + col * (itemSize + 5);
		int cellY = y + row * (itemSize + 5);

		if (!unlocked) {
			drawLockedCell(graphics, cellX, cellY, row, col);
		} else {
			drawUnlockedCell(graphics, cellX, cellY);
		}
	}

	/**
	 * Draws a locked cell with appropriate styling and symbols.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param cellX    the x-coordinate of the cell
	 * @param cellY    the y-coordinate of the cell
	 * @param row      the row index of the cell
	 * @param col      the column index of the cell
	 */
	private void drawLockedCell(Graphics2D graphics, int cellX, int cellY, int row, int col) {
		graphics.setColor(new Color(100, 100, 100));
		graphics.fillRect(cellX, cellY, itemSize, itemSize);

		boolean canUnlock = backpack.canUnlockCell(row, col);
		boolean hasPending = backpack.getNbCells() > 0;

		if (canUnlock && hasPending) {
			graphics.setColor(new Color(255, 215, 0));
			graphics.setStroke(new BasicStroke(3));
		} else {
			graphics.setColor(Color.BLACK);
			graphics.setStroke(new BasicStroke(1));
		}

		graphics.drawRect(cellX, cellY, itemSize, itemSize);
		graphics.setStroke(new BasicStroke(1));

		drawLockSymbol(graphics, cellX, cellY);
	}

	/**
	 * Draws the lock symbol on a locked cell.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param cellX    the x-coordinate of the cell
	 * @param cellY    the y-coordinate of the cell
	 */
	private void drawLockSymbol(Graphics2D graphics, int cellX, int cellY) {
		graphics.setColor(Color.RED);
		graphics.setFont(new Font("Arial", Font.BOLD, itemSize / 2));
		String lockSymbol = "✕";
		int symbolWidth = graphics.getFontMetrics().stringWidth(lockSymbol);
		graphics.drawString(lockSymbol, cellX + (itemSize - symbolWidth) / 2,
				cellY + itemSize / 2 + graphics.getFontMetrics().getAscent() / 4);
	}

	/**
	 * Draws an unlocked cell.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param cellX    the x-coordinate of the cell
	 * @param cellY    the y-coordinate of the cell
	 */
	private void drawUnlockedCell(Graphics2D graphics, int cellX, int cellY) {
		graphics.setColor(new Color(200, 200, 200));
		graphics.fillRect(cellX, cellY, itemSize, itemSize);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(cellX, cellY, itemSize, itemSize);
	}

	/**
	 * Draws all items including backpack items, staged items, and dragged item.
	 * 
	 * @param graphics the graphics context to draw on
	 */
	private void drawItems(Graphics2D graphics) {
		for (Item item : backpack.items()) {
			if (item == draggedItem) {
				continue;
			}
			Position pos = backpack.position(item);
			if (pos != null) {
				drawItem(graphics, item, pos);
			}
		}

		for (StagedItem staged : stagedItems) {
			if (staged.getItem() == draggedItem) {
				continue;
			}
			drawStagedItemWithLabel(graphics, staged, false);
		}

		if (draggedItem != null) {
			drawDraggedItem(graphics);
		}
	}

	/**
	 * Draws the instructions text based on current room type.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param gameData   the current game data
	 * @param actualRows the number of rows in the backpack
	 * @param actualCols the number of columns in the backpack
	 */
	private void drawInstructions(Graphics2D graphics, GameData gameData, int actualRows, int actualCols) {
		graphics.setFont(new Font("Arial", Font.BOLD, 18));
		graphics.setColor(Color.WHITE);
		int instructY = y + actualRows * (itemSize + 5) + 25;
		int offsetX = -20;

		Room room = gameData.getCurrentRoom();
		TypeRoom type = (room == null) ? TypeRoom.CORRIDOR : room.type();

		String instructions = getInstructionsForRoomType(type);
		graphics.drawString(instructions, x + offsetX, instructY);
	}

	/**
	 * Returns the appropriate instruction text for a given room type.
	 * 
	 * @param type the type of room
	 * @return the instructions string
	 */
	private String getInstructionsForRoomType(TypeRoom type) {
		if (type == TypeRoom.ENEMY) {
			return "Click : use | I : info";
		} else if (type == TypeRoom.MERCHANT) {
			return "Double click shop : buy | I : info | Double click item : sell | R : rotate";
		} else {
			return "Drag: Move | Drag out of the bag: Remove | R: Rotate | I: Info";
		}
	}

	/**
	 * Draws the unlock counter if there are cells to unlock.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param actualRows the number of rows in the backpack
	 */
	private void drawUnlockCounter(Graphics2D graphics, int actualRows) {
		if (showLockedCells && backpack.getNbCells() > 0) {
			graphics.setFont(new Font("Arial", Font.BOLD, 18));
			graphics.setColor(Color.WHITE);
			graphics.drawString("Unblockable cells: " + backpack.getNbCells(), x - 20, y - 35);
		}
	}

	/**
	 * Draws a staged item with its label.
	 * 
	 * @param graphics  the graphics context to draw on
	 * @param staged    the staged item to draw
	 * @param highlight whether to highlight the item
	 * @throws NullPointerException if graphics or staged is null
	 */
	private void drawStagedItemWithLabel(Graphics2D graphics, StagedItem staged, boolean highlight) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(staged);

		List<Position> orientedCells = staged.getItem().shape().turnItem(staged.getOrientation());
		Dimension dim = staged.getItem().shape().dimensionOfItem(orientedCells);

		int itemWidth = dim.width() * itemSize;
		int itemHeight = dim.height() * itemSize;

		if (highlight) {
			drawHighlight(graphics, staged.getScreenX(), staged.getScreenY(), itemWidth, itemHeight);
		}

		drawStagedItemImage(graphics, staged, itemWidth, itemHeight);
		drawStagedItemBorder(graphics, staged, itemWidth, itemHeight, highlight);
		drawStagedItemLabel(graphics, staged, itemWidth, itemHeight);
	}

	/**
	 * Draws a highlight rectangle for a staged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param screenX    the x-coordinate
	 * @param screenY    the y-coordinate
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 */
	private void drawHighlight(Graphics2D graphics, int screenX, int screenY, int itemWidth, int itemHeight) {
		graphics.setColor(new Color(255, 255, 0, 100));
		graphics.fillRect(screenX, screenY, itemWidth, itemHeight);
	}

	/**
	 * Draws the image or fallback color for a staged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param staged     the staged item
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 */
	private void drawStagedItemImage(Graphics2D graphics, StagedItem staged, int itemWidth, int itemHeight) {
		BufferedImage itemImage = itemImages.get(staged.getItem().name());

		if (itemImage != null) {
			drawRotatedImage(graphics, itemImage, staged.getScreenX(), staged.getScreenY(), itemWidth, itemHeight,
					staged.getOrientation().nbOfRotation());
		} else {
			Color itemColor = staged.getItem().isGold() ? new Color(255, 215, 0) : new Color(150, 150, 255);
			graphics.setColor(itemColor);
			graphics.fillRect(staged.getScreenX(), staged.getScreenY(), itemWidth, itemHeight);
		}
	}

	/**
	 * Draws the border around a staged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param staged     the staged item
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 * @param highlight  whether the item is highlighted
	 */
	private void drawStagedItemBorder(Graphics2D graphics, StagedItem staged, int itemWidth, int itemHeight,
			boolean highlight) {
		graphics.setColor(highlight ? Color.YELLOW : Color.BLACK);
		graphics.setStroke(new BasicStroke(highlight ? 3 : 2));
		graphics.drawRect(staged.getScreenX(), staged.getScreenY(), itemWidth, itemHeight);
		graphics.setStroke(new BasicStroke(1));
	}

	/**
	 * Draws the label below a staged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param staged     the staged item
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 */
	private void drawStagedItemLabel(Graphics2D graphics, StagedItem staged, int itemWidth, int itemHeight) {
		graphics.setFont(new Font("Arial", Font.BOLD, 14));
		graphics.setColor(Color.WHITE);

		String displayName = getStagedItemDisplayName(staged);

		int textWidth = graphics.getFontMetrics().stringWidth(displayName);
		int textX = staged.getScreenX() + (itemWidth - textWidth) / 2;
		int textY = staged.getScreenY() + itemHeight + 18;

		graphics.setColor(new Color(0, 0, 0, 180));
		graphics.fillRoundRect(textX - 5, textY - 15, textWidth + 10, 20, 5, 5);

		graphics.setColor(Color.WHITE);
		graphics.drawString(displayName, textX, textY);
	}

	/**
	 * Gets the display name for a staged item.
	 * 
	 * @param staged the staged item
	 * @return the display name
	 */
	private String getStagedItemDisplayName(StagedItem staged) {
		Objects.requireNonNull(staged);

		return switch (staged.getItem()) {
		case Gold gold -> gold.purse() + " Or";
		default -> staged.getItem().name();
		};
	}

	/**
	 * Adds an item to the staging area. If the item is gold, merges it with
	 * existing gold in the backpack.
	 * 
	 * @param item    the item to add
	 * @param screenX the x-coordinate on screen
	 * @param screenY the y-coordinate on screen
	 * @throws NullPointerException if item is null
	 */
	public void addToStaging(Item item, int screenX, int screenY) {
		Objects.requireNonNull(item);

		switch (item) {
		case Gold newGold -> {
			backpack.mergeGold(newGold.purse());
			return;
		}
		default -> {
		}
		}

		stagedItems.add(new StagedItem(item, screenX, screenY, Orientation.ORIENTATION_0));
	}

	/**
	 * Draws the gold amount overlay on an item.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param gold     the gold item
	 * @param cellX    the x-coordinate of the cell
	 * @param cellY    the y-coordinate of the cell
	 * @param width    the width of the item
	 * @param height   the height of the item
	 * @throws NullPointerException if graphics or gold is null
	 */
	private void drawGoldAmount(Graphics2D graphics, Gold gold, int cellX, int cellY, int width, int height) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(gold);

		String amount = String.valueOf(gold.purse());

		int fontSize = Math.max(14, itemSize / 3);
		graphics.setFont(new Font("Arial", Font.BOLD, fontSize));

		FontMetrics fm = graphics.getFontMetrics();
		int textWidth = fm.stringWidth(amount);
		int textHeight = fm.getAscent();

		int textX = cellX + 2 + (width - textWidth) / 2;
		int textY = cellY + 2 + (height + textHeight) / 2 - 2;

		drawGoldAmountOutline(graphics, amount, textX, textY);

		graphics.setColor(Color.BLACK);
		graphics.drawString(amount, textX, textY);
	}

	/**
	 * Draws the white outline for the gold amount text.
	 * 
	 * @param graphics the graphics context to draw on
	 * @param amount   the amount string to draw
	 * @param textX    the x-coordinate of the text
	 * @param textY    the y-coordinate of the text
	 */
	private void drawGoldAmountOutline(Graphics2D graphics, String amount, int textX, int textY) {
		graphics.setColor(Color.WHITE);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx != 0 || dy != 0) {
					graphics.drawString(amount, textX + dx, textY + dy);
				}
			}
		}
	}

	/**
	 * Draws an item at its position in the backpack.
	 * 
	 * @param graphics  the graphics context to draw on
	 * @param item      the item to draw
	 * @param anchorPos the anchor position of the item
	 * @throws NullPointerException if graphics, item, or anchorPos is null
	 */
	private void drawItem(Graphics2D graphics, Item item, Position anchorPos) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(item);
		Objects.requireNonNull(anchorPos);

		int cellX = x + anchorPos.col() * (itemSize + 5);
		int cellY = y + anchorPos.row() * (itemSize + 5);

		var orientation = backpack.orientation(item);
		var orientedCells = item.shape().turnItem(orientation);
		var dim = item.shape().dimensionOfItem(orientedCells);

		int itemWidth = dim.width() * (itemSize + 5) - 5;
		int itemHeight = dim.height() * (itemSize + 5) - 5;

		var itemImage = itemImages.get(item.name());

		if (itemImage != null) {
			drawItemWithImage(graphics, item, cellX, cellY, itemWidth, itemHeight, orientation);
			return;
		}

		drawCellShapeFallback(graphics, item, anchorPos, orientedCells);

		switch (item) {
		case Gold gold -> drawGoldAmount(graphics, gold, cellX, cellY, itemWidth, itemHeight);
		default -> {
		}
		}
	}

	/**
	 * Draws an item using its image.
	 * 
	 * @param graphics    the graphics context to draw on
	 * @param item        the item to draw
	 * @param cellX       the x-coordinate of the cell
	 * @param cellY       the y-coordinate of the cell
	 * @param itemWidth   the width of the item
	 * @param itemHeight  the height of the item
	 * @param orientation the orientation of the item
	 */
	private void drawItemWithImage(Graphics2D graphics, Item item, int cellX, int cellY, int itemWidth, int itemHeight,
			Orientation orientation) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(item);
		Objects.requireNonNull(orientation);

		var itemImage = itemImages.get(item.name());
		drawRotatedImage(graphics, itemImage, cellX + 2, cellY + 2, itemWidth, itemHeight, orientation.nbOfRotation());
		graphics.setColor(Color.BLACK);
		graphics.drawRect(cellX + 2, cellY + 2, itemWidth, itemHeight);

		switch (item) {
		case Gold gold -> drawGoldAmount(graphics, gold, cellX, cellY, itemWidth, itemHeight);
		default -> {
		}
		}
	}

	/**
	 * Draws an item using colored cells as fallback when no image is available.
	 * 
	 * @param graphics      the graphics context to draw on
	 * @param item          the item to draw
	 * @param anchorPos     the anchor position
	 * @param orientedCells the cells occupied by the item
	 * @throws NullPointerException if graphics, item, anchorPos, or orientedCells
	 *                              is null
	 */
	private void drawCellShapeFallback(Graphics2D graphics, Item item, Position anchorPos,
			List<Position> orientedCells) {
		Objects.requireNonNull(graphics);
		Objects.requireNonNull(item);
		Objects.requireNonNull(anchorPos);
		Objects.requireNonNull(orientedCells);

		Color itemColor = getItemColor(item);

		for (var cell : orientedCells) {
			drawSingleItemCell(graphics, anchorPos, cell, itemColor);
		}
	}

	/**
	 * Gets the color for an item based on its type.
	 * 
	 * @param item the item
	 * @return the color for the item
	 */
	private Color getItemColor(Item item) {
		if (item.isGold()) {
			return new Color(255, 215, 0);
		} else if (item.isCurse()) {
			return new Color(20, 20, 20);
		} else {
			return new Color(150, 150, 255);
		}
	}

	/**
	 * Draws a single cell of an item.
	 * 
	 * @param graphics  the graphics context to draw on
	 * @param anchorPos the anchor position
	 * @param cell      the relative cell position
	 * @param itemColor the color to use
	 */
	private void drawSingleItemCell(Graphics2D graphics, Position anchorPos, Position cell, Color itemColor) {
		int r = anchorPos.row() + cell.row();
		int c = anchorPos.col() + cell.col();

		int px = x + c * (itemSize + 5) + 2;
		int py = y + r * (itemSize + 5) + 2;

		graphics.setColor(itemColor);
		graphics.fillRect(px, py, itemSize - 4, itemSize - 4);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(px, py, itemSize - 4, itemSize - 4);
	}

	/**
	 * Draws the currently dragged item at the cursor position.
	 * 
	 * @param graphics the graphics context to draw on
	 */
	private void drawDraggedItem(Graphics2D graphics) {
		Objects.requireNonNull(graphics);

		var orientedCells = draggedItem.shape().turnItem(draggedOrientation);
		var dim = draggedItem.shape().dimensionOfItem(orientedCells);

		int itemWidth = dim.width() * itemSize;
		int itemHeight = dim.height() * itemSize;

		int drawX = dragCurrentX - itemWidth / 2;
		int drawY = dragCurrentY - itemHeight / 2;

		var itemImage = itemImages.get(draggedItem.name());
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

		drawDraggedItemVisual(graphics, itemImage, drawX, drawY, itemWidth, itemHeight);
		drawDraggedItemBorder(graphics, drawX, drawY, itemWidth, itemHeight);

		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		switch (draggedItem) {
		case Gold gold -> drawGoldAmount(graphics, gold, drawX - 2, drawY - 2, itemWidth, itemHeight);
		default -> {
		}
		}
	}

	/**
	 * Draws the visual representation of a dragged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param itemImage  the item image (can be null)
	 * @param drawX      the x-coordinate to draw at
	 * @param drawY      the y-coordinate to draw at
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 */
	private void drawDraggedItemVisual(Graphics2D graphics, BufferedImage itemImage, int drawX, int drawY,
			int itemWidth, int itemHeight) {
		if (itemImage != null) {
			drawRotatedImage(graphics, itemImage, drawX, drawY, itemWidth, itemHeight,
					draggedOrientation.nbOfRotation());
		} else {
			Color itemColor = draggedItem.isGold() ? new Color(255, 215, 0) : new Color(150, 150, 255);
			graphics.setColor(itemColor);
			graphics.fillRect(drawX, drawY, itemWidth, itemHeight);
		}
	}

	/**
	 * Draws the border around a dragged item.
	 * 
	 * @param graphics   the graphics context to draw on
	 * @param drawX      the x-coordinate
	 * @param drawY      the y-coordinate
	 * @param itemWidth  the width of the item
	 * @param itemHeight the height of the item
	 */
	private void drawDraggedItemBorder(Graphics2D graphics, int drawX, int drawY, int itemWidth, int itemHeight) {
		graphics.setColor(Color.YELLOW);
		graphics.setStroke(new BasicStroke(3));
		graphics.drawRect(drawX, drawY, itemWidth, itemHeight);
		graphics.setStroke(new BasicStroke(1));
	}

	/**
	 * Draws a rotated image at the specified position.
	 * 
	 * @param g2d           the graphics context to draw on
	 * @param image         the image to draw
	 * @param x             the x-coordinate
	 * @param y             the y-coordinate
	 * @param width         the target width
	 * @param height        the target height
	 * @param rotationCount the number of 90-degree rotations
	 * @throws NullPointerException if g2d or image is null
	 */
	private void drawRotatedImage(Graphics2D g2d, BufferedImage image, int x, int y, int width, int height,
			int rotationCount) {
		Objects.requireNonNull(g2d);
		Objects.requireNonNull(image);

		rotationCount = rotationCount % 4;
		double angle = rotationCount * Math.PI / 2;

		AffineTransform old = g2d.getTransform();
		AffineTransform at = createRotationTransform(x, y, width, height, angle, image, rotationCount);

		g2d.setTransform(at);
		g2d.drawImage(image, 0, 0, null);
		g2d.setTransform(old);
	}

	/**
	 * Creates an affine transform for rotating and scaling an image.
	 * 
	 * @param x             the x-coordinate
	 * @param y             the y-coordinate
	 * @param width         the target width
	 * @param height        the target height
	 * @param angle         the rotation angle in radians
	 * @param image         the image being transformed
	 * @param rotationCount the number of 90-degree rotations
	 * @return the configured affine transform
	 */
	private AffineTransform createRotationTransform(int x, int y, int width, int height, double angle,
			BufferedImage image, int rotationCount) {
		AffineTransform at = new AffineTransform();
		at.translate(x + width / 2.0, y + height / 2.0);
		at.rotate(angle);

		double scaleX, scaleY;
		if (rotationCount % 2 == 1) {
			scaleX = (double) width / image.getHeight();
			scaleY = (double) height / image.getWidth();
		} else {
			scaleX = (double) width / image.getWidth();
			scaleY = (double) height / image.getHeight();
		}

		at.scale(scaleX, scaleY);
		at.translate(-image.getWidth() / 2.0, -image.getHeight() / 2.0);

		return at;
	}

	/**
	 * Checks if the given coordinates are within the backpack bounds.
	 * 
	 * @param clickX the x-coordinate to check
	 * @param clickY the y-coordinate to check
	 * @return true if the coordinates are within bounds, false otherwise
	 */
	public boolean contains(int clickX, int clickY) {
		int widthPx = backpack.width() * (itemSize + 5);
		int heightPx = backpack.height() * (itemSize + 5);
		return clickX >= x && clickX < x + widthPx && clickY >= y && clickY < y + heightPx;
	}

	/**
	 * Attempts to unlock a cell at the given mouse position.
	 * 
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 * @return true if a cell was unlocked, false otherwise
	 */
	public boolean tryUnlockCell(int mouseX, int mouseY) {
		if (!contains(mouseX, mouseY)) {
			return false;
		}

		int col = (mouseX - x) / (itemSize + 5);
		int row = (mouseY - y) / (itemSize + 5);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			return backpack.unlockCell(row, col);
		}

		return false;
	}

	/**
	 * Starts dragging an item from either the staging area or the backpack. Curses
	 * cannot be dragged.
	 * 
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 */
	public void startDrag(int mouseX, int mouseY) {
		if (tryStartDragFromStaging(mouseX, mouseY)) {
			return;
		}
		tryStartDragFromBackpack(mouseX, mouseY);
	}

	/**
	 * Attempts to start dragging from the staging area.
	 * 
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 * @return true if drag started, false otherwise
	 */
	private boolean tryStartDragFromStaging(int mouseX, int mouseY) {
		for (int i = stagedItems.size() - 1; i >= 0; i--) {
			StagedItem staged = stagedItems.get(i);
			if (staged.contains(mouseX, mouseY, itemSize)) {
				initializeDragFromStaging(staged, mouseX, mouseY, i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Initializes drag state when starting from staging area.
	 * 
	 * @param staged the staged item being dragged
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 * @param index  the index to remove from staging
	 */
	private void initializeDragFromStaging(StagedItem staged, int mouseX, int mouseY, int index) {
		draggedItem = staged.getItem();
		draggedOrientation = staged.getOrientation();
		draggedFromStaging = true;
		dragCurrentX = mouseX;
		dragCurrentY = mouseY;
		stagedItems.remove(index);
	}

	/**
	 * Attempts to start dragging from the backpack.
	 * 
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 */
	private void tryStartDragFromBackpack(int mouseX, int mouseY) {
		if (!contains(mouseX, mouseY)) {
			return;
		}

		int col = (mouseX - x) / (itemSize + 5);
		int row = (mouseY - y) / (itemSize + 5);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			Item clickedItem = backpack.itemAt(row, col);
			if (clickedItem != null && clickedItem.isMovable()) {
				initializeDragFromBackpack(clickedItem, mouseX, mouseY);
			}
		}
	}

	/**
	 * Initializes drag state when starting from backpack.
	 * 
	 * @param clickedItem the item being dragged
	 * @param mouseX      the x-coordinate of the mouse
	 * @param mouseY      the y-coordinate of the mouse
	 */
	private void initializeDragFromBackpack(Item clickedItem, int mouseX, int mouseY) {
		draggedItem = clickedItem;
		dragCurrentX = mouseX;
		dragCurrentY = mouseY;
		originalPosition = backpack.position(clickedItem);
		draggedOrientation = backpack.orientation(clickedItem);
		draggedFromStaging = false;
		backpack.remove(draggedItem);
	}

	/**
	 * Handles double-click events to move items from backpack to staging. Only
	 * movable items (not curses or gold) can be moved this way.
	 * 
	 * @param mouseX the x-coordinate of the mouse
	 * @param mouseY the y-coordinate of the mouse
	 */
	public void handleDoubleClick(int mouseX, int mouseY) {
		if (!contains(mouseX, mouseY)) {
			return;
		}

		int col = (mouseX - x) / (itemSize + 5);
		int row = (mouseY - y) / (itemSize + 5);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			Item clickedItem = backpack.itemAt(row, col);
			if (clickedItem != null && !clickedItem.isGold() && clickedItem.isMovable()) {
				moveItemToStaging(clickedItem, mouseX, mouseY);
			}
		}
	}

	/**
	 * Moves an item from the backpack to the staging area.
	 * 
	 * @param clickedItem the item to move
	 * @param mouseX      the x-coordinate for staging position
	 * @param mouseY      the y-coordinate for staging position
	 */
	private void moveItemToStaging(Item clickedItem, int mouseX, int mouseY) {
		Orientation orientation = backpack.orientation(clickedItem);
		backpack.remove(clickedItem);
		stagedItems.add(new StagedItem(clickedItem, mouseX - itemSize, mouseY - itemSize, orientation));
	}

	/**
	 * Updates the position of the currently dragged item.
	 * 
	 * @param mouseX the new x-coordinate of the mouse
	 * @param mouseY the new y-coordinate of the mouse
	 */
	public void updateDrag(int mouseX, int mouseY) {
		if (draggedItem != null) {
			dragCurrentX = mouseX;
			dragCurrentY = mouseY;
		}
	}

	/**
	 * Ends the drag operation and attempts to place the item.
	 * 
	 * @param mouseX the x-coordinate where drag ended
	 * @param mouseY the y-coordinate where drag ended
	 */
	public void endDrag(int mouseX, int mouseY) {
		if (draggedItem == null) {
			return;
		}

		boolean placed = tryPlaceInBackpack(mouseX, mouseY);

		if (!placed) {
			returnToStaging(mouseX, mouseY);
		}

		resetDragState();
	}

	/**
	 * Attempts to place the dragged item in the backpack.
	 * 
	 * @param mouseX the x-coordinate
	 * @param mouseY the y-coordinate
	 * @return true if placement succeeded, false otherwise
	 */
	private boolean tryPlaceInBackpack(int mouseX, int mouseY) {
		Position gridPos = pixelToGridPosition(mouseX, mouseY);
		if (gridPos == null) {
			return false;
		}

		return backpack.add(draggedItem, gridPos, draggedOrientation);
	}

	/**
	 * Returns the dragged item to the staging area.
	 * 
	 * @param mouseX the x-coordinate for staging position
	 * @param mouseY the y-coordinate for staging position
	 */
	private void returnToStaging(int mouseX, int mouseY) {
		List<Position> cells = draggedItem.shape().turnItem(draggedOrientation);
		Dimension dim = draggedItem.shape().dimensionOfItem(cells);
		int itemWidth = dim.width() * itemSize;
		int itemHeight = dim.height() * itemSize;

		stagedItems
				.add(new StagedItem(draggedItem, mouseX - itemWidth / 2, mouseY - itemHeight / 2, draggedOrientation));
	}

	/**
	 * Resets all drag-related state variables.
	 */
	private void resetDragState() {
		draggedItem = null;
		originalPosition = null;
		draggedOrientation = Orientation.ORIENTATION_0;
		draggedFromStaging = false;
	}

	/**
	 * Checks if an item is currently being dragged.
	 * 
	 * @return true if dragging, false otherwise
	 */
	public boolean isDragging() {
		return draggedItem != null;
	}

	/**
	 * Gets the currently dragged item.
	 * 
	 * @return the dragged item, or null if none
	 */
	public Item getDraggedItem() {
		return draggedItem;
	}

	/**
	 * Cancels the current drag operation and returns the item to its original
	 * position.
	 */
	public void cancelDrag() {
		if (draggedItem == null) {
			return;
		}

		if (draggedFromStaging) {
			stagedItems.add(new StagedItem(draggedItem, dragCurrentX, dragCurrentY, draggedOrientation));
		} else if (originalPosition != null) {
			backpack.add(draggedItem, originalPosition, draggedOrientation);
		}

		resetDragState();
	}

	/**
	 * Discards the currently dragged item without returning it anywhere.
	 */
	public void discardDraggedItem() {
		resetDragState();
	}

	/**
	 * Rotates the currently dragged item by 90 degrees.
	 */
	public void rotateItemUnderMouse() {
		if (draggedItem != null) {
			draggedOrientation = draggedOrientation.rotate();
		}
	}

	/**
	 * Rotates an item at the specified position (staged or in backpack).
	 * 
	 * @param clickX the x-coordinate of the click
	 * @param clickY the y-coordinate of the click
	 */
	public void rotateItemAt(int clickX, int clickY) {
		if (tryRotateStagedItem(clickX, clickY)) {
			return;
		}
		checkItemInBackpack(clickX, clickY);
	}

	/**
	 * Attempts to rotate a staged item at the given position.
	 * 
	 * @param clickX the x-coordinate
	 * @param clickY the y-coordinate
	 * @return true if a staged item was found and rotated, false otherwise
	 */
	private boolean tryRotateStagedItem(int clickX, int clickY) {
		for (StagedItem staged : stagedItems) {
			if (staged.contains(clickX, clickY, itemSize)) {
				staged.setOrientation(staged.getOrientation().rotate());
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for an item in the backpack at the given position.
	 * 
	 * @param clickX the x-coordinate
	 * @param clickY the y-coordinate
	 */
	private void checkItemInBackpack(int clickX, int clickY) {
		if (!contains(clickX, clickY)) {
			return;
		}

		int col = (clickX - x) / (itemSize + 5);
		int row = (clickY - y) / (itemSize + 5);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			backpack.itemAt(row, col);
		}
	}

	/**
	 * Gets the item at the specified pixel coordinates in the backpack.
	 * 
	 * @param mouseX the x-coordinate
	 * @param mouseY the y-coordinate
	 * @return the item at the position, or null if none or out of bounds
	 */
	public Item itemAtPixel(int mouseX, int mouseY) {
		Position gridPos = pixelToGridPosition(mouseX, mouseY);
		if (gridPos == null) {
			return null;
		}

		return backpack.itemAt(gridPos.row(), gridPos.col());
	}

	/**
	 * Removes an item from the staging area.
	 * 
	 * @param item the item to remove
	 * @throws NullPointerException if item is null
	 */
	public void removeFromStaging(Item item) {
		Objects.requireNonNull(item);
		stagedItems.removeIf(staged -> staged.getItem() == item);
	}

	/**
	 * Gets a copy of the list of staged items.
	 * 
	 * @return a new list containing all staged items
	 */
	public List<StagedItem> getStagedItems() {
		return new ArrayList<>(stagedItems);
	}

	/**
	 * Clears all items from the staging area.
	 */
	public void clearStaging() {
		stagedItems.clear();
	}

	/**
	 * Gets the total width of the backpack view in pixels.
	 * 
	 * @return the width in pixels
	 */
	public int getWidth() {
		return backpack.width() * (itemSize + 5);
	}

	/**
	 * Gets the total height of the backpack view in pixels.
	 * 
	 * @return the height in pixels
	 */
	public int getHeight() {
		return backpack.height() * (itemSize + 5);
	}

	/**
	 * Gets the x-coordinate of the backpack view.
	 * 
	 * @return the x-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y-coordinate of the backpack view.
	 * 
	 * @return the y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the size of each cell in pixels.
	 * 
	 * @return the item size
	 */
	public int getItemSize() {
		return itemSize;
	}

	/**
	 * Gets the currently selected item (dragged item or item under mouse).
	 * 
	 * @param mouseX the x position of the mouse
	 * @param mouseY the y position of the mouse
	 * @return the selected item, or null if none
	 */
	public Item getSelectedItem(int mouseX, int mouseY) {
		if (draggedItem != null) {
			return draggedItem;
		}

		for (StagedItem staged : stagedItems) {
			if (staged.contains(mouseX, mouseY, itemSize)) {
				return staged.getItem();
			}
		}

		return itemAtPixel(mouseX, mouseY);
	}

	/**
	 * Merges all staged gold items by calling the provided merger function. Removes
	 * the staged gold items after merging. This eliminates code duplication between
	 * TreasureScreen and RewardScreen.
	 * 
	 * @param goldMerger function that accepts gold amount to merge (typically
	 *                   gameData::addGoldToBackpack)
	 * @throws NullPointerException if goldMerger is null
	 */
	public void mergeAllStagedGold(java.util.function.IntConsumer goldMerger) {
		Objects.requireNonNull(goldMerger);

		var stagedItemsCopy = new ArrayList<>(stagedItems);
		for (var staged : stagedItemsCopy) {
			switch (staged.getItem()) {
			case Gold stagedGold -> {
				goldMerger.accept(stagedGold.purse());
				removeFromStaging(staged.getItem());
			}
			default -> {
			}
			}
		}
	}

	/**
	 * Converts pixel coordinates to grid position. Helper method to centralize the
	 * pixel-to-grid conversion logic.
	 * 
	 * @param mouseX the x-coordinate in pixels
	 * @param mouseY the y-coordinate in pixels
	 * @return the grid position, or null if outside the backpack area
	 */
	private Position pixelToGridPosition(int mouseX, int mouseY) {
		if (!contains(mouseX, mouseY)) {
			return null;
		}

		int col = (mouseX - x) / (itemSize + 5);
		int row = (mouseY - y) / (itemSize + 5);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			return new Position(row, col);
		}

		return null;
	}
}