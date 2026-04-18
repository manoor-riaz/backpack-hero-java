package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.uge.backpackhero.*;
import fr.uge.backpackhero.Dimension;

/**
 * Screen displayed when the hero receives a curse from an enemy. The player can
 * choose to accept the curse (place it in backpack) or refuse (take damage).
 */
public class CurseScreen implements Screen {
	private final Hero hero;
	private final Curses curse;
	private final BufferedImage buttonImage;
	private final int returnScreenIndex;

	private Button acceptButton;
	private Button refuseButton;
	private Button backButton;

	private boolean placingCurse = false;
	private int cursePreviewRow = -1;
	private int cursePreviewCol = -1;

	private int backpackX;
	private int backpackY;
	private int cellSize = 60;
	private int cellGap = 5;

	private String message = "";
	private Color messageColor = Color.WHITE;

	private boolean completed = false;

	/**
	 * Creates a new curse screen.
	 * 
	 * @param hero              the hero receiving the curse
	 * @param curse             the curse to handle
	 * @param buttonImage       the button image
	 * @param returnScreenIndex the index of the screen to return to
	 * @throws NullPointerException if hero or curse is null
	 */
	public CurseScreen(Hero hero, Curses curse, BufferedImage buttonImage, int returnScreenIndex) {
		this.hero = Objects.requireNonNull(hero);
		this.curse = Objects.requireNonNull(curse);
		this.buttonImage = buttonImage;
		this.returnScreenIndex = returnScreenIndex;
	}

	/**
	 * Creates a new curse screen with a callback (compatibility constructor).
	 * 
	 * @param hero        the hero receiving the curse
	 * @param curse       the curse to handle
	 * @param buttonImage the button image
	 * @param onComplete  the completion callback (ignored)
	 * @throws NullPointerException if hero or curse is null
	 */
	public CurseScreen(Hero hero, Curses curse, BufferedImage buttonImage, Runnable onComplete) {
		this(hero, curse, buttonImage, -1);
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics);

		drawBackground(graphics, width, height);
		drawTitle(graphics, width);
		drawCurseName(graphics, width);
		drawCurseDimensions(graphics, width);

		if (!placingCurse) {
			drawChoiceScreen(graphics, width, height);
		} else {
			drawPlacementScreen(graphics, width, height);
		}

