package fr.uge.graphic;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.HallOfFame;

/**
 * Manages multiple screens and handles transitions between them. Also stores
 * the Hall of Fame for the game and tracks mouse position for input handling.
 */
public class ScreenManager {
	private final List<Screen> screens = new ArrayList<>();
	private int currentScreenIndex = 0;
	private ScreenInfo screenInfo;

	private int lastMouseX = 0;
	private int lastMouseY = 0;

	private HallOfFame hallOfFame;

	/**
	 * Constructs a new ScreenManager with an empty screen list.
	 */
	public ScreenManager() {
	}

	/**
	 * Sets the screen dimensions.
	 *
	 * @param width  the width of the screen
	 * @param height the height of the screen
	 * @throws IllegalArgumentException if width or height is negative
	 */
	public void setScreenInfo(int width, int height) {
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}
		this.screenInfo = new ScreenInfo(width, height);
	}

	/**
	 * Gets the current screen dimensions.
	 *
	 * @return the screen info, or null if not set
	 */
	public ScreenInfo getScreenInfo() {
		return screenInfo;
	}

	/**
	 * Sets the Hall of Fame for the game.
	 * 
	 * @param hallOfFame the Hall of Fame to use
	 * @throws NullPointerException if hallOfFame is null
	 */
	public void setHallOfFame(HallOfFame hallOfFame) {
		this.hallOfFame = Objects.requireNonNull(hallOfFame, "hallOfFame cannot be null");
	}

	/**
	 * Returns the Hall of Fame.
	 * 
	 * @return the Hall of Fame, or null if not set
	 */
	public HallOfFame getHallOfFame() {
		return hallOfFame;
	}

	/**
	 * Returns the current screen being displayed.
	 * 
	 * @return the current screen, or null if no screens exist
	 */
	public Screen getCurrentScreen() {
		if (screens.isEmpty()) {
			return null;
		}
		return screens.get(currentScreenIndex);
	}

	/**
	 * Adds a screen to the manager's screen list.
	 *
	 * @param screen the screen to add
	 * @throws NullPointerException if screen is null
	 */
	public void addScreen(Screen screen) {
		Objects.requireNonNull(screen, "screen cannot be null");
		screens.add(screen);
	}

	/**
	 * Advances to the next screen if one exists.
	 */
	public void nextScreen() {
		if (currentScreenIndex < screens.size() - 1) {
			currentScreenIndex++;
		}
	}

	/**
	 * Goes back to the previous screen if one exists.
	 */
	public void previousScreen() {
		if (currentScreenIndex > 0) {
			currentScreenIndex--;
		}
	}

	/**
	 * Switches to the screen at the specified index.
	 *
	 * @param index the index of the screen to display
	 * @throws IllegalArgumentException if index is negative or >= screen count
	 */
	public void goToScreen(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index cannot be negative");
		}
		if (index >= screens.size()) {
			throw new IllegalArgumentException("index cannot be >= screen count");
		}
		currentScreenIndex = index;
	}

	/**
	 * Draws the current screen.
	 *
	 * @param graphics the graphics context to draw on
	 * @param width    the width of the drawing area
	 * @param height   the height of the drawing area
	 * @throws NullPointerException     if graphics is null
	 * @throws IllegalArgumentException if width or height is negative
	 */
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics, "graphics cannot be null");
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}

		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).draw(graphics, width, height);
		}
	}

	/**
	 * Forwards a mouse click event to the current screen.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	public void handleClick(int x, int y) {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleClick(x, y, this);
		}
	}

	/**
	 * Forwards a mouse move event to the current screen.
	 *
	 * @param x the X coordinate of the mouse
	 * @param y the Y coordinate of the mouse
	 */
	public void handleMouseMove(int x, int y) {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleMouseMove(x, y);
		}
	}

	/**
	 * Forwards a mouse release event to the current screen.
	 *
	 * @param x the X coordinate of the release
	 * @param y the Y coordinate of the release
	 */
	public void handleMouseRelease(int x, int y) {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleMouseRelease(x, y, this);
		}
	}

	/**
	 * Forwards a right-click event to the current screen.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	public void handleRightClick(int x, int y) {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleRightClick(x, y);
		}
	}

	/**
	 * Stores the last known mouse position for use with keyboard rotation.
	 *
	 * @param x the X coordinate of the mouse
	 * @param y the Y coordinate of the mouse
	 */
	public void setLastMousePosition(int x, int y) {
		this.lastMouseX = x;
		this.lastMouseY = y;
	}

	/**
	 * Forwards a rotation event to the current screen using the last mouse
	 * position.
	 */
	public void handleRotate() {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleRotate(lastMouseX, lastMouseY);
		}
	}

	/**
	 * Forwards a key press event to the current screen.
	 * 
	 * @param key the character of the key pressed
	 */
	public void handleKeyPress(char key) {
		if (!screens.isEmpty()) {
			screens.get(currentScreenIndex).handleKeyPress(key, lastMouseX, lastMouseY, this);
		}
	}

	/**
	 * Returns the total number of screens managed.
	 *
	 * @return the number of screens
	 */
	public int getScreenCount() {
		return screens.size();
	}

	/**
	 * Returns the index of the currently displayed screen.
	 * 
	 * @return the current screen index
	 */
	public int getCurrentScreenIndex() {
		return currentScreenIndex;
	}

	/**
	 * Removes the screen at the specified index and adjusts the current index if
	 * needed.
	 * 
	 * @param index the index of the screen to remove
	 * @return true if the screen was removed, false if index is invalid
	 */
	public boolean removeScreen(int index) {
		if (index < 0 || index >= screens.size()) {
			return false;
		}
		screens.remove(index);
		adjustCurrentIndexAfterRemoval();
		return true;
	}

	/**
	 * Adjusts the current screen index after a screen removal to ensure it remains
	 * valid.
	 */
	private void adjustCurrentIndexAfterRemoval() {
		if (currentScreenIndex >= screens.size()) {
			currentScreenIndex = screens.size() - 1;
		}
		if (currentScreenIndex < 0) {
			currentScreenIndex = 0;
		}
	}

	/**
	 * Removes the last screen from the list.
	 * 
	 * @return true if a screen was removed, false if the list was empty
	 */
	public boolean removeLastScreen() {
		if (screens.isEmpty()) {
			return false;
		}
		return removeScreen(screens.size() - 1);
	}
}