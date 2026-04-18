package fr.uge.graphic;

import java.awt.Graphics2D;

/**
 * Interface for all game screens. Defines methods for drawing and handling user
 * input.
 */
public interface Screen {
	/**
	 * Draws the screen content.
	 *
	 * @param graphics : the graphics context
	 * @param width    : the screen width
	 * @param height   : the screen height
	 */
	void draw(Graphics2D graphics, int width, int height);

	/**
	 * Handles mouse clicks (mouse button press).
	 *
	 * @param x       : the x coordinate of the click
	 * @param y       : the y coordinate of the click
	 * @param manager : the screen manager
	 */
	void handleClick(int x, int y, ScreenManager manager);

	/**
	 * Handles mouse movement (for dragging items).
	 *
	 * @param x : the current x coordinate
	 * @param y : the current y coordinate
	 */
	default void handleMouseMove(int x, int y) {

	}

	/**
	 * Handles mouse button release (to place items).
	 *
	 * @param x       : the x coordinate where mouse was released
	 * @param y       : the y coordinate where mouse was released
	 * @param manager : the screen manager
	 */
	default void handleMouseRelease(int x, int y, ScreenManager manager) {

	}

	/**
	 * Handles right-click (for double-click alternative: extract item from
	 * backpack).
	 *
	 * @param x : the x coordinate of the click
	 * @param y : the y coordinate of the click
	 */
	default void handleRightClick(int x, int y) {

	}

	/**
	 * Handles the R key press (for rotation).
	 *
	 * @param x : the x position of the mouse
	 * @param y : the y position of the mouse
	 */
	default void handleRotate(int x, int y) {

	}

	/**
	 * Handles a key press.
	 *
	 * @param key     : the character of the key pressed
	 * @param x       : the x position of the mouse
	 * @param y       : the y position of the mouse
	 * @param manager : the screen manager
	 */
	default void handleKeyPress(char key, int x, int y, ScreenManager manager) {

	}
}