		drawMessage(graphics, width, height);
		drawHeroStats(graphics, width, height);
	}

	/**
	 * Draws the background of the screen.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(new Color(20, 10, 30));
		graphics.fillRect(0, 0, width, height);
	}

	/**
	 * Draws the title of the screen.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawTitle(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.BOLD, 28));
		graphics.setColor(new Color(40, 40, 40));
		String title = "CURSE RECEIVED!";
		int titleWidth = graphics.getFontMetrics().stringWidth(title);
		graphics.drawString(title, (width - titleWidth) / 2, 50);
	}

	/**
	 * Draws the curse name.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawCurseName(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.BOLD, 20));
		graphics.setColor(new Color(200, 100, 200));
		String curseName = curse.name() + " (" + curse.rarity() + ")";
		int nameWidth = graphics.getFontMetrics().stringWidth(curseName);
		graphics.drawString(curseName, (width - nameWidth) / 2, 85);
	}

	/**
	 * Draws the curse dimensions.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawCurseDimensions(Graphics2D graphics, int width) {
		Dimension dim = curse.dimension();
		graphics.setFont(new Font("Arial", Font.PLAIN, 16));
		graphics.setColor(Color.LIGHT_GRAY);
		String sizeInfo = "Size: " + dim.height() + "x" + dim.width() + " cells";
		int sizeWidth = graphics.getFontMetrics().stringWidth(sizeInfo);
		graphics.drawString(sizeInfo, (width - sizeWidth) / 2, 110);
	}

	/**
	 * Draws the message at the bottom.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawMessage(Graphics2D graphics, int width, int height) {
		if (!message.isEmpty()) {
			graphics.setFont(new Font("Arial", Font.BOLD, 18));
			graphics.setColor(messageColor);
			int msgWidth = graphics.getFontMetrics().stringWidth(message);
			graphics.drawString(message, (width - msgWidth) / 2, height - 30);
		}
	}

	/**
	 * Draws the choice screen.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawChoiceScreen(Graphics2D graphics, int width, int height) {
		drawExplanations(graphics, width);
		drawHealerInfo(graphics, width);
		drawCursePreview(graphics, width / 2 - 60, 340, 40);
		drawChoiceButtons(graphics, width, height);
	}

	/**
	 * Draws the explanations text.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawExplanations(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.PLAIN, 16));
		graphics.setColor(Color.WHITE);

		String[] explanations = { "You have received a curse!", "Choose:",
				"ACCEPT: Place the curse in your bag (destroys items underneath)",
				"REFUSE: Take " + hero.nextCurseRefusalDamage() + " point(s) of damage" };

		int y = 160;
		for (String line : explanations) {
			int lineWidth = graphics.getFontMetrics().stringWidth(line);
			graphics.drawString(line, (width - lineWidth) / 2, y);
			y += 25;
		}
	}

	/**
	 * Draws the healer info hint.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawHealerInfo(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.ITALIC, 14));
		graphics.setColor(new Color(100, 255, 150));
		String healerInfo = "Tip: The healer can remove curses for gold";
		int healerInfoWidth = graphics.getFontMetrics().stringWidth(healerInfo);
		graphics.drawString(healerInfo, (width - healerInfoWidth) / 2, 275);
	}

	/**
	 * Draws the choice buttons (accept/refuse).
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawChoiceButtons(Graphics2D graphics, int width, int height) {
		int btnWidth = 200;
		int btnHeight = 50;
		int btnY = height - 150;
		int spacing = 50;

		int acceptX = width / 2 - btnWidth - spacing / 2;
		if (acceptButton == null) {
			acceptButton = new Button(acceptX, btnY, btnWidth, btnHeight, buttonImage);
		}
		acceptButton.draw(graphics);
		drawButtonText(graphics, acceptX, btnY, btnWidth, btnHeight, "ACCEPT", new Color(100, 200, 100));

		int refuseX = width / 2 + spacing / 2;
		if (refuseButton == null) {
			refuseButton = new Button(refuseX, btnY, btnWidth, btnHeight, buttonImage);
		}
		refuseButton.draw(graphics);
		String refuseText = "REFUSE (-" + hero.nextCurseRefusalDamage() + " HP)";
		drawButtonText(graphics, refuseX, btnY, btnWidth, btnHeight, refuseText, new Color(200, 100, 100));
	}

	/**
	 * Draws text on a button.
	 * 
	 * @param graphics the graphics context
	 * @param x        the button x position
	 * @param y        the button y position
	 * @param width    the button width
	 * @param height   the button height
	 * @param text     the text to draw
	 * @param color    the text color
	 */
	private void drawButtonText(Graphics2D graphics, int x, int y, int width, int height, String text, Color color) {
		graphics.setFont(new Font("Arial", Font.BOLD, 16));
		graphics.setColor(color);
		int textWidth = graphics.getFontMetrics().stringWidth(text);
		graphics.drawString(text, x + (width - textWidth) / 2, y + 32);
	}

	/**
	 * Draws the placement screen.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawPlacementScreen(Graphics2D graphics, int width, int height) {
		drawInstructions(graphics, width);
		drawBackpack(graphics, width, height);
		drawCurseOverlayIfValid(graphics);
		drawBackButton(graphics, width, height);
	}

	/**
	 * Draws the placement instructions.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 */
	private void drawInstructions(Graphics2D graphics, int width) {
		graphics.setFont(new Font("Arial", Font.BOLD, 16));
		graphics.setColor(new Color(255, 215, 0));
		String instruction = "Click on a cell to place the curse";
		int instrWidth = graphics.getFontMetrics().stringWidth(instruction);
		graphics.drawString(instruction, (width - instrWidth) / 2, 140);

		graphics.setFont(new Font("Arial", Font.PLAIN, 14));
		graphics.setColor(Color.LIGHT_GRAY);
		String subInstruction = "(Items on these cells will be DESTROYED)";
		int subWidth = graphics.getFontMetrics().stringWidth(subInstruction);
		graphics.drawString(subInstruction, (width - subWidth) / 2, 160);
	}

	/**
	 * Draws the backpack grid.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackpack(Graphics2D graphics, int width, int height) {
		BackPack backpack = hero.backpack();
		int totalWidth = backpack.width() * (cellSize + cellGap) - cellGap;

		backpackX = (width - totalWidth) / 2;
		backpackY = 200;

		for (int row = 0; row < backpack.height(); row++) {
			for (int col = 0; col < backpack.width(); col++) {
				drawBackpackCell(graphics, backpack, row, col);
			}
		}
	}

	/**
	 * Draws a single backpack cell.
	 * 
	 * @param graphics the graphics context
	 * @param backpack the backpack
	 * @param row      the row index
	 * @param col      the column index
	 */
	private void drawBackpackCell(Graphics2D graphics, BackPack backpack, int row, int col) {
		int cellX = backpackX + col * (cellSize + cellGap);
		int cellY = backpackY + row * (cellSize + cellGap);

		boolean unlocked = backpack.isCellUnlocked(row, col);
		Item itemAtCell = backpack.itemAt(row, col);

		Color bgColor = determineCellColor(unlocked, itemAtCell);
		graphics.setColor(bgColor);
		graphics.fillRect(cellX, cellY, cellSize, cellSize);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(cellX, cellY, cellSize, cellSize);

		if (itemAtCell != null && unlocked) {
			drawItemName(graphics, itemAtCell, cellX, cellY);
		}

		if (!unlocked) {
			drawLockedIndicator(graphics, cellX, cellY);
		}
	}

	/**
	 * Determines the background color of a cell.
	 * 
	 * @param unlocked   whether the cell is unlocked
	 * @param itemAtCell the item at the cell
	 * @return the background color
	 */
	private Color determineCellColor(boolean unlocked, Item itemAtCell) {
		if (!unlocked) {
			return new Color(60, 60, 60);
		} else if (itemAtCell != null) {
			if (itemAtCell.isGold()) {
				return new Color(200, 180, 50);
			} else if (itemAtCell.isCurse()) {
				return new Color(20, 20, 20);
			} else {
				return new Color(100, 150, 200);
			}
		} else {
			return new Color(150, 150, 150);
		}
	}

	/**
	 * Draws the item name on a cell.
	 * 
	 * @param graphics the graphics context
	 * @param item     the item
	 * @param cellX    the cell x position
	 * @param cellY    the cell y position
	 */
	private void drawItemName(Graphics2D graphics, Item item, int cellX, int cellY) {
		graphics.setFont(new Font("Arial", Font.PLAIN, 10));
		graphics.setColor(Color.WHITE);
		String name = item.name();
		if (name.length() > 8) {
			name = name.substring(0, 7) + "..";
		}
		graphics.drawString(name, cellX + 3, cellY + cellSize - 5);
	}

	/**
	 * Draws a locked indicator on a cell.
	 * 
	 * @param graphics the graphics context
	 * @param cellX    the cell x position
	 * @param cellY    the cell y position
	 */
	private void drawLockedIndicator(Graphics2D graphics, int cellX, int cellY) {
		graphics.setColor(Color.RED);
		graphics.setFont(new Font("Arial", Font.BOLD, 20));
		graphics.drawString("X", cellX + cellSize / 2 - 7, cellY + cellSize / 2 + 7);
	}

	/**
	 * Draws the curse overlay if the preview position is valid.
	 * 
	 * @param graphics the graphics context
	 */
	private void drawCurseOverlayIfValid(Graphics2D graphics) {
		if (cursePreviewRow >= 0 && cursePreviewCol >= 0) {
			drawCurseOverlay(graphics, hero.backpack());
		}
	}

	/**
	 * Draws the curse overlay on the backpack.
	 * 
	 * @param graphics the graphics context
	 * @param backpack the backpack
	 */
	private void drawCurseOverlay(Graphics2D graphics, BackPack backpack) {
		boolean canPlace = backpack.canPlaceCurse(curse, cursePreviewRow, cursePreviewCol);
		var toDestroy = backpack.itemsDestroyedByCurse(curse, new Position(cursePreviewRow, cursePreviewCol));

		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

		for (var cell : curse.shape().turnItem(Orientation.ORIENTATION_0)) {
			int row = cursePreviewRow + cell.row();
			int col = cursePreviewCol + cell.col();

			int cellX = backpackX + col * (cellSize + cellGap);
			int cellY = backpackY + row * (cellSize + cellGap);

			Color overlayColor = canPlace ? new Color(40, 40, 40) : new Color(255, 50, 50);
			graphics.setColor(overlayColor);
			graphics.fillRect(cellX, cellY, cellSize, cellSize);
		}

		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

		if (canPlace && !toDestroy.isEmpty()) {
			drawDestroyedItemsMessage(graphics, toDestroy);
		}
	}

	/**
	 * Draws the message about destroyed items.
	 * 
	 * @param graphics  the graphics context
	 * @param toDestroy the list of items to be destroyed
	 */
	private void drawDestroyedItemsMessage(Graphics2D graphics, java.util.List<Item> toDestroy) {
		graphics.setFont(new Font("Arial", Font.BOLD, 14));
		graphics.setColor(new Color(255, 100, 100));

		StringBuilder destroyMsg = new StringBuilder("Items destroyed: ");
		for (int i = 0; i < toDestroy.size(); i++) {
			if (i > 0)
				destroyMsg.append(", ");
			destroyMsg.append(toDestroy.get(i).name());
		}

		int msgWidth = graphics.getFontMetrics().stringWidth(destroyMsg.toString());
		int msgX = Math.max(10, (int) ((backpackX + backpackX + hero.backpack().width() * (cellSize + cellGap)) / 2.0
				- msgWidth / 2.0));
		graphics.drawString(destroyMsg.toString(), msgX,
				backpackY + hero.backpack().height() * (cellSize + cellGap) + 30);
	}

	/**
	 * Draws a curse preview.
	 * 
	 * @param graphics        the graphics context
	 * @param startX          the starting x position
	 * @param startY          the starting y position
	 * @param previewCellSize the size of each preview cell
	 */
	private void drawCursePreview(Graphics2D graphics, int startX, int startY, int previewCellSize) {
		graphics.setColor(new Color(40, 40, 40));

		for (var cell : curse.shape().turnItem(Orientation.ORIENTATION_0)) {
			int cellX = startX + cell.col() * (previewCellSize + 2);
			int cellY = startY + cell.row() * (previewCellSize + 2);

			graphics.fillRect(cellX, cellY, previewCellSize, previewCellSize);
			graphics.setColor(Color.BLACK);
			graphics.drawRect(cellX, cellY, previewCellSize, previewCellSize);
			graphics.setColor(new Color(40, 40, 40));
		}
	}

	/**
	 * Draws the back button.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawBackButton(Graphics2D graphics, int width, int height) {
		int btnWidth = 200;
		int btnHeight = 50;
		int btnX = Math.max(20, width / 50);
		int btnY = height - btnHeight - Math.max(20, height / 50);

		if (backButton == null || backButton.getX() != btnX || backButton.getY() != btnY) {
			backButton = new Button(btnX, btnY, btnWidth, btnHeight, buttonImage);
		}
		backButton.draw(graphics);

		graphics.setFont(new Font("Arial", Font.BOLD, 16));
		graphics.setColor(new Color(255, 150, 150));
		String backText = "BACK (Refuse)";
		int backTextWidth = graphics.getFontMetrics().stringWidth(backText);
		graphics.drawString(backText, btnX + (btnWidth - backTextWidth) / 2, btnY + 32);
	}

	/**
	 * Draws the hero stats.
	 * 
	 * @param graphics the graphics context
	 * @param width    the screen width
	 * @param height   the screen height
	 */
	private void drawHeroStats(Graphics2D graphics, int width, int height) {
		graphics.setFont(new Font("Arial", Font.BOLD, 14));
		graphics.setColor(Color.WHITE);

		int statsX = 20;
		int statsY = height - 100;

		graphics.drawString("HP: " + hero.hp() + "/" + hero.maxhp(), statsX, statsY);
		graphics.drawString("Shield: " + hero.shieldpoint(), statsX, statsY + 20);
		graphics.drawString("Refusals: " + hero.curseNumberRefused(), statsX, statsY + 40);
	}

	/**
	 * Returns to the combat screen.
	 * 
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	private void returnToCombat(ScreenManager manager) {
		Objects.requireNonNull(manager);
		if (completed) {
			return;
		}
		completed = true;

		int destinationIndex = returnScreenIndex >= 0 ? returnScreenIndex : manager.getCurrentScreenIndex() - 1;

		while (manager.getScreenCount() > destinationIndex + 1) {
			manager.removeLastScreen();
		}

		if (destinationIndex >= 0 && destinationIndex < manager.getScreenCount()) {
			manager.goToScreen(destinationIndex);
		}
	}

	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		if (completed) {
			return;
		}

		if (!placingCurse) {
			handleChoiceClick(x, y, manager);
		} else {
			handlePlacementClick(x, y, manager);
		}
	}

	/**
	 * Handles clicks in choice mode.
	 * 
	 * @param x       the x coordinate
	 * @param y       the y coordinate
	 * @param manager the screen manager
	 */
	private void handleChoiceClick(int x, int y, ScreenManager manager) {
		if (acceptButton != null && acceptButton.contains(x, y)) {
			placingCurse = true;
			message = "";
			return;
		}

		if (refuseButton != null && refuseButton.contains(x, y)) {
			handleRefusal(manager);
		}
	}

	/**
	 * Handles curse refusal.
	 * 
	 * @param manager the screen manager
	 */
	private void handleRefusal(ScreenManager manager) {
		int damage = hero.refuseCurse();
		message = "You refused and took " + damage + " damage!";
		messageColor = new Color(255, 100, 100);
		returnToCombat(manager);
	}

	/**
	 * Handles clicks in placement mode.
	 * 
	 * @param x       the x coordinate
	 * @param y       the y coordinate
	 * @param manager the screen manager
	 */
	private void handlePlacementClick(int x, int y, ScreenManager manager) {
		if (backButton != null && backButton.contains(x, y)) {
			placingCurse = false;
			cursePreviewRow = -1;
			cursePreviewCol = -1;
			message = "";
			return;
		}

		BackPack backpack = hero.backpack();

		int col = (x - backpackX) / (cellSize + cellGap);
		int row = (y - backpackY) / (cellSize + cellGap);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			handleCellClick(row, col, backpack, manager);
		}
	}

	/**
	 * Handles clicking on a backpack cell.
	 * 
	 * @param row      the row index
	 * @param col      the column index
	 * @param backpack the backpack
	 * @param manager  the screen manager
	 */
	private void handleCellClick(int row, int col, BackPack backpack, ScreenManager manager) {
		if (backpack.canPlaceCurse(curse, row, col)) {
			var destroyed = backpack.placeCurse(curse, new Position(row, col));

			if (destroyed != null) {
				String msg = destroyed.isEmpty() ? "Curse placed!"
						: "Curse placed! " + destroyed.size() + " item(s) destroyed";
				message = msg;
				messageColor = Color.WHITE;
				returnToCombat(manager);
			}
		} else {
			handleInvalidPlacement(row, col, backpack);
		}
	}

	/**
	 * Handles invalid curse placement.
	 * 
	 * @param row      the row index
	 * @param col      the column index
	 * @param backpack the backpack
	 */
	private void handleInvalidPlacement(int row, int col, BackPack backpack) {
		boolean hasCurseBlocking = false;
		for (var cell : curse.shape().turnItem(Orientation.ORIENTATION_0)) {
			int checkRow = row + cell.row();
			int checkCol = col + cell.col();
			if (checkRow >= 0 && checkRow < backpack.height() && checkCol >= 0 && checkCol < backpack.width()) {
				var item = backpack.itemAt(checkRow, checkCol);
				if (item != null && item.isCurse()) {
					hasCurseBlocking = true;
					break;
				}
			}
		}

		message = hasCurseBlocking ? "Impossible! Curses cannot overlap" : "Cannot place here!";
		messageColor = new Color(255, 100, 100);
	}

	@Override
	public void handleMouseMove(int x, int y) {
		if (placingCurse && !completed) {
			updateCursePreview(x, y);
		}
	}

	/**
	 * Updates the curse preview position.
	 * 
	 * @param x the mouse x position
	 * @param y the mouse y position
	 */
	private void updateCursePreview(int x, int y) {
		BackPack backpack = hero.backpack();

		int col = (x - backpackX) / (cellSize + cellGap);
		int row = (y - backpackY) / (cellSize + cellGap);

		if (row >= 0 && row < backpack.height() && col >= 0 && col < backpack.width()) {
			cursePreviewRow = row;
			cursePreviewCol = col;
		} else {
			cursePreviewRow = -1;
			cursePreviewCol = -1;
		}
	}

	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);
	}

	@Override
	public void handleRotate(int x, int y) {
		if (placingCurse && !completed) {
			message = "Curses cannot be rotated!";
			messageColor = new Color(255, 200, 100);
		}
	}

	@Override
	public void handleRightClick(int x, int y) {
		if (placingCurse && !completed) {
			placingCurse = false;
			cursePreviewRow = -1;
			cursePreviewCol = -1;
			message = "";
		}
	}
}